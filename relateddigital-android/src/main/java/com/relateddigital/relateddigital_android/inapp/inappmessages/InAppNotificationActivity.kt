package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.ActivityInAppNotificationBinding
import com.relateddigital.relateddigital_android.databinding.CarouselBinding
import com.relateddigital.relateddigital_android.databinding.NpsSecondPopUpBinding
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppNotificationType
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso

class InAppNotificationActivity : Activity(), SmileRating.OnSmileySelectionListener, SmileRating.OnRatingSelectedListener {
    internal enum class NpsSecondPopUpType {
        IMAGE_TEXT_BUTTON, IMAGE_TEXT_BUTTON_IMAGE, FEEDBACK_FORM
    }

    internal enum class NpsType {
        NPS, SMILE_RATING, NPS_WITH_NUMBERS, NPS_WITH_SECOND_POPUP, NONE
    }

    private var mInAppMessage: InAppMessage? = null
    private var mUpdateDisplayState: InAppUpdateDisplayState? = null
    private var mIntentId = -1
    private lateinit var binding: ActivityInAppNotificationBinding
    private lateinit var bindingSecondPopUp: NpsSecondPopUpBinding
    private lateinit var bindingCarousel: CarouselBinding
    private var mIsCarousel = false
    private var mCarouselPosition = -1
    private var mIsRotation = false
    private var secondPopUpType = NpsSecondPopUpType.IMAGE_TEXT_BUTTON
    private var npsType = NpsType.NONE
    private var buttonCallback: InAppButtonInterface? = null
    private var isNpsSecondPopupButtonClicked = false
    private var isNpsSecondPopupActivated = false
    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIntentId = savedInstanceState?.getInt(INTENT_ID_KEY, Int.MAX_VALUE)
                ?: intent.getIntExtra(INTENT_ID_KEY, Int.MAX_VALUE)
        mInAppMessage = inAppMessage
        if (mInAppMessage == null) {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        } else {
            buttonCallback = RelatedDigital.getInAppButtonInterface()
            val view: View

            if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.CAROUSEL.toString()) {
                mIsCarousel = true
                bindingCarousel = CarouselBinding.inflate(layoutInflater)
                view = bindingCarousel.root
                if (savedInstanceState != null) {
                    mCarouselPosition = savedInstanceState.getInt(CAROUSEL_LAST_INDEX_KEY, -1)
                }
            } else {
                mIsCarousel = false
                binding = ActivityInAppNotificationBinding.inflate(layoutInflater)
                view = binding.root
            }
            cacheImages()
            setContentView(view)
            if (isShowingInApp) {
                if (mIsCarousel) {
                    if (mCarouselPosition == -1) {
                        mCarouselPosition = 0
                    }
                    bindingCarousel.carouselContainer.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {
                        override fun onSwipeRight() {
                            if (!isFirstCarousel) {
                                mCarouselPosition--
                                setupViewCarousel()
                            }
                        }

                        override fun onSwipeLeft() {
                            if (!isLastCarousel) {
                                mCarouselPosition++
                                setupViewCarousel()
                            }
                        }
                    })
                    setupInitialViewCarousel()
                } else {
                    setUpView()
                }
            } else {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INTENT_ID_KEY, mIntentId)
        if (mIsCarousel) {
            outState.putInt(CAROUSEL_LAST_INDEX_KEY, mCarouselPosition)
        }
        mIsRotation = true
    }

    private val inAppMessage: InAppMessage?
        get() {
            val inAppNotificationState: InAppNotificationState?
            mUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(mIntentId)
            return if (mUpdateDisplayState == null || mUpdateDisplayState!!.getDisplayState() == null) {
                null
            } else {
                inAppNotificationState = mUpdateDisplayState!!.getDisplayState() as InAppNotificationState?
                inAppNotificationState?.getInAppMessage()
            }
        }

    private fun setUpView() {
        if (!mInAppMessage!!.mActionData!!.mImg.isNullOrEmpty()) {
            binding.ivTemplate.visibility = View.VISIBLE
            if(AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mImg)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mImg!!).into(binding.ivTemplate)
            } else {
                Glide.with(this)
                    .load(mInAppMessage!!.mActionData!!.mImg!!)
                    .into(binding.ivTemplate);
            }
        } else {
            binding.ivTemplate.visibility = View.GONE
        }
        binding.smileRating.setOnSmileySelectionListener(this)
        binding.smileRating.setOnRatingSelectedListener(this)
        setCloseButton()
        setTemplate()
    }

    private fun setTemplate() {
        if (!mInAppMessage!!.mActionData!!.mBackground.isNullOrEmpty()) {
            try {
                binding.llBack.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for background color\nSetting the default value.")
                e.printStackTrace()
            }
        }
        when (mInAppMessage!!.mActionData!!.mMsgType) {
            InAppNotificationType.IMAGE_TEXT_BUTTON.toString() -> {
                setTitle()
                setBody()
                setButton()
                setPromotionCode()
                binding.ratingBar.visibility = View.GONE
                binding.smileRating.visibility = View.GONE
            }
            InAppNotificationType.FULL_IMAGE.toString() -> {
                binding.tvBody.visibility = View.GONE
                binding.tvTitle.visibility = View.GONE
                binding.smileRating.visibility = View.GONE
                binding.btnTemplate.visibility = View.GONE
                binding.ivTemplate.setOnClickListener {
                    RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, rateReport)
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
                    } else {
                        if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                            try {
                                val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mAndroidLnk))
                                startActivity(viewIntent)
                            } catch (e: ActivityNotFoundException) {
                                Log.i("Visilabs", "User doesn't have an activity for notification URI")
                            }
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                }
            }
            InAppNotificationType.IMAGE_BUTTON.toString() -> {
                binding.smileRating.visibility = View.GONE
                binding.llTextContainer.visibility = View.GONE
                setButton()
            }
            InAppNotificationType.NPS.toString() -> {
                npsType = NpsType.NPS
                setTitle()
                setBody()
                setButton()
                showNps()
            }
            InAppNotificationType.SMILE_RATING.toString() -> {
                npsType = NpsType.SMILE_RATING
                setBody()
                setTitle()
                setButton()
                showSmileRating()
            }
            InAppNotificationType.NPS_WITH_NUMBERS.toString() -> {
                npsType = NpsType.NPS_WITH_NUMBERS
                binding.smileRating.visibility = View.GONE
                setBody()
                setTitle()
                setButton()
                showNpsWithNumbers()
            }
            InAppNotificationType.NPS_AND_SECOND_POP_UP.toString() -> {
                npsType = NpsType.NPS_WITH_SECOND_POPUP
                setNpsSecondPopUpCloseButton()
                setTitle()
                setBody()
                setNpsSecondPopUpButton()
                showNps()
            }
        }
    }

    private fun setNpsSecondPopUpCloseButton() {
        binding.ibClose.visibility = View.GONE
        when (mInAppMessage!!.mActionData!!.mSecondPopupType) {
            "image_text_button" -> {
                secondPopUpType = NpsSecondPopUpType.IMAGE_TEXT_BUTTON
            }
            "image_text_button_image" -> {
                secondPopUpType = NpsSecondPopUpType.IMAGE_TEXT_BUTTON_IMAGE
            }
            "feedback_form" -> {
                secondPopUpType = NpsSecondPopUpType.FEEDBACK_FORM
            }
        }
    }

    private fun setTitle() {
        binding.tvTitle.visibility = View.VISIBLE
        binding.tvTitle.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        binding.tvTitle.text = mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
        if (!mInAppMessage!!.mActionData!!.mMsgTitleColor.isNullOrEmpty()) {
            try {
                binding.tvTitle.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for message title color\nSetting the default value.")
                e.printStackTrace()
                binding.tvTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
            }
        } else {
            binding.tvTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue))
        }
        try {
            binding.tvTitle.textSize = mInAppMessage!!.mActionData!!.mMsgBodyTextSize!!.toFloat() + 12
        } catch (e: Exception) {
            Log.w(LOG_TAG, "Could not parse the data given for message body text size\nSetting the default value.")
            e.printStackTrace()
            binding.tvTitle.textSize = 16f
        }
    }

    private fun setBody() {
        binding.tvBody.text = mInAppMessage!!.mActionData!!.mMsgBody!!.replace("\\n", "\n")
        binding.tvBody.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        binding.tvBody.visibility = View.VISIBLE
        if (!mInAppMessage!!.mActionData!!.mMsgBodyColor.isNullOrEmpty()) {
            try {
                binding.tvBody.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgBodyColor))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for message body color\nSetting the default value.")
                e.printStackTrace()
            }
        }
        try {
            binding.tvBody.textSize = mInAppMessage!!.mActionData!!.mMsgBodyTextSize!!.toFloat() + 8
        } catch (e: Exception) {
            Log.w(LOG_TAG, "Could not parse the data given for message body text size\nSetting the default value.")
            e.printStackTrace()
            binding.tvBody.textSize = 12f
        }
    }

    private fun setButton() {
        binding.btnTemplate.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        binding.btnTemplate.visibility = View.VISIBLE
        binding.btnTemplate.text = mInAppMessage!!.mActionData!!.mBtnText
        if (!mInAppMessage!!.mActionData!!.mButtonTextColor.isNullOrEmpty()) {
            try {
                binding.btnTemplate.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonTextColor))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for button text color\nSetting the default value.")
                e.printStackTrace()
                binding.btnTemplate.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            }
        } else {
            binding.btnTemplate.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        }
        if (!mInAppMessage!!.mActionData!!.mButtonColor.isNullOrEmpty()) {
            try {
                binding.btnTemplate.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for button color\nSetting the default value.")
                e.printStackTrace()
            }
        }
        binding.btnTemplate.setOnClickListener {
            if (npsType == NpsType.NONE) {
                RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, rateReport)
                if (buttonCallback != null) {
                    RelatedDigital.setInAppButtonInterface(null)
                    buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
                } else {
                    if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                        try {
                            val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mAndroidLnk))
                            startActivity(viewIntent)
                        } catch (e: ActivityNotFoundException) {
                            Log.i("Visilabs", "User doesn't have an activity for notification URI")
                        }
                    }
                }
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            } else {
                if (isRatingEntered) {
                    RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, rateReport)
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
                    } else {
                        if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                            try {
                                val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mAndroidLnk))
                                startActivity(viewIntent)
                            } catch (e: ActivityNotFoundException) {
                                Log.i("Visilabs", "User doesn't have an activity for notification URI")
                            }
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                }
            }
        }
    }

    // Since the default is "GREAT", no need to check if the user chose something.
    private val isRatingEntered: Boolean
        get() {
            var result = false
            when (npsType) {
                NpsType.NPS -> {
                    if (binding.ratingBar.rating != 0f) {
                        result = true
                    }
                }
                NpsType.SMILE_RATING -> {
                    result = true // Since the default is "GREAT", no need to check if the user chose something.
                }
                NpsType.NPS_WITH_NUMBERS -> {
                    if (binding.npsWithNumbersView.selectedRate != 0) {
                        result = true
                    }
                }
                else -> {

                }
            }
            return result
        }

    private fun setNpsSecondPopUpButton() {
        binding.btnTemplate.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        binding.btnTemplate.visibility = View.VISIBLE
        binding.btnTemplate.text = mInAppMessage!!.mActionData!!.mBtnText
        if (!mInAppMessage!!.mActionData!!.mButtonTextColor.isNullOrEmpty()) {
            try {
                binding.btnTemplate.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonTextColor))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for button text color\nSetting the default value.")
                e.printStackTrace()
                binding.btnTemplate.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
            }
        } else {
            binding.btnTemplate.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        }
        if (!mInAppMessage!!.mActionData!!.mButtonColor.isNullOrEmpty()) {
            try {
                binding.btnTemplate.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for button color\nSetting the default value.")
                e.printStackTrace()
            }
        }
        binding.btnTemplate.setOnClickListener {
            if (binding.ratingBar.rating != 0f) {
                if (secondPopUpType == NpsSecondPopUpType.FEEDBACK_FORM) {
                    if (isRatingAboveThreshold) {
                        RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, rateReport)
                        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                        finish()
                    } else {
                        setupSecondPopUp()
                    }
                } else {
                    setupSecondPopUp()
                }
            }
        }
    }

    private val isRatingAboveThreshold: Boolean
        get() {
            val rating = (binding.ratingBar.rating * 2).toInt()
            return rating >= mInAppMessage!!.mActionData!!.mSecondPopupFeecbackFormMinPoint!!.toInt() * 2
        }

    private fun setupSecondPopUp() {
        isNpsSecondPopupActivated = true
        bindingSecondPopUp = NpsSecondPopUpBinding.inflate(layoutInflater)
        setContentView(bindingSecondPopUp.root)
        if (!mInAppMessage!!.mActionData!!.mBackground.isNullOrEmpty()) {
            try {
                bindingSecondPopUp.container.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Could not parse the data given for background color\nSetting the default value.")
                e.printStackTrace()
            }
        }
        when (secondPopUpType) {
            NpsSecondPopUpType.IMAGE_TEXT_BUTTON -> {
                bindingSecondPopUp.commentBox.visibility = View.GONE
                bindingSecondPopUp.imageView2.visibility = View.GONE
                if (!mInAppMessage!!.mActionData!!.mPromotionCode.isNullOrEmpty()) {
                    bindingSecondPopUp.couponContainer.setBackgroundColor(Color.parseColor(
                            mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor
                    ))
                    bindingSecondPopUp.couponCode.text = mInAppMessage!!.mActionData!!.mPromotionCode
                    bindingSecondPopUp.couponCode.setTextColor(Color.parseColor(
                            mInAppMessage!!.mActionData!!.mPromoCodeTextColor
                    ))
                    bindingSecondPopUp.couponContainer.setOnClickListener {
                        val clipboard = applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(getString(R.string.coupon_code), mInAppMessage!!.mActionData!!.mPromotionCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(applicationContext, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
                    }
                } else {
                    bindingSecondPopUp.couponContainer.visibility = View.GONE
                }
            }
            NpsSecondPopUpType.IMAGE_TEXT_BUTTON_IMAGE -> {
                bindingSecondPopUp.commentBox.visibility = View.GONE
                bindingSecondPopUp.couponContainer.visibility = View.GONE
                if (!mInAppMessage!!.mActionData!!.mSecondPopupImg2.isNullOrEmpty()) {
                    if(AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg2)) {
                        Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg2)
                            .into(bindingSecondPopUp.imageView2)
                    } else {
                        Glide.with(this)
                            .load(mInAppMessage!!.mActionData!!.mSecondPopupImg2)
                            .into(bindingSecondPopUp.imageView2);
                    }
                }
            }
            NpsSecondPopUpType.FEEDBACK_FORM -> {
                bindingSecondPopUp.imageView2.visibility = View.GONE
                bindingSecondPopUp.couponContainer.visibility = View.GONE
            }
        }
        if (mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("backgroundclick")) {
            bindingSecondPopUp.closeButton.visibility = View.GONE
            setFinishOnTouchOutside(true)
        } else {
            setFinishOnTouchOutside(
                !mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("closebutton")
            )
            bindingSecondPopUp.closeButton.setBackgroundResource(closeIcon)
            bindingSecondPopUp.closeButton.setOnClickListener {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
        }
        if (!mInAppMessage!!.mActionData!!.mSecondPopupImg1.isNullOrEmpty()) {
            if(AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg1)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg1)
                    .into(bindingSecondPopUp.imageView)
            } else {
                Glide.with(this)
                    .load(mInAppMessage!!.mActionData!!.mSecondPopupImg1)
                    .into(bindingSecondPopUp.imageView);
            }
        }
        bindingSecondPopUp.titleView.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        bindingSecondPopUp.titleView.text = mInAppMessage!!.mActionData!!.mSecondPopupMsgTitle!!.replace("\\n", "\n")
        bindingSecondPopUp.titleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
        bindingSecondPopUp.bodyTextView.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        bindingSecondPopUp.bodyTextView.text = mInAppMessage!!.mActionData!!.mSecondPopupMsgBody!!.replace("\\n", "\n")
        bindingSecondPopUp.bodyTextView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgBodyColor))
        bindingSecondPopUp.bodyTextView.textSize = mInAppMessage!!.mActionData!!.mSecondPopupMsgBodyTextSize!!.toFloat() + 8
        bindingSecondPopUp.button.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        bindingSecondPopUp.button.text = mInAppMessage!!.mActionData!!.mSecondPopupBtnText
        bindingSecondPopUp.button.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonTextColor))
        bindingSecondPopUp.button.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
        bindingSecondPopUp.button.setOnClickListener {
            RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, npsSecondPopupRateReport)
            isNpsSecondPopupButtonClicked = true
            if (buttonCallback != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
            } else {
                if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                    try {
                        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(
                                mInAppMessage!!.mActionData!!.mAndroidLnk))
                        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "The link is not formatted properly!")
                    }
                } else {
                    Log.e(LOG_TAG, "The link is empty or not in a proper format!")
                }
            }
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        }
    }

    private fun setPromotionCode() {
        if (!StringUtils.isNullOrWhiteSpace(mInAppMessage!!.mActionData!!.mPromotionCode)
                && !StringUtils.isNullOrWhiteSpace(mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor)
                && !StringUtils.isNullOrWhiteSpace(mInAppMessage!!.mActionData!!.mPromoCodeTextColor)) {
            binding.llCouponContainer.visibility = View.VISIBLE
            binding.llCouponContainer.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor))
            binding.tvCouponCode.text = mInAppMessage!!.mActionData!!.mPromotionCode
            binding.tvCouponCode.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeTextColor))
            binding.llCouponContainer.setOnClickListener {
                val clipboard = applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.coupon_code), mInAppMessage!!.mActionData!!.mPromotionCode)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(applicationContext, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
            }
        } else {
            binding.llCouponContainer.visibility = View.GONE
        }
    }

    private val rateReport: String
        get() {
            when (mInAppMessage!!.mActionData!!.mMsgType) {
                InAppNotificationType.SMILE_RATING.toString() -> return "OM.s_point=" + binding.smileRating.rating.toString() + "&OM.s_cat=" + mInAppMessage!!.mActionData!!.mMsgType.toString() + "&OM.s_page=act-" + mInAppMessage!!.mActId
                InAppNotificationType.NPS.toString(), InAppNotificationType.NPS_AND_SECOND_POP_UP.toString() -> return "OM.s_point=" + binding.ratingBar.rating.toString() + "&OM.s_cat=" + mInAppMessage!!.mActionData!!.mMsgType.toString() + "&OM.s_page=act-" + mInAppMessage!!.mActId
                InAppNotificationType.NPS_WITH_NUMBERS.toString() -> return "OM.s_point=" + binding.npsWithNumbersView.selectedRate.toString() + "&OM.s_cat=" + mInAppMessage!!.mActionData!!.mMsgType.toString() + "&OM.s_page=act-" + mInAppMessage!!.mActId
            }
            return ""
        }
    private val npsSecondPopupRateReport: String
        get() = when (secondPopUpType) {
            NpsSecondPopUpType.IMAGE_TEXT_BUTTON, NpsSecondPopUpType.IMAGE_TEXT_BUTTON_IMAGE -> {
                "OM.s_point=" + binding.ratingBar.rating.toString() + "&OM.s_cat=" + mInAppMessage!!.mActionData!!.mMsgType
                        .toString() + "&OM.s_page=act-" + mInAppMessage!!.mActId.toString() +
                        "&OM.btn_title=" + "Android Link Yonlendirme" +
                        "&OM.btn_source=" + "OM.act-" + mInAppMessage!!.mActId
            }
            NpsSecondPopUpType.FEEDBACK_FORM -> {
                Toast.makeText(this, getString(R.string.feedback_toast), Toast.LENGTH_SHORT).show()
                "OM.s_point=" + binding.ratingBar.rating.toString() + "&OM.s_cat=" + mInAppMessage!!.mActionData!!.mMsgType
                        .toString() + "&OM.s_page=act-" + mInAppMessage!!.mActId.toString() + "&OM.s_feed=" +
                        bindingSecondPopUp.commentBox.text.toString()
            }
        }

    private fun setCloseButton() {
        if(mInAppMessage!!.mActionData!!.mCloseEventTrigger != null) {
            if (mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("backgroundclick")) {
                binding.ibClose.visibility = View.GONE
                setFinishOnTouchOutside(true)
            } else {
                setFinishOnTouchOutside(
                    !mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("closebutton")
                )
                binding.ibClose.setOnClickListener {
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                }
                binding.ibClose.setBackgroundResource(closeIcon)
            }
        } else {
            setFinishOnTouchOutside(true)
            binding.ibClose.setOnClickListener {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
            binding.ibClose.setBackgroundResource(closeIcon)
        }
    }

    private fun showNps() {
        binding.ratingBar.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ratingBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.yellow))
        }
    }

    private fun showSmileRating() {
        binding.smileRating.visibility = View.VISIBLE
    }

    private fun showNpsWithNumbers() {
        binding.npsWithNumbersView.visibility = View.VISIBLE
        val colors = IntArray(mInAppMessage!!.mActionData!!.mNumberColors!!.size)
        for (i in mInAppMessage!!.mActionData!!.mNumberColors!!.indices) {
            colors[i] = Color.parseColor(mInAppMessage!!.mActionData!!.mNumberColors!![i])
        }
        binding.npsWithNumbersView.setColors(colors)
    }

    private val closeIcon: Int
        get() {
            when (mInAppMessage!!.mActionData!!.mCloseButtonColor) {
                "white" -> return R.drawable.ic_close_white_24dp
                "black" -> return R.drawable.ic_close_black_24dp
            }
            return R.drawable.ic_close_black_24dp
        }
    private val isShowingInApp: Boolean
        get() {
            return if (mUpdateDisplayState == null) {
                false
            } else InAppNotificationState.TYPE == mUpdateDisplayState!!.getDisplayState()!!.type
        }

    override fun onSmileySelected(@BaseRating.Smiley smiley: Int, reselected: Boolean) {
        when (smiley) {
            BaseRating.BAD -> Log.i("VL", "Bad")
            BaseRating.GOOD -> Log.i("VL", "Good")
            BaseRating.GREAT -> Log.i("VL", "Great")
            BaseRating.OKAY -> Log.i("VL", "Okay")
            BaseRating.TERRIBLE -> Log.i("VL", "Terrible")
            BaseRating.NONE -> Log.i("VL", "None")
        }
    }

    override fun onRatingSelected(level: Int, reselected: Boolean) {
        Log.i("VL", "Rated as: $level - $reselected")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mInAppMessage != null) {
            if (mIsRotation) {
                mIsRotation = false
            } else {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            }
            if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.NPS_AND_SECOND_POP_UP.toString()) {
                if (!isNpsSecondPopupButtonClicked && isNpsSecondPopupActivated) {
                    RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, rateReport)
                }
            }
        }
    }

    private fun setupInitialViewCarousel() {
        if (mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("backgroundclick")) {
            bindingCarousel.carouselCloseButton.visibility = View.GONE
            setFinishOnTouchOutside(true)
        } else {
            setFinishOnTouchOutside(
                !mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("closebutton")
            )
            bindingCarousel.carouselCloseButton.setBackgroundResource(closeIcon)
            bindingCarousel.carouselCloseButton.setOnClickListener {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
        }
        for (i in 0 until carouselItemCount) {
            val view = View(applicationContext)
            view.setBackgroundResource(R.drawable.dot_indicator_default)
            val layoutParams = LinearLayout.LayoutParams(
                    40, 40)
            layoutParams.setMargins(10, 0, 10, 0)
            view.layoutParams = layoutParams
            bindingCarousel.dotIndicator.addView(view)
        }
        setupViewCarousel()
    }

    private fun setupViewCarousel() {
        bindingCarousel.carouselImage.visibility = View.VISIBLE
        bindingCarousel.carouselTitle.visibility = View.VISIBLE
        bindingCarousel.carouselBodyText.visibility = View.VISIBLE
        bindingCarousel.carouselButton.visibility = View.VISIBLE
        for (i in 0 until carouselItemCount) {
            if (i == mCarouselPosition) {
                bindingCarousel.dotIndicator.getChildAt(i).setBackgroundResource(R.drawable.dot_indicator_selected)
            } else {
                bindingCarousel.dotIndicator.getChildAt(i).setBackgroundResource(R.drawable.dot_indicator_default)
            }
        }
        when (mCarouselPosition) {
            0 -> {
                bindingCarousel.carouselContainer.setBackgroundColor(Color.parseColor("#CD2F2F"))
                if(AppUtils.isAnImage("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_154_20200603160304969.jpg")) {
                    Picasso.get().load("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_154_20200603160304969.jpg")
                        .into(bindingCarousel.carouselImage)
                } else {
                    Glide.with(this)
                        .load("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_154_20200603160304969.jpg")
                        .into(bindingCarousel.carouselImage);
                }
                bindingCarousel.carouselTitle.text = "Title1"
                bindingCarousel.carouselTitle.setTextColor(Color.parseColor("#E8F279"))
                bindingCarousel.carouselTitle.textSize = 32f
                bindingCarousel.carouselBodyText.text = "Text1"
                bindingCarousel.carouselBodyText.setTextColor(Color.parseColor("#E3A9E7"))
                bindingCarousel.carouselBodyText.textSize = 24f
                bindingCarousel.carouselButton.text = "Button1"
                bindingCarousel.carouselButton.setTextColor(Color.parseColor("#000000"))
                bindingCarousel.carouselButton.setBackgroundColor(Color.parseColor("#A9E7E4"))
                bindingCarousel.carouselButton.textSize = 24f
                bindingCarousel.carouselButton.setOnClickListener { //TODO : send report here
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback!!.onPress("https://www.relateddigital.com/")
                    } else {
                        try {
                            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
                            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(viewIntent)
                        } catch (e: Exception) {
                            Log.e(LOG_TAG, "The link is not formatted properly!")
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                }
            }
            1 -> {
                bindingCarousel.carouselContainer.setBackgroundColor(Color.parseColor("#77CD2F"))
                bindingCarousel.carouselImage.visibility = View.GONE
                bindingCarousel.carouselTitle.text = "Title2"
                bindingCarousel.carouselTitle.setTextColor(Color.parseColor("#F5F43E"))
                bindingCarousel.carouselTitle.textSize = 32f
                bindingCarousel.carouselBodyText.text = "Text2"
                bindingCarousel.carouselBodyText.setTextColor(Color.parseColor("#FFFFFF"))
                bindingCarousel.carouselBodyText.textSize = 24f
                bindingCarousel.carouselButton.text = "Button2"
                bindingCarousel.carouselButton.setBackgroundColor(Color.parseColor("#27FB76"))
                bindingCarousel.carouselButton.setTextColor(Color.parseColor("#000000"))
                bindingCarousel.carouselButton.textSize = 24f
                bindingCarousel.carouselButton.setOnClickListener { //TODO : send report here
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback!!.onPress("https://www.relateddigital.com/")
                    } else {
                        try {
                            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
                            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(viewIntent)
                        } catch (e: Exception) {
                            Log.e(LOG_TAG, "The link is not formatted properly!")
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                }
            }
            2 -> {
                bindingCarousel.carouselContainer.setBackgroundColor(Color.parseColor("#2FBBCD"))
                if(AppUtils.isAnImage("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_411_20210121113801841.jpg")) {
                    Picasso.get().load("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_411_20210121113801841.jpg")
                        .into(bindingCarousel.carouselImage)
                } else {
                    Glide.with(this)
                        .load("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_411_20210121113801841.jpg")
                        .into(bindingCarousel.carouselImage);
                }
                bindingCarousel.carouselTitle.text = "Title3"
                bindingCarousel.carouselTitle.setTextColor(Color.parseColor("#FFFFFF"))
                bindingCarousel.carouselTitle.textSize = 32f
                bindingCarousel.carouselBodyText.text = "Text3"
                bindingCarousel.carouselBodyText.setTextColor(Color.parseColor("#1D19E0"))
                bindingCarousel.carouselBodyText.textSize = 24f
                bindingCarousel.carouselButton.visibility = View.GONE
            }
            3 -> {
                bindingCarousel.carouselContainer.setBackgroundColor(Color.parseColor("#C9CD2F"))
                bindingCarousel.carouselImage.visibility = View.GONE
                bindingCarousel.carouselTitle.text = "Title4"
                bindingCarousel.carouselTitle.setTextColor(Color.parseColor("#E019D6"))
                bindingCarousel.carouselTitle.textSize = 32f
                bindingCarousel.carouselBodyText.visibility = View.GONE
                bindingCarousel.carouselButton.text = "Button4"
                bindingCarousel.carouselButton.setBackgroundColor(Color.parseColor("#E02B19"))
                bindingCarousel.carouselButton.setTextColor(Color.parseColor("#080201"))
                bindingCarousel.carouselButton.textSize = 24f
                bindingCarousel.carouselButton.setOnClickListener { //TODO : send report here
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback!!.onPress("https://www.relateddigital.com/")
                    } else {
                        try {
                            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
                            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(viewIntent)
                        } catch (e: Exception) {
                            Log.e(LOG_TAG, "The link is not formatted properly!")
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                }
            }
            4 -> {
                bindingCarousel.carouselContainer.setBackgroundColor(Color.parseColor("#CD2FC5"))
                if(AppUtils.isAnImage("https://e7.pngegg.com/pngimages/994/882/png-clipart-new-super-mario-bros-2-new-super-mario-bros-2-mario-luigi-superstar-saga-mario-heroes-super-mario-bros.png")) {
                    Picasso.get().load("https://e7.pngegg.com/pngimages/994/882/png-clipart-new-super-mario-bros-2-new-super-mario-bros-2-mario-luigi-superstar-saga-mario-heroes-super-mario-bros.png")
                        .into(bindingCarousel.carouselImage)
                } else {
                    Glide.with(this)
                        .load("https://e7.pngegg.com/pngimages/994/882/png-clipart-new-super-mario-bros-2-new-super-mario-bros-2-mario-luigi-superstar-saga-mario-heroes-super-mario-bros.png")
                        .into(bindingCarousel.carouselImage);
                }
                bindingCarousel.carouselTitle.visibility = View.GONE
                bindingCarousel.carouselBodyText.text = "Text5"
                bindingCarousel.carouselBodyText.setTextColor(ContextCompat.getColor(applicationContext, R.color.yellow))
                bindingCarousel.carouselBodyText.textSize = 24f
                bindingCarousel.carouselButton.visibility = View.GONE
            }
            else -> {
            }
        }
    }

    //TODO: return real carousel item count here
    private val carouselItemCount: Int
        get() =//TODO: return real carousel item count here
            5

    private val isLastCarousel: Boolean
        get() = mCarouselPosition == carouselItemCount - 1
    private val isFirstCarousel: Boolean
        get() = mCarouselPosition == 0

    private fun cacheImages() {
        if (mIsCarousel) {
            //TODO: cache all images for carousel
            if (AppUtils.isAnImage("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_154_20200603160304969.jpg")) {
                Picasso.get()
                    .load("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_154_20200603160304969.jpg")
                    .fetch()
            }
            if (AppUtils.isAnImage("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_411_20210121113801841.jpg")) {
                Picasso.get()
                    .load("https://img.visilabs.net/in-app-message/uploaded_images/163_1100_411_20210121113801841.jpg")
                    .fetch()
            }
            if (AppUtils.isAnImage("https://e7.pngegg.com/pngimages/994/882/png-clipart-new-super-mario-bros-2-new-super-mario-bros-2-mario-luigi-superstar-saga-mario-heroes-super-mario-bros.png")) {
                Picasso.get()
                    .load("https://e7.pngegg.com/pngimages/994/882/png-clipart-new-super-mario-bros-2-new-super-mario-bros-2-mario-luigi-superstar-saga-mario-heroes-super-mario-bros.png")
                    .fetch()
            }
        } else {
            if (!mInAppMessage!!.mActionData!!.mImg.isNullOrEmpty()) {
                if(AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mImg)) {
                    Picasso.get().load(mInAppMessage!!.mActionData!!.mImg).fetch()
                }
            }
            if (mInAppMessage!!.mActionData!!.mMsgType === InAppNotificationType.NPS_AND_SECOND_POP_UP.toString()) {
                if (!mInAppMessage!!.mActionData!!.mSecondPopupImg1.isNullOrEmpty()) {
                    if(AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg1)) {
                        Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg1).fetch()
                    }
                }
                if (!mInAppMessage!!.mActionData!!.mSecondPopupImg2.isNullOrEmpty()) {
                    if(AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg2)) {
                        Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg2).fetch()
                    }
                }
            }
        }
    }

    companion object {
        private const val LOG_TAG = "Template Activity"
        const val INTENT_ID_KEY = "INTENT_ID_KEY"
        private const val CAROUSEL_LAST_INDEX_KEY = "carousel_last_index"
    }
}