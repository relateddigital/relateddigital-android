package com.relateddigital.relateddigital_android.inapp.inappmessages.inlineNpsWithNumbers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.inapp.*
import com.relateddigital.relateddigital_android.inapp.inappmessages.NpsWithNumbersView
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.AppUtils
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso
import java.util.*


class InlineNpsWithNumbersView : LinearLayout {
    private var model: RelatedDigitalModel? = null
    private val mIntentId = -1
    private val buttonCallback: InAppButtonInterface? = null
    var mContext: Context = context
    var mNpsItemClickListener: NpsItemClickListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.inline_nps_with_numbers, this, true)

    }

    fun setNpsWithNumberAction(
        context: Context,
        properties: HashMap<String, String>,
        npsItemClickListener: NpsItemClickListener?,
        parent: Activity?
    ) {
        if (RelatedDigital.isBlocked(mContext)) {
            Log.w(TAG, "Too much server load, ignoring the request!")
            return
        }
        if(RelatedDigital.getRelatedDigitalModel(context).getIsInAppNotificationEnabled()) {
            mNpsItemClickListener = npsItemClickListener

            RequestHandler.createNpsWithNumbersRequest(
                mContext,
                getNpsCallback(context, null),
                properties,
            )
        }


    }

    private fun getNpsCallback(context: Context?, npsRequestListener: NpsRequestListener?): VisilabsCallback {
        return object : VisilabsCallback {
            override fun success(response: VisilabsResponse?) {
                npsRequestListener?.onRequestResult(true)
                try {
                    val mInAppMessages: Array<InAppMessage> = Gson().fromJson(
                        response!!.rawResponse,
                        Array<InAppMessage>::class.java
                    )
                    var mInAppMessage: InAppMessage? = null
                    if (mInAppMessages.size > 0) {
                        mInAppMessage = mInAppMessages[0]
                    }
                    val llback = findViewById<LinearLayout>(R.id.ll_back)
                    llback.visibility = VISIBLE
                    setImage(mInAppMessage!!.mActionData!!.mImg)
                    setTitle(
                        mInAppMessage!!.mActionData!!.mMsgTitle,
                        mInAppMessage!!.mActionData!!.mBackground,
                        mInAppMessage!!.mActionData!!.getFontFamily(mContext),
                        mInAppMessage!!.mActionData!!.mMsgTitleColor,
                        mInAppMessage!!.mActionData!!.mMsgTitleTextSize
                    )
                    setBody(
                        mInAppMessage!!.mActionData!!.mMsgBody,
                        mInAppMessage!!.mActionData!!.mBackground,
                        mInAppMessage!!.mActionData!!.getFontFamily(mContext),
                        mInAppMessage!!.mActionData!!.mMsgBodyColor,
                        mInAppMessage!!.mActionData!!.mMsgBodyTextSize
                    )
                    setButton(
                        mInAppMessage!!.mActionData!!.mBtnText,
                        mInAppMessage!!.mActionData!!.mButtonColor,
                        mInAppMessage!!.mActionData!!.getFontFamily(mContext),
                        mInAppMessage!!.mActionData!!.mButtonTextColor,
                        mInAppMessage!!.mActionData!!.mMsgType,
                        mInAppMessage!!.mActId,
                        mInAppMessage
                    )
                    setTemplate(mInAppMessage!!.mActionData!!.mBackground)
                    showNpsWithNumbers(mInAppMessage!!.mActionData!!.mNumberColors,mInAppMessage!!.mActionData!!.mNumberRange)

                } catch (ex: Exception) {
                    Log.e(InlineNpsWithNumbersView.TAG, ex.message, ex)
                    npsRequestListener?.onRequestResult(false)
                }
            }

            override fun fail(response: VisilabsResponse?) {
                Log.e(InlineNpsWithNumbersView.TAG, response!!.rawResponse)
                npsRequestListener?.onRequestResult(false)
            }

        }


    }

    fun showNpsWithNumbers(colorNumber: Array<String?>?, numberRange: String?) {
        val npsWithNumbersView: NpsWithNumbersView = findViewById(R.id.npsWithNumbersView)
        npsWithNumbersView.setVisibility(VISIBLE)
        val colors = IntArray(colorNumber!!.size)

        for (i in colorNumber.indices) {
            colors[i] = Color.parseColor(colorNumber[i])
        }
        var isFromZero = false
        if (!numberRange.isNullOrEmpty()) {
            isFromZero = numberRange == "0-10"
        }
        npsWithNumbersView.setColors(colors,isFromZero)
    }



    fun setTemplate(backgroundColor: String?) {
        val llBack = findViewById<LinearLayout>(R.id.ll_back)
        if (backgroundColor != null && backgroundColor != "") {
            try {
                llBack.visibility = VISIBLE
                llBack.setBackgroundColor(Color.parseColor(backgroundColor))
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for background color\nSetting the default value."
                )
                e.printStackTrace()
            }
        }
    }

    fun setTitle(
        title: String?,
        backgroundColor: String?,
        fontFamily: Typeface?,
        titleColor: String?,
        textSize: String?
    ) {
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        if (title == "" ||
            title == null
        ) {
            tvTitle.visibility = GONE
        } else {
            if (backgroundColor != null &&
                backgroundColor != ""
            ) {
                tvTitle.setBackgroundColor(Color.parseColor(backgroundColor))
            }
            tvTitle.setText(title.replace("\\n", "\n"))
            tvTitle.setTypeface(fontFamily)
            tvTitle.visibility = VISIBLE
            if (titleColor != null && titleColor != "") {
                try {
                    tvTitle.setTextColor(Color.parseColor(titleColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for message body color\nSetting the default value."
                    )
                    e.printStackTrace()
                    tvTitle.setTextColor(resources.getColor(R.color.blue))
                }
            } else {
                tvTitle.setTextColor(resources.getColor(R.color.blue))
            }
            try {
                //tvTitle.textSize = textSize.toFloat() + 8
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for message body text size\nSetting the default value."
                )
                e.printStackTrace()
                tvTitle.textSize = 12f
            }
        }
    }

    fun setBody(
        body: String?,
        backgroundColor: String?,
        fontFamily: Typeface?,
        bodyColor: String?,
        textSize: String?
    ) {
        val tvBody = findViewById<TextView>(R.id.tv_body)
        if (body == "" ||
            body == null
        ) {
            tvBody.visibility = GONE
        } else {
            if (backgroundColor != null &&
                backgroundColor != ""
            ) {
                tvBody.setBackgroundColor(Color.parseColor(backgroundColor))
            }
            tvBody.setText(body.replace("\\n", "\n"))
            tvBody.setTypeface(fontFamily)
            tvBody.visibility = VISIBLE
            if (bodyColor != null && bodyColor != "") {
                try {
                    tvBody.setTextColor(Color.parseColor(bodyColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for message body color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            }
            try {
               // tvBody.textSize = textSize.toFloat() + 8
            } catch (e: Exception) {
                Log.w(
                    LOG_TAG,
                    "Could not parse the data given for message body text size\nSetting the default value."
                )
                e.printStackTrace()
                tvBody.textSize = 12f
            }
        }
    }

    fun setButton(
        btnText: String?,
        buttonColor: String?,
        fontFamily: Typeface?,
        buttonTextColor: String?,
        msgType: String?,
        actId: Int?,
        mInAppMessage: InAppMessage?
    ) {
        val btnTemplate = findViewById<Button>(R.id.btn_template)
        btnTemplate.visibility = VISIBLE
        if (btnText == "" ||
            btnText == null
        ) {
            btnTemplate.visibility = GONE
        } else {
            btnTemplate.text = btnText
            btnTemplate.setTypeface(fontFamily)
            btnTemplate.visibility = VISIBLE
            if (buttonTextColor != null && buttonTextColor != "") {
                try {
                    btnTemplate.setTextColor(Color.parseColor(buttonTextColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for message body color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            } else {
                btnTemplate.setTextColor(resources.getColor(R.color.black))
            }
            if (buttonColor != null && buttonColor != "") {
                try {
                    btnTemplate.setBackgroundColor(Color.parseColor(buttonColor))
                } catch (e: Exception) {
                    Log.w(
                        LOG_TAG,
                        "Could not parse the data given for button color\nSetting the default value."
                    )
                    e.printStackTrace()
                }
            }
            btnTemplate.setOnClickListener {
                if (isRatingEntered) {

                    RequestHandler.createInAppNotificationClickRequest(mContext,mInAppMessage, getRateReport(msgType, actId))
                    if (buttonCallback != null) {
                        RelatedDigital.setInAppButtonInterface(null)
                        buttonCallback.onPress(mInAppMessage!!.mActionData!!.mAndroidLnk)
                    } else {
                        if (!mInAppMessage!!.mActionData!!.mAndroidLnk.isNullOrEmpty()
                        ) {
                            try {
                                if (mNpsItemClickListener != null && mInAppMessage.mActionData!!.mAndroidLnk
                                        != null
                                ) {
                                    mNpsItemClickListener!!.npsItemClicked(
                                        mInAppMessage!!.mActionData!!.mAndroidLnk
                                    )
                                }
                                val viewIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    StringUtils.getURIfromUrlString(
                                        mInAppMessage!!.mActionData!!.mAndroidLnk
                                    )
                                )
                                context.startActivity(viewIntent)
                            } catch (e: ActivityNotFoundException) {
                                Log.i(
                                    "Visilabs",
                                    "User doesn't have an activity for notification URI"
                                )
                            }
                        }
                    }
                    InAppUpdateDisplayState.releaseDisplayState(mIntentId)
                }
            }
        }
    }

    private val isRatingEntered: Boolean
         get() {
            var result = false
            val npsWithNumbersView: NpsWithNumbersView = findViewById(R.id.npsWithNumbersView)
            if (npsWithNumbersView.selectedRate != -1) {
                result = true
            }
            return result
        }

    private fun getRateReport(msgType: String?, actId: Int?): String {
        val npsWithNumbersView: NpsWithNumbersView = findViewById(R.id.npsWithNumbersView)
        when (msgType) {
            InAppNotificationType.NPS_WITH_NUMBERS.toString() -> return "OM.s_point=" + npsWithNumbersView.selectedRate
                .toString() + "&OM.s_cat=" + msgType.toString() + "&OM.s_page=act-" + actId
        }
        return ""
    }

    fun setImage(imageResId: String?) {
        val ivTemplate = findViewById<ImageView>(R.id.iv_template)
        if (imageResId == "" ||
            imageResId == null
        ) {
            ivTemplate.visibility = GONE
        } else {
            if (AppUtils.isAnImage(imageResId)) {
                Picasso.get().load(imageResId).into(ivTemplate)
            } else {
                Glide.with(this)
                    .load(imageResId)
                    .into(ivTemplate)
            }
        }
    }

    companion object {
        private const val TAG = "NpsWithNumbersView"
        private const val LOG_TAG = "NpsWithNumbersView"
    }
}
