package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
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
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso


class InAppNotificationActivity : Activity(), SmileRating.OnSmileySelectionListener,
    SmileRating.OnRatingSelectedListener, CarouselFinishInterface, CarouselButtonInterface {
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
    private var mIsRotation = false
    private var secondPopUpType = NpsSecondPopUpType.IMAGE_TEXT_BUTTON
    private var npsType = NpsType.NONE
    private var buttonCallback: InAppButtonInterface? = null
    private var isNpsSecondPopupButtonClicked = false
    private var isNpsSecondPopupActivated = false
    private var player: ExoPlayer? = null
    private var player2: ExoPlayer? = null
    private var carouselAdapter: CarouselAdapter? = null
    private var result:Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIntentId = savedInstanceState?.getInt(INTENT_ID_KEY, Int.MAX_VALUE)
            ?: intent.getIntExtra(INTENT_ID_KEY, Int.MAX_VALUE)
        mInAppMessage = inAppMessage
        if (isShowingNpsInApp) {
        if (mInAppMessage == null) {
            Log.e(LOG_TAG, "InAppMessage is null! Could not get display state!")
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        } else {
            buttonCallback = RelatedDigital.getInAppButtonInterface()
            val view: View

            if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.CAROUSEL.toString()) {
                bindingCarousel = CarouselBinding.inflate(layoutInflater)
                view = bindingCarousel.root
            } else {
                binding = ActivityInAppNotificationBinding.inflate(layoutInflater)
                view = binding.root
            }
            cacheResources()
            setContentView(view)

            if (isShowingInApp) {
                if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.CAROUSEL.toString()) {
                    setupCarousel()
                } else {
                    setUpView()
                }
            } else {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                finish()
            }
        }
    }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INTENT_ID_KEY, mIntentId)
        mIsRotation = true
    }

    private val inAppMessage: InAppMessage?
        get() {
            val inAppNotificationState: InAppNotificationState?
            mUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(mIntentId)
            return if (mUpdateDisplayState == null || mUpdateDisplayState!!.getDisplayState() == null) {
                null
            } else {
                inAppNotificationState =
                    mUpdateDisplayState!!.getDisplayState() as InAppNotificationState?
                inAppNotificationState?.getInAppMessage()
            }
        }

    private fun setUpView() {
        if (!mInAppMessage!!.mActionData!!.mImg.isNullOrEmpty()) {
            binding.ivTemplate.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mImg)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mImg!!).into(binding.ivTemplate)
            } else {
                Glide.with(this)
                    .load(mInAppMessage!!.mActionData!!.mImg!!)
                    .into(binding.ivTemplate)
            }
        } else {
            binding.ivTemplate.visibility = View.GONE
            if (!mInAppMessage!!.mActionData!!.mVideoUrl.isNullOrEmpty()) {
                binding.videoView.visibility = View.VISIBLE
                startPlayer()
            } else {
                binding.videoView.visibility = View.GONE
                releasePlayer()
            }
        }
        binding.smileRating.setOnSmileySelectionListener(this)
        binding.smileRating.setOnRatingSelectedListener(this)
        setCloseButton()
        setTemplate()
    }

    private fun setTemplate() {
        binding.btnTemplateSecond.visibility = View.GONE
        if (!mInAppMessage!!.mActionData!!.mBackground.isNullOrEmpty()) {
            try {
                binding.llBack.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for background color\nSetting the default value."
                )
                e.printStackTrace()
            }
        }
        else {
            try {
                binding.llBack.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonTextColor))
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for background color\nSetting the default value."
                )
                e.printStackTrace()
            }


        }
        when (mInAppMessage!!.mActionData!!.mMsgType) {
            InAppNotificationType.IMAGE_TEXT_BUTTON.toString() -> {
                setTitle()
                setBody()
                setButton()

                if (!mInAppMessage!!.mActionData!!.mSecondButtonFunction.isNullOrEmpty()) {
                    setupSecondButton()
                }
                setPromotionCode()
                setCouponCodeDesign()
                binding.ratingBar.visibility = View.GONE
                binding.smileRating.visibility = View.GONE
            }
            InAppNotificationType.FULL_IMAGE.toString() -> {
                binding.tvBody.visibility = View.GONE
                binding.tvTitle.visibility = View.GONE
                binding.smileRating.visibility = View.GONE
                binding.btnTemplate.visibility = View.GONE
                binding.llButtonContainer.visibility = View.GONE
                binding.ivTemplate.setOnClickListener {
                    InAppNotificationClickRequest.createInAppNotificationClickRequest(
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
            InAppNotificationType.IMAGE_BUTTON.toString() -> {
                binding.llTextContainer.visibility = View.GONE
                binding.ratingBar.visibility = View.GONE
                binding.smileRating.visibility = View.GONE

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
        if (mInAppMessage!!.mActionData!!.mMsgTitle.isNullOrEmpty()) {
            binding.tvTitle.visibility = View.GONE
        } else {
            if (!mInAppMessage!!.mActionData!!.mMsgTitleBackgroundColor.isNullOrEmpty()) {
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
        if (mInAppMessage!!.mActionData!!.mMsgBody.isNullOrEmpty()) {
            binding.tvBody.visibility = View.GONE
        } else {
            if (!mInAppMessage!!.mActionData!!.mMsgBodyBackgroundColor.isNullOrEmpty()) {
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

    private fun setCouponCodeDesign() {
        if (mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonText?.isNotEmpty() == true) {
            binding.copyButton.visibility = View.VISIBLE
            binding.tvCouponCodeWithButton.visibility = View.VISIBLE
            binding.contentCopy.visibility = View.GONE
            binding.tvCouponCode.visibility = View.GONE
            binding.llCouponContainer.updatePadding(top = 50, bottom = 50)

            if (!mInAppMessage!!.mActionData!!.mBackground.isNullOrEmpty()) {
                try {
                    binding.llCouponContainer.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mBackground))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for background color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            }

            if (!mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor.isNullOrEmpty()) {
                try {
                    val gdButton = binding.tvCouponCodeWithButton.background as GradientDrawable
                    gdButton.setColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for button color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            }



            setCopyButton()
        }

    }

    private fun setCopyButton() {
        binding.copyButton.text = mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonText
        if (!mInAppMessage!!.mActionData!!.mButtonTextColor.isNullOrEmpty()) {
            try {
                binding.copyButton.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonTextColor))
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for button text color\nSetting the default value."
                )
                e.printStackTrace()
                binding.copyButton.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
            }
        } else {
            binding.copyButton.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
        }
        if (!mInAppMessage!!.mActionData!!.mPromocodeCopyButtonColor.isNullOrEmpty()) {
            try {
                val gdButton = binding.copyButton.background as GradientDrawable
                gdButton.setColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromocodeCopyButtonColor))
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for button color\nSetting the default value."
                )
                e.printStackTrace()
            }
        }
    }

    private fun setButton() {
        if (mInAppMessage!!.mActionData!!.mBtnText.isNullOrEmpty()) {
            binding.llButtonContainer.visibility = View.GONE
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
                    val gdButton = binding.btnTemplate.background as GradientDrawable
                    gdButton.setColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
                    if (!mInAppMessage!!.mActionData!!.mButtonBorderRadius.isNullOrEmpty()) {
                        gdButton.cornerRadius =
                            mInAppMessage!!.mActionData!!.mButtonBorderRadius!!.toFloat()
                    }
                    else {
                        gdButton.cornerRadius = 0f
                    }
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
                    InAppNotificationClickRequest.createInAppNotificationClickRequest(
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
                            } else if (mInAppMessage!!.mActionData!!.mButtonFunction == Constants.BUTTON_COPY_REDIRECT) {
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
                                val clipboard =
                                    applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    getString(R.string.coupon_code),
                                    mInAppMessage!!.mActionData!!.mPromotionCode
                                )
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.copied_to_clipboard),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (mInAppMessage!!.mActionData!!.mButtonFunction == Constants.BUTTON_REDIRECT) {
                                AppUtils.goToNotificationSettings(applicationContext)
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
                                } else
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
                        InAppNotificationClickRequest.createInAppNotificationClickRequest(
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

    private fun setupSecondButton() {
        binding.btnTemplateSecond.visibility = View.VISIBLE
        binding.btnTemplateSecond.text = mInAppMessage!!.mActionData!!.mSecondButtonText
        if (!mInAppMessage!!.mActionData!!.mSecondButtonTextColor.isNullOrEmpty()) {
            try {
                binding.btnTemplateSecond.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mSecondButtonTextColor))
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for button text color\nSetting the default value."
                )
                e.printStackTrace()
                binding.btnTemplateSecond.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
            }
        } else {
            binding.btnTemplateSecond.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.black
                )
            )
        }
        if (!mInAppMessage!!.mActionData!!.mSecondButtonColor.isNullOrEmpty()) {
            try {
                val gdButton = binding.btnTemplateSecond.background as GradientDrawable
                gdButton.setColor(Color.parseColor(mInAppMessage!!.mActionData!!.mSecondButtonColor))
                if (!mInAppMessage!!.mActionData!!.mButtonBorderRadius.isNullOrEmpty()) {
                    gdButton.cornerRadius =
                        mInAppMessage!!.mActionData!!.mButtonBorderRadius!!.toFloat()
                }
                else {
                    gdButton.cornerRadius = 0f
                }
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for button color\nSetting the default value."
                )
                e.printStackTrace()
            }
        }

        binding.btnTemplateSecond.setOnClickListener {
            InAppNotificationClickRequest.createInAppNotificationClickRequest(
                applicationContext,
                mInAppMessage,
                rateReport
            )
            if (buttonCallback != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mSecondButtonAndroidLink)
            } else {
                if (mInAppMessage!!.mActionData!!.mSecondButtonFunction == Constants.BUTTON_LINK) {
                    if (!mInAppMessage!!.mActionData!!.mSecondButtonAndroidLink.isNullOrEmpty()) {
                        try {
                            val viewIntent = Intent(
                                Intent.ACTION_VIEW,
                                StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mSecondButtonAndroidLink)
                            )
                            startActivity(viewIntent)
                        } catch (e: ActivityNotFoundException) {
                            Log.i(
                                LOG_TAG,
                                "User doesn't have an activity for notification URI"
                            )
                        }
                    }
                } else if (mInAppMessage!!.mActionData!!.mSecondButtonFunction == Constants.BUTTON_COPY_REDIRECT) {
                    if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.IMAGE_TEXT_BUTTON.toString()) {
                        if (!mInAppMessage!!.mActionData!!.mSecondButtonAndroidLink.isNullOrEmpty()) {
                            try {
                                val viewIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    StringUtils.getURIfromUrlString(mInAppMessage!!.mActionData!!.mSecondButtonAndroidLink)
                                )
                                startActivity(viewIntent)
                            } catch (e: ActivityNotFoundException) {
                                Log.i(
                                    LOG_TAG,
                                    "User doesn't have an activity for notification URI"
                                )
                            }
                        }

                        val clipboard =
                            applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            getString(R.string.coupon_code),
                            mInAppMessage!!.mActionData!!.mPromotionCode
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    AppUtils.goToNotificationSettings(applicationContext)
                }
            }
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
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
                    result =
                        true // Since the default is "GREAT", no need to check if the user chose something.
                }
                NpsType.NPS_WITH_NUMBERS -> {
                    if (binding.npsWithNumbersView.selectedRate != -1) {
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
                val gdButton = binding.btnTemplate.background as GradientDrawable
                gdButton.setColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
                if (!mInAppMessage!!.mActionData!!.mButtonBorderRadius.isNullOrEmpty()) {
                    gdButton.cornerRadius =
                        mInAppMessage!!.mActionData!!.mButtonBorderRadius!!.toFloat()
                }
                else {
                    gdButton.cornerRadius = 0f
                }
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for button color\nSetting the default value."
                )
                e.printStackTrace()
            }
        }
        binding.btnTemplate.setOnClickListener {
            releasePlayer()
            if (binding.ratingBar.rating != 0f) {
                if (secondPopUpType == NpsSecondPopUpType.FEEDBACK_FORM) {
                    if (isRatingAboveThreshold) {
                        InAppNotificationClickRequest.createInAppNotificationClickRequest(
                            applicationContext,
                            mInAppMessage,
                            rateReport
                        )
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
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for background color\nSetting the default value."
                )
                e.printStackTrace()
            }
        }
        when (secondPopUpType) {
            NpsSecondPopUpType.IMAGE_TEXT_BUTTON -> {
                bindingSecondPopUp.commentBox.visibility = View.GONE
                bindingSecondPopUp.imageView2.visibility = View.GONE
                bindingSecondPopUp.secondVideoView2.visibility = View.GONE
                if (!mInAppMessage!!.mActionData!!.mPromotionCode.isNullOrEmpty()) {
                    bindingSecondPopUp.couponContainer.setBackgroundColor(
                        Color.parseColor(
                            mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor
                        )
                    )
                    bindingSecondPopUp.couponCode.text =
                        mInAppMessage!!.mActionData!!.mPromotionCode
                    bindingSecondPopUp.couponCode.setTextColor(
                        Color.parseColor(
                            mInAppMessage!!.mActionData!!.mPromoCodeTextColor
                        )
                    )
                    bindingSecondPopUp.couponContainer.setOnClickListener {
                        val clipboard =
                            applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            getString(R.string.coupon_code),
                            mInAppMessage!!.mActionData!!.mPromotionCode
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    bindingSecondPopUp.couponContainer.visibility = View.GONE
                }
            }
            NpsSecondPopUpType.IMAGE_TEXT_BUTTON_IMAGE -> {
                bindingSecondPopUp.commentBox.visibility = View.GONE
                bindingSecondPopUp.couponContainer.visibility = View.GONE
                if (!mInAppMessage!!.mActionData!!.mSecondPopupImg2.isNullOrEmpty()) {
                    bindingSecondPopUp.imageView2.visibility = View.VISIBLE
                    bindingSecondPopUp.secondVideoView2.visibility = View.GONE
                    if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg2)) {
                        Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg2)
                            .into(bindingSecondPopUp.imageView2)
                    } else {
                        Glide.with(this)
                            .load(mInAppMessage!!.mActionData!!.mSecondPopupImg2)
                            .into(bindingSecondPopUp.imageView2)
                    }
                } else {
                    bindingSecondPopUp.imageView2.visibility = View.GONE
                    if (!mInAppMessage!!.mActionData!!.mSecondPopupVideoUrl2.isNullOrEmpty()) {
                        bindingSecondPopUp.secondVideoView2.visibility = View.VISIBLE
                        player2 = ExoPlayer.Builder(this).build()
                        bindingSecondPopUp.secondVideoView2.player = player2
                        val mediaItem =
                            MediaItem.fromUri(mInAppMessage!!.mActionData!!.mSecondPopupVideoUrl2!!)
                        player2!!.setMediaItem(mediaItem)
                        player2!!.prepare()
                        player2!!.playWhenReady = true
                    } else {
                        bindingSecondPopUp.secondVideoView2.visibility = View.GONE
                        releasePlayer()
                    }
                }
            }
            NpsSecondPopUpType.FEEDBACK_FORM -> {
                bindingSecondPopUp.imageView2.visibility = View.GONE
                bindingSecondPopUp.secondVideoView2.visibility = View.GONE
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
            bindingSecondPopUp.imageView.visibility = View.VISIBLE
            bindingSecondPopUp.secondVideoView.visibility = View.GONE
            if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg1)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg1)
                    .into(bindingSecondPopUp.imageView)
            } else {
                Glide.with(this)
                    .load(mInAppMessage!!.mActionData!!.mSecondPopupImg1)
                    .into(bindingSecondPopUp.imageView)
            }
        } else {
            bindingSecondPopUp.imageView.visibility = View.GONE
            if (!mInAppMessage!!.mActionData!!.mSecondPopupVideoUrl1.isNullOrEmpty()) {
                bindingSecondPopUp.secondVideoView.visibility = View.VISIBLE
                player = ExoPlayer.Builder(this).build()
                bindingSecondPopUp.secondVideoView.player = player
                val mediaItem =
                    MediaItem.fromUri(mInAppMessage!!.mActionData!!.mSecondPopupVideoUrl1!!)
                player!!.setMediaItem(mediaItem)
                player!!.prepare()
                startPlayer()
            } else {
                bindingSecondPopUp.secondVideoView.visibility = View.GONE
                releasePlayer()
            }
        }
        bindingSecondPopUp.titleView.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        bindingSecondPopUp.titleView.text =
            mInAppMessage!!.mActionData!!.mSecondPopupMsgTitle!!.replace("\\n", "\n")
        bindingSecondPopUp.titleView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgTitleColor))
        bindingSecondPopUp.bodyTextView.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        bindingSecondPopUp.bodyTextView.text =
            mInAppMessage!!.mActionData!!.mSecondPopupMsgBody!!.replace("\\n", "\n")
        bindingSecondPopUp.bodyTextView.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mMsgBodyColor))
        bindingSecondPopUp.bodyTextView.textSize =
            mInAppMessage!!.mActionData!!.mSecondPopupMsgBodyTextSize!!.toFloat() + 8
        bindingSecondPopUp.button.typeface = mInAppMessage!!.mActionData!!.getFontFamily(this)
        bindingSecondPopUp.button.text = mInAppMessage!!.mActionData!!.mSecondPopupBtnText
        bindingSecondPopUp.button.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonTextColor))
        val gdButton = bindingSecondPopUp.button.background as GradientDrawable
        gdButton.setColor(Color.parseColor(mInAppMessage!!.mActionData!!.mButtonColor))
        if (!mInAppMessage!!.mActionData!!.mButtonBorderRadius.isNullOrEmpty()) {
            gdButton.cornerRadius = mInAppMessage!!.mActionData!!.mButtonBorderRadius!!.toFloat()
        }
        else {
            gdButton.cornerRadius = 0f
        }
        bindingSecondPopUp.button.setOnClickListener {
            InAppNotificationClickRequest.createInAppNotificationClickRequest(
                applicationContext,
                mInAppMessage,
                npsSecondPopupRateReport
            )
            isNpsSecondPopupButtonClicked = true
            if (buttonCallback != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonCallback!!.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
            } else {
                if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()) {
                    try {
                        val viewIntent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                mInAppMessage!!.mActionData!!.mAndroidLnk
                            )
                        )
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
            && !StringUtils.isNullOrWhiteSpace(mInAppMessage!!.mActionData!!.mPromoCodeTextColor)
        ) {
            binding.llCouponContainer.visibility = View.VISIBLE
            binding.llCouponContainer.setBackgroundColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeBackgroundColor))
            binding.tvCouponCode.text = mInAppMessage!!.mActionData!!.mPromotionCode
            binding.tvCouponCode.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeTextColor))
            binding.tvCouponCodeWithButton.text = mInAppMessage!!.mActionData!!.mPromotionCode
            binding.tvCouponCodeWithButton.setTextColor(Color.parseColor(mInAppMessage!!.mActionData!!.mPromoCodeTextColor))
            binding.tvCouponCodeWithButton.setTextSize(17f)
            //TODO When data comes use the codes below
            if (mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonText?.isNotEmpty() == true) {
                binding.copyButton.setOnClickListener {
                    val clipboard =
                        applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(
                        getString(R.string.coupon_code),
                        mInAppMessage!!.mActionData!!.mPromotionCode
                    )
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.copied_to_clipboard),
                        Toast.LENGTH_LONG
                    ).show()
/*
                    if(mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonFunction?.isNotEmpty() == true)
                    {
                        if(mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonFunction.equals("copy_close")) {
                            finish()
                        }
                    } */
                }
            } else {
                binding.copyButton.visibility = View.GONE
            }

            binding.llCouponContainer.setOnClickListener {
                val clipboard =
                    applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    getString(R.string.coupon_code),
                    mInAppMessage!!.mActionData!!.mPromotionCode
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    applicationContext,
                    getString(R.string.copied_to_clipboard),
                    Toast.LENGTH_LONG
                ).show()
/*
                if(mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonFunction?.isNotEmpty() == true)
                {
                    if(mInAppMessage!!.mActionData!!.mPromoCodeCopyButtonFunction.equals("copy_close")) {
                        finish()
                    }
                } */
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
        if (mInAppMessage!!.mActionData!!.mCloseEventTrigger != null) {
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
            binding.ratingBar.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.yellow))
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

        var isFromZero = false
        if (!mInAppMessage!!.mActionData!!.mNumberRange.isNullOrEmpty()) {
            isFromZero = mInAppMessage!!.mActionData!!.mNumberRange == "0-10"
        }
        binding.npsWithNumbersView.setColors(colors, isFromZero)
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
    private val isShowingNpsInApp: Boolean
        get() {
            if(mInAppMessage!!.mActionData!!.mMsgType!!.equals("nps_with_numbers")) {
                if (mInAppMessage!!.mActionData!!.mDisplayType!!.equals("inline")) {
                    result = false
                } else if (mInAppMessage!!.mActionData!!.mDisplayType == null && mInAppMessage!!.mActionData!!.mDisplayType!!.equals(
                        "popup"
                    )
                ) {
                    result = true
                }
            }

            return result
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
        releasePlayer()
        if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.CAROUSEL.toString()) {
            carouselAdapter!!.releasePlayer()
        }
        if (mInAppMessage != null) {
            if (mIsRotation) {
                mIsRotation = false
            } else {
                InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            }
            if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.NPS_AND_SECOND_POP_UP.toString()) {
                if (!isNpsSecondPopupButtonClicked && isNpsSecondPopupActivated) {
                    InAppNotificationClickRequest.createInAppNotificationClickRequest(
                        applicationContext,
                        mInAppMessage,
                        rateReport
                    )
                }
            }
        }
    }

    private fun setupCarousel() {
        setFinishOnTouchOutside(
            !mInAppMessage!!.mActionData!!.mCloseEventTrigger.equals("closebutton")
        )

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )

        bindingCarousel.carouselRecyclerView.layoutManager = layoutManager

        carouselAdapter = CarouselAdapter(this, this, this)

        bindingCarousel.carouselRecyclerView.adapter = carouselAdapter

        val snapHelper: SnapHelper = PagerSnapHelper()

        snapHelper.attachToRecyclerView(bindingCarousel.carouselRecyclerView)

        carouselAdapter!!.setMessage(mInAppMessage)
    }

    private fun cacheResources() {
        if (!mInAppMessage!!.mActionData!!.mImg.isNullOrEmpty()) {
            if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mImg)) {
                Picasso.get().load(mInAppMessage!!.mActionData!!.mImg).fetch()
            }
        }
        if (mInAppMessage!!.mActionData!!.mMsgType === InAppNotificationType.NPS_AND_SECOND_POP_UP.toString()) {
            if (!mInAppMessage!!.mActionData!!.mSecondPopupImg1.isNullOrEmpty()) {
                if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg1)) {
                    Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg1).fetch()
                }
            }
            if (!mInAppMessage!!.mActionData!!.mSecondPopupImg2.isNullOrEmpty()) {
                if (AppUtils.isAnImage(mInAppMessage!!.mActionData!!.mSecondPopupImg2)) {
                    Picasso.get().load(mInAppMessage!!.mActionData!!.mSecondPopupImg2).fetch()
                }
            }
        }

        if (!mInAppMessage!!.mActionData!!.mVideoUrl.isNullOrEmpty()) {
            initializePlayer()
        }
    }

    private fun initializePlayer() {
        if (mInAppMessage!!.mActionData!!.mMsgType != InAppNotificationType.CAROUSEL.toString()) {
            player = ExoPlayer.Builder(this).build()
            binding.videoView.player = player
            val mediaItem = MediaItem.fromUri(mInAppMessage!!.mActionData!!.mVideoUrl!!)
            player!!.setMediaItem(mediaItem)
            player!!.prepare()
        }
    }

    private fun startPlayer() {
        player!!.playWhenReady = true
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
        if (player2 != null) {
            player2!!.release()
            player2 = null
        }
    }

    companion object {
        private const val LOG_TAG = "Template Activity"
        const val INTENT_ID_KEY = "INTENT_ID_KEY"
    }

    override fun onFinish() {
        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        releasePlayer()
        if (mInAppMessage!!.mActionData!!.mMsgType == InAppNotificationType.CAROUSEL.toString()) {
            carouselAdapter!!.releasePlayer()
        }
        finish()
    }

    override fun onPress(link: String?) {
        if (link.isNullOrEmpty()) {
            Log.e("InAppCarousel", "The link is not formatted properly!")
            return
        }
        try {
            val viewIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(link)
            )
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(viewIntent)
        } catch (e: Exception) {
            Log.e("InAppCarousel", "The link is not formatted properly!")
        }
    }
}