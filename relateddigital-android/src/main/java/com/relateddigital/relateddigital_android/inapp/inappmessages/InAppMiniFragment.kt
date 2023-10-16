package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentInAppMiniBinding
import com.relateddigital.relateddigital_android.databinding.FragmentInAppMiniTopBinding
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.util.AnimationManager
import com.squareup.picasso.Picasso
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest


class InAppMiniFragment : Fragment() {
    private var mParent: Activity? = null
    private var mDetector: GestureDetector? = null
    private var mHandler: Handler? = null
    private var mInAppStateId = 0
    private var mInAppNotificationState: InAppNotificationState? = null
    private var mInAppMessage: InAppMessage? = null
    private var mRemover: Runnable? = null
    private var mDisplayMini: Runnable? = null
    private var mCleanedUp = false
    private var binding: FragmentInAppMiniBinding? = null
    private var bindingTop: FragmentInAppMiniTopBinding? = null
    private var useBinding: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCleanedUp = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (useBinding) {
            binding = FragmentInAppMiniBinding.inflate(inflater, container, false)
            var view: View? = binding!!.root
            if (mInAppNotificationState != null) {

                if (mInAppMessage == null) {
                    remove()
                } else {

                    binding!!.tvInAppTitleMini.text =
                        mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
                    binding!!.tvInAppTitleMini.typeface =
                        mInAppMessage!!.mActionData!!.getFontFamily(requireActivity())
                    //TODO when backend ready setCloseButton()
                    //setCloseButton()
                    if (!mInAppMessage!!.mActionData!!.mImg.equals("")) {
                        binding!!.ivInAppImageMini.visibility = View.VISIBLE
                        Picasso.get().load(mInAppMessage!!.mActionData!!.mImg)
                            .into(binding!!.ivInAppImageMini)
                    } else {
                        binding!!.ivInAppImageMini.visibility = View.GONE
                    }
                    mHandler!!.postDelayed(mRemover!!, MINI_REMOVE_TIME.toLong())
                }
            } else {
                cleanUp()
                view = null
            }
            return view
        } else {
            bindingTop = FragmentInAppMiniTopBinding.inflate(inflater, container, false)
            var viewTop: View? = bindingTop!!.root
            if (mInAppNotificationState != null) {
                bindingTop!!.tvInAppTitleMini.text =
                    mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
                bindingTop!!.tvInAppTitleMini.typeface =
                    mInAppMessage!!.mActionData!!.getFontFamily(requireActivity())
                //TODO when backend ready setCloseButton()
                setCloseButton()
                if (!mInAppMessage!!.mActionData!!.mImg.equals("")) {
                    bindingTop!!.ivInAppImageMini.visibility = View.VISIBLE
                    Picasso.get().load(mInAppMessage!!.mActionData!!.mImg)
                        .into(bindingTop!!.ivInAppImageMini)
                } else {
                    bindingTop!!.ivInAppImageMini.visibility = View.GONE
                }
                mHandler!!.postDelayed(mRemover!!, MINI_REMOVE_TIME.toLong())
            } else {
                cleanUp()
                viewTop = null
            }
            return viewTop
        }
    }

    private val closeIcon: Int
        get() {
            when (mInAppMessage!!.mActionData!!.mCloseButtonColor) {
                "white" -> return R.drawable.ic_close_white_24dp
                "black" -> return R.drawable.ic_close_black_24dp
            }
            return R.drawable.ic_close_black_24dp
        }

    fun setCloseButton() {

        //TODO up or bottom
        /*  if(mInAppMessage!!.mActionData!!up) {
          binding!!.ibClose.visibility = View.VISIBLE
      binding!!.ibClose.setBackgroundResource(R.drawable.ic_close_white_24dp)
      binding!!.ibClose.setOnClickListener {
          remove()
      }
          }*/
        // else
        //   {
        //bindingTop!!.ibClose.visibility = View.VISIBLE
        //bindingTop!!.ibClose.setBackgroundResource(R.drawable.ic_close_white_24dp)
        //bindingTop!!.ibClose.setOnClickListener {
        //   remove()
        //     }
        // }      */

    }


    fun setInAppState(stateId: Int, inAppState: InAppNotificationState?) {
        mInAppStateId = stateId
        mInAppNotificationState = inAppState
        if (mInAppNotificationState != null) {
            mInAppMessage = mInAppNotificationState!!.getInAppMessage()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mCleanedUp) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mInAppMessage != null) {
            //TODO when backend ready make arrangement for delay time here
            mHandler!!.postDelayed(mDisplayMini!!, 500)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        cleanUp()
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mParent = activity
        mHandler = Handler(Looper.getMainLooper())
        mRemover = Runnable { this@InAppMiniFragment.remove() }
        if (mInAppMessage == null || mInAppNotificationState == null) {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            cleanUp()
        } else {
            displayMiniInApp()
            setGestureDetector(activity)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun displayMiniInApp() {
        mDisplayMini = Runnable {
            requireView().visibility = View.VISIBLE
            requireView().setBackgroundColor(mInAppNotificationState!!.getHighlightColor())
            requireView().setOnTouchListener { _, event -> mDetector!!.onTouchEvent(event) }
            if (useBinding) {
                requireView().startAnimation(
                    AnimationManager.getMiniTranslateAnimation(
                        requireActivity()
                    )
                )
                binding!!.ivInAppImageMini.startAnimation(
                    AnimationManager.getMiniScaleAnimation(
                        requireActivity()
                    )
                )
            } else {
                requireView().startAnimation(
                    AnimationManager.getMiniTranslateTopAnimation(
                        requireActivity()
                    )
                )
                bindingTop!!.ivInAppImageMini.startAnimation(
                    AnimationManager.getMiniScaleAnimation(
                        requireActivity()
                    )
                )
            }
        }
    }

    private fun setGestureDetector(activity: Context) {
        mDetector = GestureDetector(activity, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(
                e1: MotionEvent, e2: MotionEvent,
                velocityX: Float, velocityY: Float
            ): Boolean {
                if (velocityY > 0) {
                    remove()
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {}
            override fun onScroll(
                e1: MotionEvent, e2: MotionEvent,
                distanceX: Float, distanceY: Float
            ): Boolean {
                return false
            }

            override fun onShowPress(e: MotionEvent) {}
            override fun onSingleTapUp(event: MotionEvent): Boolean {
                val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
                val buttonInterface: InAppButtonInterface? =
                    RelatedDigital.getInAppButtonInterface()
                InAppNotificationClickRequest.createInAppNotificationClickRequest(
                    activity,
                    mInAppMessage,
                    null
                )
                if (buttonInterface != null) {
                    RelatedDigital.setInAppButtonInterface(null)
                    buttonInterface.onPress(uriString)
                } else {
                    if (!uriString.isNullOrEmpty()) {
                        try {
                            val uri: Uri = Uri.parse(uriString)
                            val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                            mParent!!.startActivity(viewIntent)
                        } catch (e: Exception) {
                            Log.i(
                                LOG_TAG,
                                "Can't parse notification URI, will not take any action",
                                e
                            )
                            return true
                        }
                    }
                }
                remove()
                return true
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cleanUp()
    }

    private fun cleanUp() {
        if (!mCleanedUp) {
            mHandler!!.removeCallbacks(mRemover!!)
            mHandler!!.removeCallbacks(mDisplayMini!!)
            InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
            try {
                if (!mParent!!.isFinishing) {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.remove(this).commitAllowingStateLoss()
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Parent is finishing.", e)
            }
        }
        mCleanedUp = true
    }

    @SuppressLint("ResourceType")
    private fun remove() {
        if (mParent != null && !mCleanedUp) {
            mHandler!!.removeCallbacks(mRemover!!)
            mHandler!!.removeCallbacks(mDisplayMini!!)
            val fragmentManager = requireActivity().supportFragmentManager
            try {
                //TODO When backend ready arrange close animation
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(0, R.anim.anim_slide_down).remove(this)
                    .commitAllowingStateLoss()
                InAppUpdateDisplayState.releaseDisplayState(mInAppStateId)
                mCleanedUp = true
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Fragment can not be removed.", e)
            }
        }
    }

    companion object {
        private const val LOG_TAG = "VisilabsFragment"
        private const val MINI_REMOVE_TIME = 10000
    }
}