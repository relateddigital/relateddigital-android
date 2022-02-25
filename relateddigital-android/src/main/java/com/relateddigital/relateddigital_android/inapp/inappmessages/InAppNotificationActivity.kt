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
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
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
import com.relateddigital.relateddigital_android.model.InAppCarouselItem


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
    private var mCarouselItems: List<InAppCarouselItem>? = null
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
                mCarouselItems = mInAppMessage!!.mActionData!!.carouselItems
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
                    .into(binding.ivTemplate)
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
        if(mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.tvTitle.visibility = View.GONE
        } else {
            if(!mInAppMessage!!.mActionData!!.mMsgTitleBackgroundColor.isNullOrEmpty()) {
                binding.tvTitle.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleBackgroundColor))
            }
            binding.tvTitle.visibility = View.VISIBLE
            binding.tvTitle.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
            binding.tvTitle.text = mInAppMessage!!.mActionData!!.mMsgTitle!!.replace("\\n", "\n")
            if (!mInAppMessage!!.mActionData!!.mMsgTitleColor.isNullOrEmpty()) {
                try {
                    binding.tvTitle.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for message title color\nSetting the default value."
                    )
                    e.printStackTrace()
                    binding.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.blue
                        )
                    )
                }
            } else {
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
            }
            try {
                binding.tvTitle.textSize =
                    mInAppMessage!!.mActionData!!.mMsgBodyTextSize!!.toFloat() + 12
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for message body text size\nSetting the default value."
                )
                e.printStackTrace()
                binding.tvTitle.textSize = 16f
            }
        }
    }

    private fun setBody() {
        if(mInAppMessage!!.mActionData!!.mMsgBody.isNullOrEmpty()) {
            binding.tvBody.visibility = View.GONE
        } else {
            if(!mInAppMessage!!.mActionData!!.mMsgBodyBackgroundColor.isNullOrEmpty()) {
                binding.tvBody.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgBodyBackgroundColor))
            }
            binding.tvBody.text = mInAppMessage!!.mActionData!!.mMsgBody!!.replace("\\n", "\n")
            binding.tvBody.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
            binding.tvBody.visibility = View.VISIBLE
            if (!mInAppMessage!!.mActionData!!.mMsgBodyColor.isNullOrEmpty()) {
                try {
                    binding.tvBody.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgBodyColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for message body color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            }
            try {
                binding.tvBody.textSize =
                    mInAppMessage!!.mActionData!!.mMsgBodyTextSize!!.toFloat() + 8
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for message body text size\nSetting the default value."
                )
                e.printStackTrace()
                binding.tvBody.textSize = 12f
            }
        }
    }

    private fun setButton() {
        if(mInAppMessage!!.mActionData!!.mBtnText.isNullOrEmpty()) {
            binding.btnTemplate.visibility = View.GONE
        } else {
            binding.btnTemplate.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
            binding.btnTemplate.visibility = View.VISIBLE
            binding.btnTemplate.text = mInAppMessage!!.mActionData!!.mBtnText
            if (!mInAppMessage!!.mActionData!!.mButtonTextColor.isNullOrEmpty()) {
                try {
                    binding.btnTemplate.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonTextColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for button text color\nSetting the default value."
                    )
                    e.printStackTrace()
                    binding.btnTemplate.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.black
                        )
                    )
                }
            } else {
                binding.btnTemplate.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
            }
            if (!mInAppMessage!!.mActionData!!.mButtonColor.isNullOrEmpty()) {
                try {
                    binding.btnTemplate.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for button color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            }
            binding.btnTemplate.setOnClickListener {
                if (npsType == NpsType.NONE) {
                    RequestHandler.createInAppNotificationClickRequest(
                        applicationContext,
                        mInAppMessage,
                        rateReport
                    )
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
                    } else {
                        if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.IMAGE_TEXT_BUTTON.toString()) {
                            if (mInAppMessage!!.mActionData!!.mButtonFunction == Constants.BUTTON_LINK) {
                                if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                                    try {
                                        val viewIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mAndroidLnk)
                                        )
                                        startActivity(viewIntent)
                                    } catch (e: ActivityNotFoundException) {
                                        Log.i(
                                            LOG_TAG,
                                            "User doesn't have an activity for notification URI"
                                        )
                                    }
                                }
                            } else {
                                AppUtils.goToNotificationSettings(applicationContext)
                            }
                        } else {
                            if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                                try {
                                    val viewIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mAndroidLnk)
                                    )
                                    startActivity(viewIntent)
                                } catch (e: ActivityNotFoundException) {
                                    Log.i(
                                        LOG_TAG,
                                        "User doesn't have an activity for notification URI"
                                    )
                                }
                            }
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                    finish()
                } else {
                    if (isRatingEntered) {
                        RequestHandler.createInAppNotificationClickRequest(
                            applicationContext,
                            mInAppMessage,
                            rateReport
                        )
                        if (buttonCallback != null) {
                            RelatedDigital.setInAppButtonInterface(null)
                            buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
                        } else {
                            if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                                try {
                                    val viewIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mAndroidLnk)
                                    )
                                    startActivity(viewIntent)
                                } catch (e: ActivityNotFoundException) {
                                    Log.i(
                                        "Visilabs",
                                        "User doesn't have an activity for notification URI"
                                    )
                                }
                            }
                        }
                        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                        finish()
                    }
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
                            .into(bindingSecondPopUp.imageView2)
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
                    .into(bindingSecondPopUp.imageView)
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
        binding.ratingBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.yellow))
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
        for (i in mCarouselItems!!.indices) {
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
        bindingCarousel.background.visibility = View.VISIBLE
        bindingCarousel.couponContainer.visibility = View.VISIBLE
        for (i in mCarouselItems!!.indices) {
            if (i == mCarouselPosition) {
                bindingCarousel.dotIndicator.getChildAt(i).setBackgroundResource(R.drawable.dot_indicator_selected)
            } else {
                bindingCarousel.dotIndicator.getChildAt(i).setBackgroundResource(R.drawable.dot_indicator_default)
            }
        }

        setupCarouselItem(mCarouselPosition)
    }

    private val isLastCarousel: Boolean
        get() = mCarouselPosition == mCarouselItems!!.size - 1
    private val isFirstCarousel: Boolean
        get() = mCarouselPosition == 0

    private fun cacheImages() {
        if (mIsCarousel) {
            for (i in mCarouselItems!!.indices) {
                if (!mCarouselItems!![i].image.isNullOrEmpty()) {
                    if (AppUtils.isAnImage(mCarouselItems!![i].image)) {
                        Picasso.get().load(mCarouselItems!![i].image).fetch()
                    }
                }
                if (!mCarouselItems!![i].backgroundImage.isNullOrEmpty()) {
                    if (AppUtils.isAnImage(mCarouselItems!![i].backgroundImage)) {
                        Picasso.get().load(mCarouselItems!![i].backgroundImage).fetch()
                    }
                }
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

    private fun setupCarouselItem(position: Int) {
        if (!mCarouselItems!![position].backgroundImage.isNullOrEmpty()) {
            Picasso.get().load(mCarouselItems!![position].backgroundImage)
                .into(bindingCarousel.background)
        } else {
            bindingCarousel.background.visibility = View.GONE
            if (!mCarouselItems!![position].backgroundColor.isNullOrEmpty()) {
                bindingCarousel.background.visibility = View.GONE
                bindingCarousel.carouselContainer.setBackgroundColor(
                    Color.parseColor(
                        mCarouselItems!![position].backgroundColor
                    )
                )
            }
        }

        if (!mCarouselItems!![position].image.isNullOrEmpty()) {
            if (AppUtils.isAnImage(mCarouselItems!![position].image)) {
                Picasso.get().load(mCarouselItems!![position].image)
                    .into(bindingCarousel.carouselImage)
            } else {
                Glide.with(this)
                    .load(mCarouselItems!![position].image)
                    .into(bindingCarousel.carouselImage)
            }
        } else {
            bindingCarousel.carouselImage.visibility = View.GONE
        }

        if (!mCarouselItems!![position].title.isNullOrEmpty()) {
            bindingCarousel.carouselTitle.text = mCarouselItems!![position].title
            bindingCarousel.carouselTitle.setTextColor(Color.parseColor(mCarouselItems!![position].titleColor))
            bindingCarousel.carouselTitle.textSize =
                mCarouselItems!![position].titleTextsize!!.toFloat() + 12
            bindingCarousel.carouselTitle.typeface = mCarouselItems!![position].getTitleFontFamily(
                this
            )
        } else {
            bindingCarousel.carouselTitle.visibility = View.GONE
        }

        if (!mCarouselItems!![position].body.isNullOrEmpty()) {
            bindingCarousel.carouselBodyText.text = mCarouselItems!![position].body
            bindingCarousel.carouselBodyText.setTextColor(
                Color.parseColor(
                    mCarouselItems!![position].bodyColor
                )
            )
            bindingCarousel.carouselBodyText.textSize =
                mCarouselItems!![position].bodyTextsize!!.toFloat() + 8
            bindingCarousel.carouselBodyText.typeface = mCarouselItems!![position].getBodyFontFamily(
                this
            )
        } else {
            bindingCarousel.carouselBodyText.visibility = View.GONE
        }

        if (!mCarouselItems!![position].promotionCode.isNullOrEmpty()) {
            bindingCarousel.couponContainer.setBackgroundColor(
                Color.parseColor(
                    mCarouselItems!![position].promocodeBackgroundColor
                )
            )
            bindingCarousel.couponCode.text = mCarouselItems!![position].promotionCode
            bindingCarousel.couponCode.setTextColor(Color.parseColor(mCarouselItems!![position].promocodeTextColor))
            bindingCarousel.couponContainer.setOnClickListener {
                val clipboard =
                    applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    getString(R.string.coupon_code),
                    mCarouselItems!![position].promotionCode
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.copied_to_clipboard),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            bindingCarousel.couponContainer.visibility = View.GONE
        }

        if (!mCarouselItems!![position].buttonText.isNullOrEmpty()) {
            bindingCarousel.carouselButton.setBackgroundColor(
                Color.parseColor(
                    mCarouselItems!![position].buttonColor
                )
            )
            bindingCarousel.carouselButton.text = mCarouselItems!![position].buttonText
            bindingCarousel.carouselButton.setTextColor(Color.parseColor(mCarouselItems!![position].buttonTextColor))
            bindingCarousel.carouselButton.textSize =
                mCarouselItems!![position].buttonTextsize!!.toFloat() + 12
            bindingCarousel.carouselButton.typeface = mCarouselItems!![position].getButtonFontFamily(
                this
            )
            bindingCarousel.carouselButton.setOnClickListener {
                RequestHandler.createInAppNotificationClickRequest(applicationContext, mInAppMessage, rateReport)
                if (buttonCallback != null) {
                    RelatedDigital.setInAppButtonInterface(null)
                    buttonCallback!!.onPress(mCarouselItems!![position].androidLnk)
                } else {
                    try {
                        val viewIntent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                mCarouselItems!![position].androidLnk
                            )
                        )
                        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(viewIntent)
                    } catch (e: java.lang.Exception) {
                        Log.e(LOG_TAG, "The link is not formatted properly!")
                    }
                }
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
        } else {
            bindingCarousel.carouselButton.visibility = View.GONE
        }
    }

    companion object {
        private const val LOG_TAG = "Template Activity"
        const val INTENT_ID_KEY = "INTENT_ID_KEY"
        private const val CAROUSEL_LAST_INDEX_KEY = "carousel_last_index"
    }
}