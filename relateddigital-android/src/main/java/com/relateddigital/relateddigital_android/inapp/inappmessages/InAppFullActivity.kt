package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.databinding.ActivityInAppFullBinding
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppNotificationType
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso

class InAppFullActivity : Activity(), IVisilabs {
    private var mInApp: InAppMessage? = null
    private var mUpdateDisplayState: InAppUpdateDisplayState? = null
    private var mIntentId = -1
    private lateinit var binding: ActivityInAppFullBinding
    private var mIsRotation = false
    private var player: ExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInAppFullBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        mIntentId = savedInstanceState?.getInt(INTENT_ID_KEY, Int.MAX_VALUE)
            ?: intent.getIntExtra(INTENT_ID_KEY, Int.MAX_VALUE)
        mUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(mIntentId)
        if (mUpdateDisplayState == null) {
            Log.e(
                "Visilabs",
                "VisilabsNotificationActivity intent received, but nothing was found to show."
            )
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
            return
        }
        if (isShowingInApp) {
            setUpView()
        } else {
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INTENT_ID_KEY, mIntentId)
        mIsRotation = true
    }

    override fun setUpView() {
        val inAppNotificationState: InAppNotificationState? =
            mUpdateDisplayState!!.getDisplayState() as InAppNotificationState?
        if (inAppNotificationState != null) {
            mInApp = inAppNotificationState.getInAppMessage()
            if (mInApp != null) {
                setInAppData()
                setPromotionCode()
                clickEvents()
            } else {
                Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
        } else {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        }
    }

    private fun setInAppData() {
        binding.tvInAppTitle.text = mInApp!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
        binding.tvInAppSubtitle.text = mInApp!!.mActionData!!.mMsgBody!!.replace("\\n", "\n")
        if (!mInApp!!.mActionData!!.mBtnText.isNullOrEmpty()) {
            binding.btnInApp.text = mInApp!!.mActionData!!.mBtnText
        }
        if (!mInApp!!.mActionData!!.mImg.isNullOrEmpty()) {
            binding.fivInAppImage.visibility = View.VISIBLE
            binding.fullVideoView.visibility = View.GONE
            binding.webViewFull?.visibility = View.GONE
            if (AppUtils.isAnImage(mInApp!!.mActionData!!.mImg)) {
                Picasso.get().load(mInApp!!.mActionData!!.mImg).into(binding.fivInAppImage)
            } else {
                Glide.with(this)
                    .load(mInApp!!.mActionData!!.mImg)
                    .into(binding.fivInAppImage)
            }
        } else {
            binding.fivInAppImage.visibility = View.GONE
            if (!mInApp!!.mActionData!!.mVideoUrl.isNullOrEmpty()) {
                if (mInApp!!.mActionData!!.mVideoUrl!!.toLowerCase().contains("youtube.com") || mInApp!!.mActionData!!.mVideoUrl!!.toLowerCase().contains("youtu.be")) {
                    binding.fullVideoView.visibility = View.GONE
                    binding.webViewFull!!.visibility = View.VISIBLE
                    setYoutubeVideo()
                }
                else {
                    binding.webViewFull!!.visibility = View.GONE
                    binding.fullVideoView.visibility = View.VISIBLE
                    initializePlayer()
                    startPlayer()
                }
            } else {
                binding.fullVideoView.visibility = View.GONE
                releasePlayer()
            }
        }
    }
    private fun setYoutubeVideo() {

        val webSettings: WebSettings = binding.webViewFull!!.settings
        if (!mInApp!!.mActionData!!.mBackground.isNullOrEmpty()) {
            binding.webViewFull!!.setBackgroundColor(Color.parseColor(mInApp!!.mActionData!!.mBackground))

        }
        else binding.webViewFull!!.setBackgroundColor(getResources().getColor(R.color.black))
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.builtInZoomControls = false
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webViewFull!!.settings.mediaPlaybackRequiresUserGesture = false
        binding.webViewFull!!.settings.allowContentAccess = true
        binding.webViewFull!!.settings.allowFileAccess = true
        binding.webViewFull!!.settings.allowFileAccessFromFileURLs = true
        binding.webViewFull!!.settings.allowUniversalAccessFromFileURLs = true

        binding.webViewFull!!.webChromeClient = WebChromeClient()
        var urlString =mInApp!!.mActionData!!.mVideoUrl
        var videoId = extractVideoId(urlString)
        val html = """
        <style>
          .iframe-container iframe {
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
          }
        </style>
        <div class="iframe-container">
          <div id="player"></div>
        </div>
        <script>
          var tag = document.createElement('script');
          tag.src = "https://www.youtube.com/iframe_api";
          var firstScriptTag = document.getElementsByTagName('script')[0];
          firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
          var player;
          var isPlaying = true;
          function onYouTubeIframeAPIReady() {
            player = new YT.Player('player', {
              width: '100%',
              videoId: '$videoId',
              playerVars: { 'autoplay': 1, 'playsinline': 1 , 'rel': 0  },
              events: {
                'onReady': function(event) {
                  event.target.playVideo();
                }
              }
            });
          }
          function watchPlayingState() {
            if (isPlaying) {
              player.playVideo();
            } else {
              player.pauseVideo();
            }
          }
        </script>
    """


        binding.webViewFull!!.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)



    }

    private fun extractVideoId(videoUrl: String?): String? {
        var videoId: String? = null
        if (videoUrl != null && videoUrl.trim { it <= ' ' }.length > 0) {
            val split = videoUrl.split("v=".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (split.size > 1) {
                videoId = split[1]
                val ampersandPosition = videoId.indexOf('&')
                if (ampersandPosition != -1) {
                    videoId = videoId.substring(0, ampersandPosition)
                }
            }
        }
        return videoId
    }

    private fun setPromotionCode() {
        if (!StringUtils.isNullOrWhiteSpace(mInApp!!.mActionData!!.mPromotionCode)
            && !StringUtils.isNullOrWhiteSpace(mInApp!!.mActionData!!.mPromoCodeBackgroundColor)
            && !StringUtils.isNullOrWhiteSpace(mInApp!!.mActionData!!.mPromoCodeTextColor)
        ) {
            binding.llCouponContainer.visibility = View.VISIBLE
            binding.llCouponContainer.setBackgroundColor(Color.parseColor(mInApp!!.mActionData!!.mPromoCodeBackgroundColor))
            binding.tvCouponCode.text = mInApp!!.mActionData!!.mPromotionCode
            binding.tvCouponCode.setTextColor(Color.parseColor(mInApp!!.mActionData!!.mPromoCodeTextColor))
            binding.llCouponContainer.setOnClickListener {
                val clipboard =
                    applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    getString(R.string.coupon_code),
                    mInApp!!.mActionData!!.mPromotionCode
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.copied_to_clipboard),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            binding.llCouponContainer.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickEvents() {
        binding.btnInApp.setOnClickListener {
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            InAppNotificationClickRequest.createInAppNotificationClickRequest(applicationContext, mInApp, null)
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(mInApp!!.mActionData!!.mAndroidLnk)
            } else {
                if (!mInApp!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                    try {
                        if (!mInApp!!.mActionData!!.mPromotionCode.isNullOrEmpty()) {

                            val clipboard =
                                applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                getString(R.string.coupon_code),
                                mInApp!!.mActionData!!.mPromotionCode
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.copied_to_clipboard),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        val viewIntent = Intent(
                            Intent.ACTION_VIEW,
                            StringUtils.getURIfromUrlString(mInApp!!.mActionData!!.mAndroidLnk)
                        )
                        this@InAppFullActivity.startActivity(viewIntent)
                    } catch (e: ActivityNotFoundException) {
                        Log.i("Visilabs", "User doesn't have an activity for notification URI")
                    }
                }
            }

            finish()
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        }
        binding.btnInApp.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.setBackgroundResource(R.drawable.cta_button_highlight)
            } else {
                v.setBackgroundResource(R.drawable.cta_button)
            }
            false
        }
        binding.llClose.setOnClickListener {
            finish()
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        }
    }

    private val surveyState: InAppNotificationState
        get() = mUpdateDisplayState!!.getDisplayState() as InAppNotificationState
    private val isShowingSurvey: Boolean
        get() = if (null == mUpdateDisplayState) {
            false
        } else InAppNotificationState.TYPE == mUpdateDisplayState!!.getDisplayState()!!.type
    private val isShowingInApp: Boolean
        get() {
            return if (null == mUpdateDisplayState) {
                false
            } else InAppNotificationState.TYPE == mUpdateDisplayState!!.getDisplayState()!!.type
        }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.fullVideoView.player = player
        val mediaItem = MediaItem.fromUri(mInApp!!.mActionData!!.mVideoUrl!!)
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
    }

    private fun startPlayer() {
        player!!.playWhenReady = true
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    override fun onBackPressed() {
        if (isShowingInApp) {
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        if (mIsRotation) {
            mIsRotation = false
        } else {
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        }
    }

    companion object {
        const val INTENT_ID_KEY = "INTENT_ID_KEY"
        const val LOG_TAG = "InAppActivityFull"
    }
}