package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso

class FullscreenPopupActivity : Activity() {
    private var inAppMessage: InAppMessage? = null
    private var intentId = -1
    private var buttonCallback: InAppButtonInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        intentId = savedInstanceState?.getInt(Constants.INTENT_ID_KEY, Int.MAX_VALUE)
            ?: intent.getIntExtra(Constants.INTENT_ID_KEY, Int.MAX_VALUE)
        inAppMessage = getInAppMessage()

        if (inAppMessage == null) {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            InAppUpdateDisplayState.releaseDisplayState(intentId)
            finish()
            return
        }

        buttonCallback = RelatedDigital.getInAppButtonInterface()
        setContentView(createContentView())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.INTENT_ID_KEY, intentId)
    }

    private fun getInAppMessage(): InAppMessage? {
        val updateDisplayState = InAppUpdateDisplayState.claimDisplayState(intentId)
        if (updateDisplayState == null || updateDisplayState.getDisplayState() == null) {
            return null
        }
        val state = updateDisplayState.getDisplayState() as? InAppNotificationState
        return state?.getInAppMessage()
    }

    private fun createContentView(): View {
        val root = FrameLayout(this)
        root.setBackgroundColor(Color.BLACK)

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.adjustViewBounds = false
        root.addView(
            imageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val imageUrl = inAppMessage?.mActionData?.mImg
        if (!imageUrl.isNullOrEmpty()) {
            if (AppUtils.isAnImage(imageUrl)) {
                Picasso.get().load(imageUrl).into(imageView)
            } else {
                Glide.with(this).load(imageUrl).into(imageView)
            }
        }

        imageView.setOnClickListener { handleImageClick() }
        root.addView(createCloseButton(), closeButtonLayoutParams())
        return root
    }

    private fun createCloseButton(): TextView {
        val closeButton = TextView(this)
        closeButton.text = "×"
        closeButton.gravity = Gravity.CENTER
        closeButton.textSize = 24f
        closeButton.setTextColor(parseCloseButtonColor(inAppMessage?.mActionData?.mCloseButtonColor))
        closeButton.setBackgroundColor(Color.TRANSPARENT)
        closeButton.setOnClickListener {
            InAppUpdateDisplayState.releaseDisplayState(intentId)
            finish()
        }
        return closeButton
    }

    private fun closeButtonLayoutParams(): FrameLayout.LayoutParams {
        val size = dpToPx(44)
        val params = FrameLayout.LayoutParams(size, size)
        params.gravity = Gravity.TOP or Gravity.END
        params.setMargins(0, dpToPx(24), dpToPx(12), 0)
        return params
    }

    private fun parseCloseButtonColor(rawColor: String?): Int {
        val color = rawColor?.trim()
        if (color.isNullOrEmpty()) return Color.WHITE
        return try {
            when {
                color.equals("black", ignoreCase = true) -> Color.BLACK
                color.equals("white", ignoreCase = true) -> Color.WHITE
                else -> Color.parseColor(color)
            }
        } catch (_: IllegalArgumentException) {
            Color.WHITE
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun handleImageClick() {
        val message = inAppMessage ?: return
        InAppNotificationClickRequest.createInAppNotificationClickRequest(applicationContext, message, null)
        val link = message.mActionData?.mAndroidLnk
        if (buttonCallback != null) {
            RelatedDigital.setInAppButtonInterface(null)
            buttonCallback!!.onPress(link)
        } else if (!link.isNullOrEmpty()) {
            try {
                val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(link))
                startActivity(viewIntent)
            } catch (e: ActivityNotFoundException) {
                Log.i("RelatedDigital", "User doesn't have an activity for notification URI")
            }
        }
        InAppUpdateDisplayState.releaseDisplayState(intentId)
        finish()
    }

    companion object {
        private const val LOG_TAG = "FullscreenPopup"
    }
}
