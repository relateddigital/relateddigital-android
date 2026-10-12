package com.relateddigital.relateddigital_android.inapp.halfscreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.fragment.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.widget.ImageView
import java.util.Locale
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentHalfScreenBinding
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest
import com.relateddigital.relateddigital_android.util.AppUtils
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [HalfScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalfScreenFragment : Fragment() {
    companion object{
        private const val LOG_TAG = "HalfScreenFragment"
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "stateIdKey"
        private const val ARG_PARAM2 = "inAppStateKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param stateId Parameter 1.
         * @param inAppState Parameter 2.
         * @return A new instance of fragment HalfScreenFragment.
         */
        fun newInstance(stateId: Int, inAppState: InAppNotificationState?): HalfScreenFragment {
            val fragment = HalfScreenFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, stateId)
            args.putParcelable(ARG_PARAM2, inAppState)
            fragment.arguments = args
            return fragment
        }
    }

    private var mStateId = 0
    private var mInAppState: InAppNotificationState? = null
    private var mInAppMessage: InAppMessage? = null
    private var mIsTop = false
    private lateinit var binding: FragmentHalfScreenBinding
    private var player: ExoPlayer? = null

    fun HalfScreenFragment() {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateId = requireArguments().getInt(ARG_PARAM1)
        mInAppState = requireArguments().getParcelable(ARG_PARAM2)
        if (mInAppState != null) {
            mInAppMessage = mInAppState!!.getInAppMessage()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHalfScreenBinding.inflate(inflater, container, false)
        val view: View = binding.root

        hideStatusBar()

        if (mInAppState != null) {
            if (mInAppMessage == null) {
                endFragment()
                Log.e(LOG_TAG, "Could not get the data, closing in app")
            } else {
                setupInitialView()
            }
        } else {
            endFragment()
            Log.e(LOG_TAG, "Could not get the data, closing in app")
        }
        return view
    }

    private fun setupInitialView() {
        mIsTop = mInAppMessage!!.mActionData!!.mPos.equals("top")
        if (mIsTop) {
            adjustTop()
        } else {
            adjustBottom()
        }
        setupCloseButton()
        setupPromotionCode()
    }

    private fun setupPromotionCode() {
        val promoCode = mInAppMessage!!.mActionData!!.mPromotionCode
        if (!promoCode.isNullOrEmpty()) {
            val bgColor = mInAppMessage!!.mActionData!!.mPromotionBackgroundColor ?: mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor
            val textColor = mInAppMessage!!.mActionData!!.mPromotionTextColor ?: mInAppMessage!!.mActionData!!.mPromoCodeTextColor

            if (mIsTop) {
                binding.topPromotionContainer.visibility = View.VISIBLE
                try {
                    if (!bgColor.isNullOrEmpty()) {
                        binding.topPromotionContainer.setBackgroundColor(Color.parseColor(bgColor))
                    }
                } catch (e: Exception) {}
                binding.topPromotionCodeText.text = promoCode
                try {
                    if (!textColor.isNullOrEmpty()) {
                        binding.topPromotionCodeText.setTextColor(Color.parseColor(textColor))
                    }
                } catch (e: Exception) {}
                binding.topPromotionCodeText.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 8
                binding.topPromotionCodeText.typeface = mInAppMessage!!.mActionData!!.getFontFamily(requireActivity())
                
                binding.topPromotionCopyButton.setOnClickListener {
                    copyToClipboard(promoCode, binding.topPromotionCopyButton)
                }
            } else {
                binding.botPromotionContainer.visibility = View.VISIBLE
                try {
                    if (!bgColor.isNullOrEmpty()) {
                        binding.botPromotionContainer.setBackgroundColor(Color.parseColor(bgColor))
                    }
                } catch (e: Exception) {}
                binding.botPromotionCodeText.text = promoCode
                try {
                    if (!textColor.isNullOrEmpty()) {
                        binding.botPromotionCodeText.setTextColor(Color.parseColor(textColor))
                    }
                } catch (e: Exception) {}
                binding.botPromotionCodeText.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 8
                binding.botPromotionCodeText.typeface = mInAppMessage!!.mActionData!!.getFontFamily(requireActivity())
                
                binding.botPromotionCopyButton.setOnClickListener {
                    copyToClipboard(promoCode, binding.botPromotionCopyButton)
                }
            }
        }
    }

    private fun copyToClipboard(text: String, button: ImageButton) {
        if (activity != null) {
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Promotion Code", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireActivity(), getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()

            button.setImageResource(R.drawable.checked_mark)
            Handler(Looper.getMainLooper()).postDelayed({
                button.setImageResource(R.drawable.content_copy_24)
            }, 2000)
        }
    }

    private fun adjustTop() {
        binding.topContentArea.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            InAppNotificationClickRequest.createInAppNotificationClickRequest(requireActivity(), mInAppMessage, null)
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val uri: Uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(LOG_TAG, "Can't parse notification URI, will not take any action", e)
                    }
                }
            }
            endFragment()
        }
        val bg = mInAppMessage!!.mActionData!!.mBackground
        if (!bg.isNullOrEmpty()) {
            try {
                binding.topContentArea.setBackgroundColor(Color.parseColor(bg))
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Could not parse background color", e)
            }
        }
        if (!mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.topTitleView.text = mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
            binding.topTitleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            binding.topTitleView.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 8
            binding.topTitleView.typeface = mInAppMessage!!.mActionData!!.getFontFamily(requireActivity())
        } else {
            binding.topTitleView.visibility = View.GONE
        }
        if (!mInAppMessage!!.mActionData!!.mImg.isNullOrEmpty()) {
            binding.topImageView.visibility = View.VISIBLE
            binding.topVideoView.visibility = View.GONE
            if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mImg)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mImg)
                    .into(binding.topImageView)
            } else {
                Glide.with(requireActivity())
                    .load(mInAppMessage!!.mActionData!!.mImg)
                    .into(binding.topImageView)
            }
        } else {
            binding.topImageView.visibility = View.GONE
            if(!mInAppMessage!!.mActionData!!.mVideoUrl.isNullOrEmpty()) {
                binding.topVideoView.visibility = View.VISIBLE
                initializePlayer()
                startPlayer()
            } else {
                binding.topVideoView.visibility = View.GONE
                releasePlayer()
            }
        }
        binding.halfScreenContainerBot.visibility = View.GONE
    }

    private fun adjustBottom() {
        binding.botContentArea.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            InAppNotificationClickRequest.createInAppNotificationClickRequest(requireActivity(), mInAppMessage, null)
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val uri: Uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(LOG_TAG, "Can't parse notification URI, will not take any action", e)
                    }
                }
            }
            endFragment()
        }
        val bg = mInAppMessage!!.mActionData!!.mBackground
        if (!bg.isNullOrEmpty()) {
            try {
                binding.botContentArea.setBackgroundColor(Color.parseColor(bg))
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Could not parse background color", e)
            }
        }
        if (!mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.botTitleView.text = mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
            binding.botTitleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            binding.botTitleView.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 8
            binding.botTitleView.typeface = mInAppMessage!!.mActionData!!.getFontFamily(requireActivity())
        } else {
            binding.botTitleView.visibility = View.GONE
        }
        if (!mInAppMessage!!.mActionData!!.mImg.isNullOrEmpty()) {
            binding.botImageView.visibility = View.VISIBLE
            binding.botVideoView.visibility = View.GONE
            if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mImg)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mImg)
                    .into(binding.botImageView)
            } else {
                Glide.with(requireActivity())
                    .load(mInAppMessage!!.mActionData!!.mImg)
                    .into(binding.botImageView)
            }
        } else {
            binding.botImageView.visibility = View.GONE
            if(!mInAppMessage!!.mActionData!!.mVideoUrl.isNullOrEmpty()) {
                binding.botVideoView.visibility = View.VISIBLE
                initializePlayer()
                startPlayer()
            } else {
                binding.botVideoView.visibility = View.GONE
                releasePlayer()
            }
        }
        binding.halfScreenContainerTop.visibility = View.GONE
    }

    private fun setupCloseButton() {
        val closeButton = if (mIsTop) binding.topCloseButton else binding.botCloseButton
        val closeColor = mInAppMessage?.mActionData?.mCloseButtonColor
        val isWhite = isWhiteColor(closeColor)

        val bgColor = if (isWhite) Color.BLACK else Color.WHITE
        val strokeColor = if (isWhite) Color.parseColor("#33FFFFFF") else Color.parseColor("#26000000")
        val iconRes = if (isWhite) R.drawable.ic_close_white_24dp else R.drawable.ic_close_black_24dp

        val circleBg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(bgColor)
            setStroke(dpToPx(0.5f).toInt(), strokeColor)
        }

        closeButton.background = circleBg
        closeButton.setImageResource(iconRes)
        closeButton.scaleType = ImageView.ScaleType.FIT_CENTER
        val paddingPx = dpToPx(6f).toInt()
        closeButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

        closeButton.setOnClickListener { endFragment() }
    }

    private fun isWhiteColor(color: String?): Boolean {
        if (color.isNullOrBlank()) return false
        val cleanColor = color.trim().lowercase(Locale.ROOT)
        return cleanColor == "white" || cleanColor == "#ffffff" || cleanColor == "#fff"
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

    private fun endFragment() {
        if (activity != null) {
            InAppUpdateDisplayState.releaseDisplayState(mStateId)
            releasePlayer()
            requireActivity().supportFragmentManager.beginTransaction().remove(this@HalfScreenFragment).commit()
        }
    }

    private fun hideStatusBar() {
        val decorView = requireActivity().window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        requireActivity().actionBar?.hide()
    }

    private fun showStatusBar() {
        if (activity != null) {
            ViewCompat.getWindowInsetsController(
                requireActivity().window.decorView
            )?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        if (mIsTop) {
            binding.topVideoView.player = player
        } else {
            binding.botVideoView.player = player
        }
        val mediaItem = MediaItem.fromUri(mInAppMessage!!.mActionData!!.mVideoUrl!!)
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

    override fun onDestroyView() {
        super.onDestroyView()
        showStatusBar()
        releasePlayer()
    }
}