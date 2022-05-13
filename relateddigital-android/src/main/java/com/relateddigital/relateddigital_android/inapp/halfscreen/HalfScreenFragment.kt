package com.relateddigital.relateddigital_android.inapp.halfscreen

import androidx.fragment.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private fun adjustTop() {
        binding.halfScreenContainerTop.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            RequestHandler.createInAppNotificationClickRequest(requireActivity(), mInAppMessage, null)
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
        if (!mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.halfScreenContainerTop.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            binding.topTitleView.text = mInAppMessage!!.mActionData!!.mMsgTitle
            binding.topTitleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            binding.topTitleView.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 14
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
            if(false) { // TODO : if !video.isNullOrEmpty():
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
        binding.halfScreenContainerBot.setOnClickListener {
            val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
            val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
            RequestHandler.createInAppNotificationClickRequest(requireActivity(), mInAppMessage, null)
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
        if (!mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.halfScreenContainerBot.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            binding.botTitleView.text = mInAppMessage!!.mActionData!!.mMsgTitle
            binding.botTitleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            binding.botTitleView.textSize = mInAppMessage!!.mActionData!!.mMsgTitleTextSize!!.toFloat() * 2 + 14
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
            if(false) { // TODO : if !video.isNullOrEmpty():
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
        if (mIsTop) {
            binding.topCloseButton.setBackgroundResource(getCloseIcon())
            binding.topCloseButton.setOnClickListener { endFragment() }
        } else {
            binding.botCloseButton.setBackgroundResource(getCloseIcon())
            binding.botCloseButton.setOnClickListener { endFragment() }
        }
    }

    private fun getCloseIcon(): Int {
        when (mInAppMessage!!.mActionData!!.mCloseButtonColor) {
            "white" -> return R.drawable.ic_close_white_24dp
            "black" -> return R.drawable.ic_close_black_24dp
        }
        return R.drawable.ic_close_black_24dp
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
        val mediaItem = MediaItem.fromUri(
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") //TODO : real url here
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