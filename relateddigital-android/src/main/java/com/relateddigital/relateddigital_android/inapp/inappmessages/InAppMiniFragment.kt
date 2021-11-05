package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.app.Fragment
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentInAppMiniBinding
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.util.AnimationManager
import com.squareup.picasso.Picasso
import com.relateddigital.relateddigital_android.network.RequestHandler


class InAppMiniFragment: Fragment() {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCleanedUp = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentInAppMiniBinding.inflate(inflater, container, false)
        var view: View? = binding!!.root
        if (mInAppNotificationState != null) {
            if (mInAppMessage == null) {
                remove()
            } else {
                binding!!.tvInAppTitleMini.text = mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
                if (!mInAppMessage!!.mActionData!!.mImg.equals("")) {
                    binding!!.ivInAppImageMini.visibility = View.VISIBLE
                    Picasso.get().load(mInAppMessage!!.mActionData!!.mImg).into(binding!!.ivInAppImageMini)
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
            mParent!!.fragmentManager.beginTransaction().remove(this).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mInAppMessage != null) {
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
        mHandler = Handler()
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
            view!!.visibility = View.VISIBLE
            view!!.setBackgroundColor(mInAppNotificationState!!.getHighlightColor())
            view!!.setOnTouchListener { _, event -> mDetector!!.onTouchEvent(event) }
            view!!.startAnimation(AnimationManager.getMiniTranslateAnimation(activity))
            binding!!.ivInAppImageMini.startAnimation(AnimationManager.getMiniScaleAnimation(activity))
        }
    }

    private fun setGestureDetector(activity: Context) {
        mDetector = GestureDetector(activity, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent,
                                 velocityX: Float, velocityY: Float): Boolean {
                if (velocityY > 0) {
                    remove()
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {}
            override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                                  distanceX: Float, distanceY: Float): Boolean {
                return false
            }

            override fun onShowPress(e: MotionEvent) {}
            override fun onSingleTapUp(event: MotionEvent): Boolean {
                val uriString: String? = mInAppMessage!!.mActionData!!.mAndroidLnk
                val buttonInterface: InAppButtonInterface? = RelatedDigital.getInAppButtonInterface()
                RequestHandler.createInAppNotificationClickRequest(activity, mInAppMessage, null)
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
                            Log.i(LOG_TAG, "Can't parse notification URI, will not take any action", e)
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
                    val fragmentManager = mParent!!.fragmentManager
                    val transaction = fragmentManager.beginTransaction()
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
            val fragmentManager = mParent!!.fragmentManager
            try {
                val transaction = fragmentManager.beginTransaction()
                transaction.setCustomAnimations(0, R.anim.anim_slide_down).remove(this).commitAllowingStateLoss()
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