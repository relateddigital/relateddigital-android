package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.inapp.InAppNotificationState
import com.relateddigital.relateddigital_android.inapp.InAppUpdateDisplayState
import com.relateddigital.relateddigital_android.model.InAppCarouselItem
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.requestHandler.InAppNotificationClickRequest
import com.squareup.picasso.Picasso

class CarouselFullscreenActivity : Activity() {

    companion object {
        private const val LOG_TAG = "CarouselFullscreen"
        private const val MAX_ITEMS = 5
        private const val HERO_HEIGHT_FRACTION = 0.85f
        private const val CARD_OVERLAP_DP = 72
    }

    private var mIntentId = -1
    private var mUpdateDisplayState: InAppUpdateDisplayState? = null
    private var mInAppMessage: InAppMessage? = null
    private var mCarouselItems: List<InAppCarouselItem> = emptyList()
    private var buttonCallback: InAppButtonInterface? = null
    private var currentPosition = 0

    private lateinit var pager: RecyclerView
    private lateinit var dotsContainer: LinearLayout
    private lateinit var primaryButton: Button
    private lateinit var secondaryButton: Button
    private lateinit var buttonSpacer: View
    private lateinit var closeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel_fullscreen)

        mIntentId = savedInstanceState?.getInt(Constants.INTENT_ID_KEY, Int.MAX_VALUE)
            ?: intent.getIntExtra(Constants.INTENT_ID_KEY, Int.MAX_VALUE)

        mUpdateDisplayState = InAppUpdateDisplayState.claimDisplayState(mIntentId)
        if (mUpdateDisplayState?.getDisplayState() == null) {
            Log.e(LOG_TAG, "Could not get display state")
            finish()
            return
        }

        val state = mUpdateDisplayState!!.getDisplayState() as? InAppNotificationState
        if (state == null) {
            Log.e(LOG_TAG, "InAppNotificationState is null")
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
            return
        }

        mInAppMessage = state.getInAppMessage()
        val items = mInAppMessage?.mActionData?.carouselItems
        if (mInAppMessage == null || items.isNullOrEmpty()) {
            Log.e(LOG_TAG, "InAppMessage or carousel items missing")
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
            return
        }

        mCarouselItems = if (items.size > MAX_ITEMS) items.subList(0, MAX_ITEMS) else items
        buttonCallback = RelatedDigital.getInAppButtonInterface()

        initViews()
        setupPager()
        setupDots()
        setupCloseButton()
        updateFooter(0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.INTENT_ID_KEY, mIntentId)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        finish()
    }

    private fun initViews() {
        pager = findViewById(R.id.carousel_fullscreen_pager)
        dotsContainer = findViewById(R.id.carousel_fullscreen_dots)
        primaryButton = findViewById(R.id.carousel_fullscreen_primary_button)
        secondaryButton = findViewById(R.id.carousel_fullscreen_secondary_button)
        buttonSpacer = findViewById(R.id.carousel_fullscreen_button_spacer)
        closeButton = findViewById(R.id.carousel_fullscreen_close_button)

        primaryButton.setOnClickListener { handleButtonClick(true) }
        secondaryButton.setOnClickListener { handleButtonClick(false) }
    }

    private fun setupPager() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        pager.layoutManager = layoutManager

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(pager)

        pager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(layoutManager)
                    if (snapView != null) {
                        val pos = layoutManager.getPosition(snapView)
                        if (pos != currentPosition && pos in mCarouselItems.indices) {
                            updateFooter(pos)
                        }
                    }
                }
            }
        })

        pager.post { pager.adapter = CarouselFullscreenAdapter() }
    }

    private fun setupDots() {
        dotsContainer.removeAllViews()
        for (i in mCarouselItems.indices) {
            val dot = View(this)
            dot.setBackgroundResource(R.drawable.dot_indicator_default)
            val lp = LinearLayout.LayoutParams(dpToPx(10f), dpToPx(10f))
            lp.setMargins(dpToPx(4f), 0, dpToPx(4f), 0)
            dot.layoutParams = lp
            dotsContainer.addView(dot)
        }
    }

    private fun setupCloseButton() {
        val closeColor = mInAppMessage?.mActionData?.mCloseButtonColor
        if (closeColor == "black") {
            closeButton.setBackgroundResource(R.drawable.ic_close_black_24dp)
        } else {
            closeButton.setBackgroundResource(R.drawable.ic_close_white_24dp)
        }
        closeButton.setOnClickListener {
            InAppUpdateDisplayState.releaseDisplayState(mIntentId)
            finish()
        }
    }

    private fun updateFooter(position: Int) {
        currentPosition = position
        val item = mCarouselItems[position]

        for (i in 0 until dotsContainer.childCount) {
            dotsContainer.getChildAt(i).setBackgroundResource(
                if (i == position) R.drawable.dot_indicator_selected else R.drawable.dot_indicator_default
            )
        }

        var borderRadius = 14f
        val brStr = mInAppMessage?.mActionData?.mButtonBorderRadius
        if (!brStr.isNullOrEmpty()) {
            try { borderRadius = brStr.toFloat() } catch (_: NumberFormatException) {}
        }
        val radiusPx = dpToPx(borderRadius)

        setupPrimaryButton(item, radiusPx.toFloat())
        setupSecondaryButton(item, radiusPx.toFloat())
    }

    private fun setupPrimaryButton(item: InAppCarouselItem, radiusPx: Float) {
        if (item.buttonText.isNullOrEmpty()) {
            primaryButton.visibility = View.GONE
            return
        }
        primaryButton.visibility = View.VISIBLE
        primaryButton.text = item.buttonText!!.replace("\\n", "\n")

        var fillColor = Color.parseColor("#00897B")
        if (!item.buttonColor.isNullOrEmpty()) {
            try { fillColor = parseColor(item.buttonColor!!) } catch (_: Exception) {}
        }

        var textColor = Color.WHITE
        if (!item.buttonTextColor.isNullOrEmpty()) {
            try { textColor = parseColor(item.buttonTextColor!!) } catch (_: Exception) {}
        }

        val bg = GradientDrawable()
        bg.setColor(fillColor)
        bg.cornerRadius = radiusPx
        primaryButton.background = bg
        primaryButton.setTextColor(textColor)
        primaryButton.typeface = item.getButtonFontFamily(this)

        if (!item.buttonTextsize.isNullOrEmpty()) {
            try { primaryButton.textSize = mapTextSize(item.buttonTextsize!!.toFloat(), true) } catch (_: NumberFormatException) {}
        }
    }

    private fun setupSecondaryButton(item: InAppCarouselItem, radiusPx: Float) {
        val hasSecondary = !item.secondButtonText.isNullOrBlank()
        secondaryButton.visibility = if (hasSecondary) View.VISIBLE else View.GONE
        buttonSpacer.visibility = if (hasSecondary) View.VISIBLE else View.GONE

        if (!hasSecondary) return

        secondaryButton.text = item.secondButtonText!!.replace("\\n", "\n")

        var fillColor = Color.parseColor("#00897B")
        if (!item.secondButtonColor.isNullOrEmpty()) {
            try { fillColor = parseColor(item.secondButtonColor!!) } catch (_: Exception) {}
        } else if (!item.buttonColor.isNullOrEmpty()) {
            try { fillColor = parseColor(item.buttonColor!!) } catch (_: Exception) {}
        }

        var secTextColor = Color.WHITE
        if (!item.secondButtonTextColor.isNullOrEmpty()) {
            try { secTextColor = parseColor(item.secondButtonTextColor!!) } catch (_: Exception) {}
        }

        val bg = GradientDrawable()
        bg.setColor(fillColor)
        bg.cornerRadius = radiusPx
        secondaryButton.background = bg
        secondaryButton.setTextColor(secTextColor)
        secondaryButton.typeface = item.getSecondButtonFontFamily(this)

        if (!item.buttonTextsize.isNullOrEmpty()) {
            try { secondaryButton.textSize = mapTextSize(item.buttonTextsize!!.toFloat(), true) } catch (_: NumberFormatException) {}
        }
    }

    private fun mapTextSize(value: Float, isTitle: Boolean): Float {
        val titleSizes = intArrayOf(12, 15, 17, 20, 24)
        val bodySizes  = intArrayOf(10, 12, 14, 16, 18)
        val idx = (value.toInt() - 1).coerceIn(0, 4)
        return if (isTitle) titleSizes[idx].toFloat() else bodySizes[idx].toFloat()
    }

    private fun handleButtonClick(isPrimary: Boolean) {
        val item = mCarouselItems[currentPosition]
        val link = if (isPrimary) item.androidLnk else item.secondAndroidLnk
        val function = if (isPrimary) item.buttonFunction else item.secondButtonFunction

        Log.d(LOG_TAG, "handleButtonClick isPrimary=$isPrimary link=$link function=$function callbackSet=${buttonCallback != null}")

        InAppNotificationClickRequest.createInAppNotificationClickRequest(this, mInAppMessage, "")

        if (function == "redirect") {
            try {
                val settingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                startActivity(settingsIntent)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Could not open app settings: ${e.message}")
            }
        } else {
            if (buttonCallback != null) {
                buttonCallback!!.onPress(link)
            } else {
                try {
                    val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(viewIntent)
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Could not open link: ${e.message}")
                }
            }
        }

        InAppUpdateDisplayState.releaseDisplayState(mIntentId)
        finish()
    }

    private fun dpToPx(dp: Float): Int {
        return Math.round(dp * resources.displayMetrics.density)
    }

    private fun parseColor(colorStr: String): Int {
        var c = colorStr
        if (!c.startsWith("#")) c = "#$c"
        return Color.parseColor(c)
    }

    // -- Adapter --

    private inner class CarouselFullscreenAdapter : RecyclerView.Adapter<CarouselFullscreenViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselFullscreenViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_carousel_fullscreen, parent, false)
            return CarouselFullscreenViewHolder(view)
        }

        override fun onBindViewHolder(holder: CarouselFullscreenViewHolder, position: Int) {
            holder.bind(mCarouselItems[position], pager.height)
        }

        override fun getItemCount(): Int = mCarouselItems.size
    }

    private inner class CarouselFullscreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val heroContainer: FrameLayout = itemView.findViewById(R.id.hero_container)
        private val heroImage: ImageView = itemView.findViewById(R.id.hero_image)
        private val cardView: FrameLayout = itemView.findViewById(R.id.card_view)
        private val iconImage: ImageView = itemView.findViewById(R.id.icon_image)
        private val cardTitle: TextView = itemView.findViewById(R.id.card_title)
        private val cardBody: TextView = itemView.findViewById(R.id.card_body)

        init {
            applyCardBorder()
        }

        private fun applyCardBorder() {
            val cardBg = GradientDrawable()
            cardBg.setColor(Color.WHITE)
            cardBg.cornerRadius = dpToPx(22f).toFloat()
            cardBg.setStroke(dpToPx(1f), Color.parseColor("#DDDDDD"))
            cardView.background = cardBg
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.clipToOutline = true
            }
        }

        private fun applyLayout(totalHeight: Int) {
            if (totalHeight <= 0) return

            val baseHeroHeight = (totalHeight * HERO_HEIGHT_FRACTION).toInt()
            val cardOverlapPx = dpToPx(CARD_OVERLAP_DP.toFloat())
            val baseTopMargin = baseHeroHeight - cardOverlapPx

            val widthPixels = cardView.context.resources.displayMetrics.widthPixels
            val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPixels - dpToPx(40f), View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            cardView.measure(widthSpec, heightSpec)
            val wantedHeight = cardView.measuredHeight

            val maxAllowedCardHeight = totalHeight - baseTopMargin - dpToPx(16f)

            var finalTopMargin = baseTopMargin
            var finalHeroHeight = baseHeroHeight

            if (wantedHeight > maxAllowedCardHeight) {
                finalTopMargin = totalHeight - wantedHeight - dpToPx(16f)
                finalHeroHeight = finalTopMargin + cardOverlapPx
                if (finalTopMargin < dpToPx(16f)) {
                    finalTopMargin = dpToPx(16f)
                    finalHeroHeight = finalTopMargin + cardOverlapPx
                }
            }

            val heroParams = heroContainer.layoutParams
            heroParams.height = finalHeroHeight
            heroContainer.layoutParams = heroParams

            val cardParams = cardView.layoutParams as FrameLayout.LayoutParams
            cardParams.topMargin = finalTopMargin
            cardParams.bottomMargin = dpToPx(16f)
            cardView.layoutParams = cardParams
        }

        fun bind(item: InAppCarouselItem, pagerHeight: Int) {

            if (!item.backgroundImage.isNullOrEmpty()) {
                heroImage.visibility = View.VISIBLE
                Picasso.get().load(item.backgroundImage).into(heroImage)
                heroContainer.setBackgroundColor(Color.WHITE)
            } else {
                heroImage.visibility = View.GONE
                var bgColor = Color.WHITE
                if (!item.backgroundColor.isNullOrEmpty()) {
                    try { bgColor = parseColor(item.backgroundColor!!) } catch (_: Exception) {}
                }
                heroContainer.setBackgroundColor(bgColor)
            }

            if (!item.image.isNullOrEmpty()) {
                iconImage.visibility = View.VISIBLE
                Picasso.get().load(item.image).into(iconImage)
            } else {
                iconImage.visibility = View.GONE
            }

            if (!item.title.isNullOrEmpty()) {
                cardTitle.visibility = View.VISIBLE
                cardTitle.text = item.title!!.replace("\\n", "\n")
                if (!item.titleColor.isNullOrEmpty()) {
                    try { cardTitle.setTextColor(parseColor(item.titleColor!!)) } catch (_: Exception) {}
                }
                cardTitle.typeface = item.getTitleFontFamily(this@CarouselFullscreenActivity)
                if (!item.titleTextsize.isNullOrEmpty()) {
                    try { cardTitle.textSize = mapTextSize(item.titleTextsize!!.toFloat(), true) } catch (_: NumberFormatException) {}
                }
            } else {
                cardTitle.visibility = View.GONE
            }

            if (!item.body.isNullOrEmpty()) {
                cardBody.visibility = View.VISIBLE
                cardBody.text = item.body!!.replace("\\n", "\n")
                if (!item.bodyColor.isNullOrEmpty()) {
                    try { cardBody.setTextColor(parseColor(item.bodyColor!!)) } catch (_: Exception) {}
                }
                cardBody.typeface = item.getBodyFontFamily(this@CarouselFullscreenActivity)
                if (!item.bodyTextsize.isNullOrEmpty()) {
                    try { cardBody.textSize = mapTextSize(item.bodyTextsize!!.toFloat(), false) } catch (_: NumberFormatException) {}
                }
            } else {
                cardBody.visibility = View.GONE
            }

            applyLayout(pagerHeight)
        }
    }
}
