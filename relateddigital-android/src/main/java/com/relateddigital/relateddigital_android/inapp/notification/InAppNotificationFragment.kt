package com.relateddigital.relateddigital_android.inapp.notification

import android.app.Fragment
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.*
import com.relateddigital.relateddigital_android.model.InAppNotificationModel
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [InAppNotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InAppNotificationFragment : Fragment() {
    internal enum class PositionOnScreen {
        TOP, MIDDLE, BOTTOM
    }

    internal enum class Shape {
        CIRCLE, SHARP_EDGE, SOFT_EDGE
    }

    private lateinit var bindingLt: FragmentInAppNotificationLtBinding
    private lateinit var bindingLm: FragmentInAppNotificationLmBinding
    private lateinit var bindingLb: FragmentInAppNotificationLbBinding
    private lateinit var bindingRt: FragmentInAppNotificationRtBinding
    private lateinit var bindingRm: FragmentInAppNotificationRmBinding
    private lateinit var bindingRb: FragmentInAppNotificationRbBinding
    private var isRight = true
    private lateinit var positionOnScreen: PositionOnScreen
    private var isTopToBottom = true
    private var isExpanded = false
    private var isSmallImage = false
    private var shape = Shape.SOFT_EDGE
    private var isArrow = false
    private var isBackgroundImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO : get the real data here and if rotation, get mModel from savedInstanceState
        //mModel = (InAppNotificationModel) getArguments().getSerializable(ARG_PARAM1);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view: View

        // TODO : get from the real data here
        isRight = true
        isTopToBottom = true
        positionOnScreen = PositionOnScreen.BOTTOM
        isSmallImage = true
        shape = Shape.SOFT_EDGE
        isArrow = true
        isBackgroundImage = true

        if (isRight) {
            when (positionOnScreen) {
                PositionOnScreen.TOP -> {
                    bindingRt =
                        FragmentInAppNotificationRtBinding.inflate(inflater, container, false)
                    view = bindingRt.root
                }
                PositionOnScreen.MIDDLE -> {
                    bindingRm =
                        FragmentInAppNotificationRmBinding.inflate(inflater, container, false)
                    view = bindingRm.root
                }
                else -> {
                    bindingRb =
                        FragmentInAppNotificationRbBinding.inflate(inflater, container, false)
                    view = bindingRb.root
                }
            }
        } else {
            when (positionOnScreen) {
                PositionOnScreen.TOP -> {
                    bindingLt =
                        FragmentInAppNotificationLtBinding.inflate(inflater, container, false)
                    view = bindingLt.root
                }
                PositionOnScreen.MIDDLE -> {
                    bindingLm =
                        FragmentInAppNotificationLmBinding.inflate(inflater, container, false)
                    view = bindingLm.root
                }
                else -> {
                    bindingLb =
                        FragmentInAppNotificationLbBinding.inflate(inflater, container, false)
                    view = bindingLb.root
                }
            }
        }
        setupInitialView()
        return view
    }

    private fun setupInitialView() {
        if (isRight) {
            when (positionOnScreen) {
                PositionOnScreen.TOP -> adjustRt()
                PositionOnScreen.MIDDLE -> adjustRm()
                else -> adjustRb()
            }
        } else {
            when (positionOnScreen) {
                PositionOnScreen.TOP -> adjustLt()
                PositionOnScreen.MIDDLE -> adjustLm()
                else -> adjustLb()
            }
        }
    }

    private fun adjustRt() {
        //TODO : from real data here
        bindingRt.smallSquareContainerRt.visibility = View.VISIBLE
        bindingRt.smallCircleContainerRt.visibility = View.VISIBLE
        bindingRt.arrowSquareRt.visibility = View.VISIBLE
        bindingRt.arrowCircleRt.visibility = View.VISIBLE
        bindingRt.smallSquareTextRt.visibility = View.VISIBLE
        bindingRt.smallCircleTextRt.visibility = View.VISIBLE
        bindingRt.smallSquareImageRt.visibility = View.VISIBLE
        bindingRt.smallCircleImageRt.visibility = View.VISIBLE
        bindingRt.smallCircleBackgroundImageRt.visibility = View.VISIBLE
        bindingRt.smallSquareBackgroundImageRt.visibility = View.VISIBLE
        bindingRt.bigContainerRt.visibility = View.GONE
        when (shape) {
            Shape.SHARP_EDGE -> {
                if (isBackgroundImage) {
                    Picasso.get()
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRt.smallSquareBackgroundImageRt)
                } else {
                    bindingRt.smallSquareContainerRt.setBackgroundColor(resources.getColor(R.color.blue))
                    bindingRt.smallSquareBackgroundImageRt.visibility = View.GONE
                }
                bindingRt.smallCircleContainerRt.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(40f, 0f, 0f, 40f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRt.smallSquareBackgroundImageRt)
                } else {
                    bindingRt.smallSquareContainerRt.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRt.smallSquareTextRt.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRt.smallSquareImageRt.setBackgroundResource(R.drawable.rounded_corners_left)
                    val gd = bindingRt.smallSquareContainerRt.background as GradientDrawable
                    gd.setColor(resources.getColor(R.color.blue))
                    val gdText = bindingRt.smallSquareTextRt.background as GradientDrawable
                    gdText.setColor(resources.getColor(R.color.blue))
                    val gdImage = bindingRt.smallSquareImageRt.background as GradientDrawable
                    gdImage.setColor(resources.getColor(R.color.blue))
                    bindingRt.smallSquareBackgroundImageRt.visibility = View.GONE
                }
                bindingRt.smallCircleContainerRt.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(500f, 0f, 0f, 500f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRt.smallCircleBackgroundImageRt)
                } else {
                    bindingRt.smallCircleContainerRt.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRt.smallCircleTextRt.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRt.smallCircleImageRt.setBackgroundResource(R.drawable.left_half_circle)
                    val gdCircle =
                        bindingRt.smallCircleContainerRt.background as GradientDrawable
                    gdCircle.setColor(resources.getColor(R.color.blue))
                    val gdCircleText =
                        bindingRt.smallCircleTextRt.background as GradientDrawable
                    gdCircleText.setColor(resources.getColor(R.color.blue))
                    val gdCircleImage =
                        bindingRt.smallCircleImageRt.background as GradientDrawable
                    gdCircleImage.setColor(resources.getColor(R.color.blue))
                    bindingRt.smallCircleBackgroundImageRt.visibility = View.GONE
                }
                bindingRt.smallSquareContainerRt.visibility = View.GONE
            }
        }
        if (shape == Shape.CIRCLE) {
            if (!isArrow) {
                bindingRt.arrowCircleRt.visibility = View.GONE
            }
            if (isExpanded) {
                bindingRt.arrowCircleRt.text = getString(R.string.notification_left_arrow)
            } else {
                bindingRt.arrowCircleRt.text = getString(R.string.notification_right_arrow)
            }
            bindingRt.arrowCircleRt.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingRt.smallCircleImageRt)
                bindingRt.smallCircleTextRt.visibility = View.GONE
            } else {
                bindingRt.smallCircleTextRt.text = "Discount"
                bindingRt.smallCircleTextRt.setTextColor(resources.getColor(R.color.white))
                bindingRt.smallCircleTextRt.setTypeface(Typeface.MONOSPACE)
                bindingRt.smallCircleImageRt.visibility = View.GONE
                bindingRt.smallCircleTextRt.topDown = isTopToBottom
                bindingRt.smallCircleTextRt.isCircle = true
                bindingRt.smallCircleTextRt.isRight = isRight
            }
            bindingRt.smallCircleContainerRt.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingRt.bigContainerRt.visibility = View.GONE
                    bindingRt.arrowCircleRt.text = getString(R.string.notification_right_arrow)
                } else {
                    isExpanded = true
                    bindingRt.bigContainerRt.visibility = View.VISIBLE
                    bindingRt.arrowCircleRt.text = getString(R.string.notification_left_arrow)
                }
            }
        } else {
            if (!isArrow) {
                bindingRt.arrowSquareRt.visibility = View.GONE
            }
            if (isExpanded) {
                bindingRt.arrowSquareRt.text = getString(R.string.notification_left_arrow)
            } else {
                bindingRt.arrowSquareRt.text = getString(R.string.notification_right_arrow)
            }
            bindingRt.arrowSquareRt.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingRt.smallSquareImageRt)
                bindingRt.smallSquareTextRt.visibility = View.GONE
            } else {
                bindingRt.smallSquareTextRt.text = "Discount"
                bindingRt.smallSquareTextRt.setTextColor(resources.getColor(R.color.white))
                bindingRt.smallSquareTextRt.typeface = Typeface.MONOSPACE
                bindingRt.smallSquareImageRt.visibility = View.GONE
                bindingRt.smallSquareTextRt.topDown = isTopToBottom
                bindingRt.smallCircleTextRt.isCircle = false
                bindingRt.smallCircleTextRt.isRight = isRight
            }
            bindingRt.smallSquareContainerRt.setOnClickListener {
                if (isExpanded) {
                    isExpanded = false
                    bindingRt.bigContainerRt.visibility = View.GONE
                    bindingRt.arrowSquareRt.text = getString(R.string.notification_right_arrow)
                } else {
                    isExpanded = true
                    bindingRt.bigContainerRt.visibility = View.VISIBLE
                    bindingRt.arrowSquareRt.text = getString(R.string.notification_left_arrow)
                }
            }
        }
        if (isBackgroundImage) {
            Picasso.get()
                .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                .into(bindingRt.bigBackgroundImageRt)
        } else {
            bindingRt.bigContainerRt.setBackgroundColor(resources.getColor(R.color.blue))
            bindingRt.bigBackgroundImageRt.visibility = View.GONE
        }
        Picasso.get().load("https://upload.wikimedia.org//wikipedia/en/a/a9/MarioNSMBUDeluxe.png")
            .into(bindingRt.bigImageRt)
        bindingRt.bigContainerRt.setOnClickListener { v ->
            // TODO : Check buttonInterface first
            // TODO : send report here
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
            activity.startActivity(viewIntent)
            endFragment()
        }
    }

    private fun adjustRm() {
        //TODO : from real data here
        bindingRm.smallSquareContainerRm.visibility = View.VISIBLE
        bindingRm.smallCircleContainerRm.visibility = View.VISIBLE
        bindingRm.arrowSquareRm.visibility = View.VISIBLE
        bindingRm.arrowCircleRm.visibility = View.VISIBLE
        bindingRm.smallSquareTextRm.visibility = View.VISIBLE
        bindingRm.smallCircleTextRm.visibility = View.VISIBLE
        bindingRm.smallSquareImageRm.visibility = View.VISIBLE
        bindingRm.smallCircleImageRm.visibility = View.VISIBLE
        bindingRm.smallCircleBackgroundImageRm.visibility = View.VISIBLE
        bindingRm.smallSquareBackgroundImageRm.visibility = View.VISIBLE
        bindingRm.bigContainerRm.visibility = View.GONE
        when (shape) {
            Shape.SHARP_EDGE -> {
                if (isBackgroundImage) {
                    Picasso.get()
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRm.smallSquareBackgroundImageRm)
                } else {
                    bindingRm.smallSquareContainerRm.setBackgroundColor(resources.getColor(R.color.blue))
                    bindingRm.smallSquareBackgroundImageRm.visibility = View.GONE
                }
                bindingRm.smallCircleContainerRm.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(40f, 0f, 0f, 40f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRm.smallSquareBackgroundImageRm)
                } else {
                    bindingRm.smallSquareContainerRm.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRm.smallSquareTextRm.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRm.smallSquareImageRm.setBackgroundResource(R.drawable.rounded_corners_left)
                    val gd = bindingRm.smallSquareContainerRm.background as GradientDrawable
                    gd.setColor(resources.getColor(R.color.blue))
                    val gdText = bindingRm.smallSquareTextRm.background as GradientDrawable
                    gdText.setColor(resources.getColor(R.color.blue))
                    val gdImage = bindingRm.smallSquareImageRm.background as GradientDrawable
                    gdImage.setColor(resources.getColor(R.color.blue))
                    bindingRm.smallSquareBackgroundImageRm.visibility = View.GONE
                }
                bindingRm.smallCircleContainerRm.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(500f, 0f, 0f, 500f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRm.smallCircleBackgroundImageRm)
                } else {
                    bindingRm.smallCircleContainerRm.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRm.smallCircleTextRm.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRm.smallCircleImageRm.setBackgroundResource(R.drawable.left_half_circle)
                    val gdCircle =
                        bindingRm.smallCircleContainerRm.background as GradientDrawable
                    gdCircle.setColor(resources.getColor(R.color.blue))
                    val gdCircleText =
                        bindingRm.smallCircleTextRm.background as GradientDrawable
                    gdCircleText.setColor(resources.getColor(R.color.blue))
                    val gdCircleImage =
                        bindingRm.smallCircleImageRm.background as GradientDrawable
                    gdCircleImage.setColor(resources.getColor(R.color.blue))
                    bindingRm.smallCircleBackgroundImageRm.visibility = View.GONE
                }
                bindingRm.smallSquareContainerRm.visibility = View.GONE
            }
        }
        if (shape == Shape.CIRCLE) {
            if (!isArrow) {
                bindingRm.arrowCircleRm.visibility = View.GONE
            }
            if (isExpanded) {
                bindingRm.arrowCircleRm.text = getString(R.string.notification_left_arrow)
            } else {
                bindingRm.arrowCircleRm.text = getString(R.string.notification_right_arrow)
            }
            bindingRm.arrowCircleRm.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingRm.smallCircleImageRm)
                bindingRm.smallCircleTextRm.visibility = View.GONE
            } else {
                bindingRm.smallCircleTextRm.text = "Discount"
                bindingRm.smallCircleTextRm.setTextColor(resources.getColor(R.color.white))
                bindingRm.smallCircleTextRm.setTypeface(Typeface.MONOSPACE)
                bindingRm.smallCircleImageRm.visibility = View.GONE
                bindingRm.smallCircleTextRm.topDown = isTopToBottom
                bindingRm.smallCircleTextRm.isCircle = true
                bindingRm.smallCircleTextRm.isRight = isRight
            }
            bindingRm.smallCircleContainerRm.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingRm.bigContainerRm.visibility = View.GONE
                    bindingRm.arrowCircleRm.text = getString(R.string.notification_right_arrow)
                } else {
                    isExpanded = true
                    bindingRm.bigContainerRm.visibility = View.VISIBLE
                    bindingRm.arrowCircleRm.text = getString(R.string.notification_left_arrow)
                }
            }
        } else {
            if (!isArrow) {
                bindingRm.arrowSquareRm.visibility = View.GONE
            }
            if (isExpanded) {
                bindingRm.arrowSquareRm.text = getString(R.string.notification_left_arrow)
            } else {
                bindingRm.arrowSquareRm.text = getString(R.string.notification_right_arrow)
            }
            bindingRm.arrowSquareRm.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingRm.smallSquareImageRm)
                bindingRm.smallSquareTextRm.visibility = View.GONE
            } else {
                bindingRm.smallSquareTextRm.text = "Discount"
                bindingRm.smallSquareTextRm.setTextColor(resources.getColor(R.color.white))
                bindingRm.smallSquareTextRm.setTypeface(Typeface.MONOSPACE)
                bindingRm.smallSquareImageRm.visibility = View.GONE
                bindingRm.smallSquareTextRm.topDown = isTopToBottom
                bindingRm.smallCircleTextRm.isCircle = false
                bindingRm.smallCircleTextRm.isRight = isRight
            }
            bindingRm.smallSquareContainerRm.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingRm.bigContainerRm.visibility = View.GONE
                    bindingRm.arrowSquareRm.text = getString(R.string.notification_right_arrow)
                } else {
                    isExpanded = true
                    bindingRm.bigContainerRm.visibility = View.VISIBLE
                    bindingRm.arrowSquareRm.text = getString(R.string.notification_left_arrow)
                }
            }
        }
        if (isBackgroundImage) {
            Picasso.get()
                .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                .into(bindingRm.bigBackgroundImageRm)
        } else {
            bindingRm.bigContainerRm.setBackgroundColor(resources.getColor(R.color.blue))
            bindingRm.bigBackgroundImageRm.visibility = View.GONE
        }
        Picasso.get().load("https://upload.wikimedia.org//wikipedia/en/a/a9/MarioNSMBUDeluxe.png")
            .into(bindingRm.bigImageRm)
        bindingRm.bigContainerRm.setOnClickListener { v ->
            // TODO : Check buttonInterface first
            // TODO : send report here
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
            activity.startActivity(viewIntent)
            endFragment()
        }
    }

    private fun adjustRb() {
        //TODO : from real data here
        bindingRb.smallSquareContainerRb.visibility = View.VISIBLE
        bindingRb.smallCircleContainerRb.visibility = View.VISIBLE
        bindingRb.arrowSquareRb.visibility = View.VISIBLE
        bindingRb.arrowCircleRb.visibility = View.VISIBLE
        bindingRb.smallSquareTextRb.visibility = View.VISIBLE
        bindingRb.smallCircleTextRb.visibility = View.VISIBLE
        bindingRb.smallSquareImageRb.visibility = View.VISIBLE
        bindingRb.smallCircleImageRb.visibility = View.VISIBLE
        bindingRb.smallCircleBackgroundImageRb.visibility = View.VISIBLE
        bindingRb.smallSquareBackgroundImageRb.visibility = View.VISIBLE
        bindingRb.bigContainerRb.visibility = View.GONE
        when (shape) {
            Shape.SHARP_EDGE -> {
                if (isBackgroundImage) {
                    Picasso.get()
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRb.smallSquareBackgroundImageRb)
                } else {
                    bindingRb.smallSquareContainerRb.setBackgroundColor(resources.getColor(R.color.blue))
                    bindingRb.smallSquareBackgroundImageRb.setVisibility(View.GONE)
                }
                bindingRb.smallCircleContainerRb.setVisibility(View.GONE)
            }
            Shape.SOFT_EDGE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(40f, 0f, 0f, 40f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRb.smallSquareBackgroundImageRb)
                } else {
                    bindingRb.smallSquareContainerRb.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRb.smallSquareTextRb.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRb.smallSquareImageRb.setBackgroundResource(R.drawable.rounded_corners_left)
                    val gd = bindingRb.smallSquareContainerRb.background as GradientDrawable
                    gd.setColor(resources.getColor(R.color.blue))
                    val gdText = bindingRb.smallSquareTextRb.background as GradientDrawable
                    gdText.setColor(resources.getColor(R.color.blue))
                    val gdImage = bindingRb.smallSquareImageRb.background as GradientDrawable
                    gdImage.setColor(resources.getColor(R.color.blue))
                    bindingRb.smallSquareBackgroundImageRb.visibility = View.GONE
                }
                bindingRb.smallCircleContainerRb.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(500f, 0f, 0f, 500f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingRb.smallCircleBackgroundImageRb)
                } else {
                    bindingRb.smallCircleContainerRb.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRb.smallCircleTextRb.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRb.smallCircleImageRb.setBackgroundResource(R.drawable.left_half_circle)
                    val gdCircle =
                        bindingRb.smallCircleContainerRb.background as GradientDrawable
                    gdCircle.setColor(resources.getColor(R.color.blue))
                    val gdCircleText =
                        bindingRb.smallCircleTextRb.background as GradientDrawable
                    gdCircleText.setColor(resources.getColor(R.color.blue))
                    val gdCircleImage =
                        bindingRb.smallCircleImageRb.background as GradientDrawable
                    gdCircleImage.setColor(resources.getColor(R.color.blue))
                    bindingRb.smallCircleBackgroundImageRb.visibility = View.GONE
                }
                bindingRb.smallSquareContainerRb.visibility = View.GONE
            }
        }
        if (shape == Shape.CIRCLE) {
            if (!isArrow) {
                bindingRb.arrowCircleRb.visibility = View.GONE
            }
            if (isExpanded) {
                bindingRb.arrowCircleRb.text = getString(R.string.notification_left_arrow)
            } else {
                bindingRb.arrowCircleRb.text = getString(R.string.notification_right_arrow)
            }
            bindingRb.arrowCircleRb.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingRb.smallCircleImageRb)
                bindingRb.smallCircleTextRb.visibility = View.GONE
            } else {
                bindingRb.smallCircleTextRb.text = "Discount"
                bindingRb.smallCircleTextRb.setTextColor(resources.getColor(R.color.white))
                bindingRb.smallCircleTextRb.setTypeface(Typeface.MONOSPACE)
                bindingRb.smallCircleImageRb.visibility = View.GONE
                bindingRb.smallCircleTextRb.topDown = isTopToBottom
                bindingRb.smallCircleTextRb.isCircle = true
                bindingRb.smallCircleTextRb.isRight = isRight
            }
            bindingRb.smallCircleContainerRb.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingRb.bigContainerRb.visibility = View.GONE
                    bindingRb.arrowCircleRb.text = getString(R.string.notification_right_arrow)
                } else {
                    isExpanded = true
                    bindingRb.bigContainerRb.visibility = View.VISIBLE
                    bindingRb.arrowCircleRb.text = getString(R.string.notification_left_arrow)
                }
            }
        } else {
            if (!isArrow) {
                bindingRb.arrowSquareRb.visibility = View.GONE
            }
            if (isExpanded) {
                bindingRb.arrowSquareRb.text = getString(R.string.notification_left_arrow)
            } else {
                bindingRb.arrowSquareRb.text = getString(R.string.notification_right_arrow)
            }
            bindingRb.arrowSquareRb.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingRb.smallSquareImageRb)
                bindingRb.smallSquareTextRb.visibility = View.GONE
            } else {
                bindingRb.smallSquareTextRb.text = "Discount"
                bindingRb.smallSquareTextRb.setTextColor(resources.getColor(R.color.white))
                bindingRb.smallSquareTextRb.typeface = Typeface.MONOSPACE
                bindingRb.smallSquareImageRb.visibility = View.GONE
                bindingRb.smallSquareTextRb.topDown = isTopToBottom
                bindingRb.smallCircleTextRb.isCircle = false
                bindingRb.smallCircleTextRb.isRight = isRight
            }
            bindingRb.smallSquareContainerRb.setOnClickListener {
                if (isExpanded) {
                    isExpanded = false
                    bindingRb.bigContainerRb.visibility = View.GONE
                    bindingRb.arrowSquareRb.text = getString(R.string.notification_right_arrow)
                } else {
                    isExpanded = true
                    bindingRb.bigContainerRb.visibility = View.VISIBLE
                    bindingRb.arrowSquareRb.text = getString(R.string.notification_left_arrow)
                }
            }
        }
        if (isBackgroundImage) {
            Picasso.get()
                .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                .into(bindingRb.bigBackgroundImageRb)
        } else {
            bindingRb.bigContainerRb.setBackgroundColor(resources.getColor(R.color.blue))
            bindingRb.bigBackgroundImageRb.visibility = View.GONE
        }
        Picasso.get().load("https://upload.wikimedia.org//wikipedia/en/a/a9/MarioNSMBUDeluxe.png")
            .into(bindingRb.bigImageRb)
        bindingRb.bigContainerRb.setOnClickListener {
            // TODO : Check buttonInterface first
            // TODO : send report here
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
            activity.startActivity(viewIntent)
            endFragment()
        }
    }

    private fun adjustLt() {
        //TODO : from real data here
        bindingLt.smallSquareContainerLt.visibility = View.VISIBLE
        bindingLt.smallCircleContainerLt.visibility = View.VISIBLE
        bindingLt.arrowSquareLt.visibility = View.VISIBLE
        bindingLt.arrowCircleLt.visibility = View.VISIBLE
        bindingLt.smallSquareTextLt.visibility = View.VISIBLE
        bindingLt.smallCircleTextLt.visibility = View.VISIBLE
        bindingLt.smallSquareImageLt.visibility = View.VISIBLE
        bindingLt.smallCircleImageLt.visibility = View.VISIBLE
        bindingLt.smallCircleBackgroundImageLt.visibility = View.VISIBLE
        bindingLt.smallSquareBackgroundImageLt.visibility = View.VISIBLE
        bindingLt.bigContainerLt.visibility = View.GONE
        when (shape) {
            Shape.SHARP_EDGE -> {
                if (isBackgroundImage) {
                    Picasso.get()
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLt.smallSquareBackgroundImageLt)
                } else {
                    bindingLt.smallSquareContainerLt.setBackgroundColor(resources.getColor(R.color.blue))
                    bindingLt.smallSquareBackgroundImageLt.visibility = View.GONE
                }
                bindingLt.smallCircleContainerLt.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 40f, 40f, 0f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLt.smallSquareBackgroundImageLt)
                } else {
                    bindingLt.smallSquareContainerLt.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLt.smallSquareTextLt.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLt.smallSquareImageLt.setBackgroundResource(R.drawable.rounded_corners_right)
                    val gd = bindingLt.smallSquareContainerLt.background as GradientDrawable
                    gd.setColor(resources.getColor(R.color.blue))
                    val gdText = bindingLt.smallSquareTextLt.background as GradientDrawable
                    gdText.setColor(resources.getColor(R.color.blue))
                    val gdImage = bindingLt.smallSquareImageLt.background as GradientDrawable
                    gdImage.setColor(resources.getColor(R.color.blue))
                    bindingLt.smallSquareBackgroundImageLt.visibility = View.GONE
                }
                bindingLt.smallCircleContainerLt.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 500f, 500f, 0f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLt.smallCircleBackgroundImageLt)
                } else {
                    bindingLt.smallCircleContainerLt.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLt.smallCircleTextLt.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLt.smallCircleImageLt.setBackgroundResource(R.drawable.right_half_circle)
                    val gdCircle =
                        bindingLt.smallCircleContainerLt.background as GradientDrawable
                    gdCircle.setColor(resources.getColor(R.color.blue))
                    val gdCircleText =
                        bindingLt.smallCircleTextLt.background as GradientDrawable
                    gdCircleText.setColor(resources.getColor(R.color.blue))
                    val gdCircleImage =
                        bindingLt.smallCircleImageLt.background as GradientDrawable
                    gdCircleImage.setColor(resources.getColor(R.color.blue))
                    bindingLt.smallCircleBackgroundImageLt.visibility = View.GONE
                }
                bindingLt.smallSquareContainerLt.visibility = View.GONE
            }
        }
        if (shape == Shape.CIRCLE) {
            if (!isArrow) {
                bindingLt.arrowCircleLt.visibility = View.GONE
            }
            if (isExpanded) {
                bindingLt.arrowCircleLt.text = getString(R.string.notification_right_arrow)
            } else {
                bindingLt.arrowCircleLt.text = getString(R.string.notification_left_arrow)
            }
            bindingLt.arrowCircleLt.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingLt.smallCircleImageLt)
                bindingLt.smallCircleTextLt.visibility = View.GONE
            } else {
                bindingLt.smallCircleTextLt.text = "Discount"
                bindingLt.smallCircleTextLt.setTextColor(resources.getColor(R.color.white))
                bindingLt.smallCircleTextLt.typeface = Typeface.MONOSPACE
                bindingLt.smallCircleImageLt.visibility = View.GONE
                bindingLt.smallCircleTextLt.topDown = isTopToBottom
                bindingLt.smallCircleTextLt.isCircle = true
                bindingLt.smallCircleTextLt.isRight = isRight
            }
            bindingLt.smallCircleContainerLt.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingLt.bigContainerLt.visibility = View.GONE
                    bindingLt.arrowCircleLt.text = getString(R.string.notification_left_arrow)
                } else {
                    isExpanded = true
                    bindingLt.bigContainerLt.visibility = View.VISIBLE
                    bindingLt.arrowCircleLt.text = getString(R.string.notification_right_arrow)
                }
            }
        } else {
            if (!isArrow) {
                bindingLt.arrowSquareLt.visibility = View.GONE
            }
            if (isExpanded) {
                bindingLt.arrowSquareLt.text = getString(R.string.notification_right_arrow)
            } else {
                bindingLt.arrowSquareLt.text = getString(R.string.notification_left_arrow)
            }
            bindingLt.arrowSquareLt.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingLt.smallSquareImageLt)
                bindingLt.smallSquareTextLt.visibility = View.GONE
            } else {
                bindingLt.smallSquareTextLt.text = "Discount"
                bindingLt.smallSquareTextLt.setTextColor(resources.getColor(R.color.white))
                bindingLt.smallSquareTextLt.typeface = Typeface.MONOSPACE
                bindingLt.smallSquareImageLt.visibility = View.GONE
                bindingLt.smallSquareTextLt.topDown = isTopToBottom
                bindingLt.smallCircleTextLt.isCircle = false
                bindingLt.smallCircleTextLt.isRight = isRight
            }
            bindingLt.smallSquareContainerLt.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingLt.bigContainerLt.visibility = View.GONE
                    bindingLt.arrowSquareLt.text = getString(R.string.notification_left_arrow)
                } else {
                    isExpanded = true
                    bindingLt.bigContainerLt.visibility = View.VISIBLE
                    bindingLt.arrowSquareLt.text = getString(R.string.notification_right_arrow)
                }
            }
        }
        if (isBackgroundImage) {
            Picasso.get()
                .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                .into(bindingLt.bigBackgroundImageLt)
        } else {
            bindingLt.bigContainerLt.setBackgroundColor(resources.getColor(R.color.blue))
            bindingLt.bigBackgroundImageLt.visibility = View.GONE
        }
        Picasso.get().load("https://upload.wikimedia.org//wikipedia/en/a/a9/MarioNSMBUDeluxe.png")
            .into(bindingLt.bigImageLt)
        bindingLt.bigContainerLt.setOnClickListener {
            // TODO : Check buttonInterface first
            // TODO : send report here
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
            activity.startActivity(viewIntent)
            endFragment()
        }
    }

    private fun adjustLm() {
        //TODO : from real data here
        bindingLm.smallSquareContainerLm.visibility = View.VISIBLE
        bindingLm.smallCircleContainerLm.visibility = View.VISIBLE
        bindingLm.arrowSquareLm.visibility = View.VISIBLE
        bindingLm.arrowCircleLm.visibility = View.VISIBLE
        bindingLm.smallSquareTextLm.visibility = View.VISIBLE
        bindingLm.smallCircleTextLm.visibility = View.VISIBLE
        bindingLm.smallSquareImageLm.visibility = View.VISIBLE
        bindingLm.smallCircleImageLm.visibility = View.VISIBLE
        bindingLm.smallCircleBackgroundImageLm.visibility = View.VISIBLE
        bindingLm.smallSquareBackgroundImageLm.visibility = View.VISIBLE
        bindingLm.bigContainerLm.visibility = View.GONE
        when (shape) {
            Shape.SHARP_EDGE -> {
                if (isBackgroundImage) {
                    Picasso.get()
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLm.smallSquareBackgroundImageLm)
                } else {
                    bindingLm.smallSquareContainerLm.setBackgroundColor(resources.getColor(R.color.blue))
                    bindingLm.smallSquareBackgroundImageLm.visibility = View.GONE
                }
                bindingLm.smallCircleContainerLm.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 40f, 40f, 0f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLm.smallSquareBackgroundImageLm)
                } else {
                    bindingLm.smallSquareContainerLm.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLm.smallSquareTextLm.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLm.smallSquareImageLm.setBackgroundResource(R.drawable.rounded_corners_right)
                    val gd = bindingLm.smallSquareContainerLm.background as GradientDrawable
                    gd.setColor(resources.getColor(R.color.blue))
                    val gdText = bindingLm.smallSquareTextLm.background as GradientDrawable
                    gdText.setColor(resources.getColor(R.color.blue))
                    val gdImage = bindingLm.smallSquareImageLm.background as GradientDrawable
                    gdImage.setColor(resources.getColor(R.color.blue))
                    bindingLm.smallSquareBackgroundImageLm.visibility = View.GONE
                }
                bindingLm.smallCircleContainerLm.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 500f, 500f, 0f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLm.smallCircleBackgroundImageLm)
                } else {
                    bindingLm.smallCircleContainerLm.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLm.smallCircleTextLm.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLm.smallCircleImageLm.setBackgroundResource(R.drawable.right_half_circle)
                    val gdCircle =
                        bindingLm.smallCircleContainerLm.background as GradientDrawable
                    gdCircle.setColor(resources.getColor(R.color.blue))
                    val gdCircleText =
                        bindingLm.smallCircleTextLm.background as GradientDrawable
                    gdCircleText.setColor(resources.getColor(R.color.blue))
                    val gdCircleImage =
                        bindingLm.smallCircleImageLm.background as GradientDrawable
                    gdCircleImage.setColor(resources.getColor(R.color.blue))
                    bindingLm.smallCircleBackgroundImageLm.setVisibility(View.GONE)
                }
                bindingLm.smallSquareContainerLm.visibility = View.GONE
            }
        }
        if (shape == Shape.CIRCLE) {
            if (!isArrow) {
                bindingLm.arrowCircleLm.visibility = View.GONE
            }
            if (isExpanded) {
                bindingLm.arrowCircleLm.text = getString(R.string.notification_right_arrow)
            } else {
                bindingLm.arrowCircleLm.text = getString(R.string.notification_left_arrow)
            }
            bindingLm.arrowCircleLm.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingLm.smallCircleImageLm)
                bindingLm.smallCircleTextLm.visibility = View.GONE
            } else {
                bindingLm.smallCircleTextLm.text = "Discount"
                bindingLm.smallCircleTextLm.setTextColor(resources.getColor(R.color.white))
                bindingLm.smallCircleTextLm.typeface = Typeface.MONOSPACE
                bindingLm.smallCircleImageLm.visibility = View.GONE
                bindingLm.smallCircleTextLm.topDown = isTopToBottom
                bindingLm.smallCircleTextLm.isCircle = true
                bindingLm.smallCircleTextLm.isRight = isRight
            }
            bindingLm.smallCircleContainerLm.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingLm.bigContainerLm.visibility = View.GONE
                    bindingLm.arrowCircleLm.text = getString(R.string.notification_left_arrow)
                } else {
                    isExpanded = true
                    bindingLm.bigContainerLm.visibility = View.VISIBLE
                    bindingLm.arrowCircleLm.text = getString(R.string.notification_right_arrow)
                }
            }
        } else {
            if (!isArrow) {
                bindingLm.arrowSquareLm.visibility = View.GONE
            }
            if (isExpanded) {
                bindingLm.arrowSquareLm.text = getString(R.string.notification_right_arrow)
            } else {
                bindingLm.arrowSquareLm.text = getString(R.string.notification_left_arrow)
            }
            bindingLm.arrowSquareLm.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingLm.smallSquareImageLm)
                bindingLm.smallSquareTextLm.visibility = View.GONE
            } else {
                bindingLm.smallSquareTextLm.text = "Discount"
                bindingLm.smallSquareTextLm.setTextColor(resources.getColor(R.color.white))
                bindingLm.smallSquareTextLm.setTypeface(Typeface.MONOSPACE)
                bindingLm.smallSquareImageLm.visibility = View.GONE
                bindingLm.smallSquareTextLm.topDown = isTopToBottom
                bindingLm.smallCircleTextLm.isCircle = false
                bindingLm.smallCircleTextLm.isRight = isRight
            }
            bindingLm.smallSquareContainerLm.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingLm.bigContainerLm.visibility = View.GONE
                    bindingLm.arrowSquareLm.text = getString(R.string.notification_left_arrow)
                } else {
                    isExpanded = true
                    bindingLm.bigContainerLm.visibility = View.VISIBLE
                    bindingLm.arrowSquareLm.text = getString(R.string.notification_right_arrow)
                }
            }
        }
        if (isBackgroundImage) {
            Picasso.get()
                .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                .into(bindingLm.bigBackgroundImageLm)
        } else {
            bindingLm.bigContainerLm.setBackgroundColor(resources.getColor(R.color.blue))
            bindingLm.bigBackgroundImageLm.visibility = View.GONE
        }
        Picasso.get().load("https://upload.wikimedia.org//wikipedia/en/a/a9/MarioNSMBUDeluxe.png")
            .into(bindingLm.bigImageLm)
        bindingLm.bigContainerLm.setOnClickListener {
            // TODO : Check buttonInterface first
            // TODO : send report here
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
            activity.startActivity(viewIntent)
            endFragment()
        }
    }

    private fun adjustLb() {
        //TODO : from real data here
        bindingLb.smallSquareContainerLb.visibility = View.VISIBLE
        bindingLb.smallCircleContainerLb.visibility = View.VISIBLE
        bindingLb.arrowSquareLb.visibility = View.VISIBLE
        bindingLb.arrowCircleLb.visibility = View.VISIBLE
        bindingLb.smallSquareTextLb.visibility = View.VISIBLE
        bindingLb.smallCircleTextLb.visibility = View.VISIBLE
        bindingLb.smallSquareImageLb.visibility = View.VISIBLE
        bindingLb.smallCircleImageLb.visibility = View.VISIBLE
        bindingLb.smallCircleBackgroundImageLb.visibility = View.VISIBLE
        bindingLb.smallSquareBackgroundImageLb.visibility = View.VISIBLE
        bindingLb.bigContainerLb.visibility = View.GONE
        when (shape) {
            Shape.SHARP_EDGE -> {
                if (isBackgroundImage) {
                    Picasso.get()
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLb.smallSquareBackgroundImageLb)
                } else {
                    bindingLb.smallSquareContainerLb.setBackgroundColor(resources.getColor(R.color.blue))
                    bindingLb.smallSquareBackgroundImageLb.visibility = View.GONE
                }
                bindingLb.smallCircleContainerLb.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 40f, 40f, 0f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLb.smallSquareBackgroundImageLb)
                } else {
                    bindingLb.smallSquareContainerLb.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLb.smallSquareTextLb.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLb.smallSquareImageLb.setBackgroundResource(R.drawable.rounded_corners_right)
                    val gd = bindingLb.smallSquareContainerLb.background as GradientDrawable
                    gd.setColor(resources.getColor(R.color.blue))
                    val gdText = bindingLb.smallSquareTextLb.background as GradientDrawable
                    gdText.setColor(resources.getColor(R.color.blue))
                    val gdImage = bindingLb.smallSquareImageLb.background as GradientDrawable
                    gdImage.setColor(resources.getColor(R.color.blue))
                    bindingLb.smallSquareBackgroundImageLb.visibility = View.GONE
                }
                bindingLb.smallCircleContainerLb.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isBackgroundImage) {
                    Glide.with(activity)
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 500f, 500f, 0f)
                            )
                        )
                        .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                        .into(bindingLb.smallCircleBackgroundImageLb)
                } else {
                    bindingLb.smallCircleContainerLb.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLb.smallCircleTextLb.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLb.smallCircleImageLb.setBackgroundResource(R.drawable.right_half_circle)
                    val gdCircle =
                        bindingLb.smallCircleContainerLb.background as GradientDrawable
                    gdCircle.setColor(resources.getColor(R.color.blue))
                    val gdCircleText =
                        bindingLb.smallCircleTextLb.background as GradientDrawable
                    gdCircleText.setColor(resources.getColor(R.color.blue))
                    val gdCircleImage =
                        bindingLb.smallCircleImageLb.background as GradientDrawable
                    gdCircleImage.setColor(resources.getColor(R.color.blue))
                    bindingLb.smallCircleBackgroundImageLb.setVisibility(View.GONE)
                }
                bindingLb.smallSquareContainerLb.visibility = View.GONE
            }
        }
        if (shape == Shape.CIRCLE) {
            if (!isArrow) {
                bindingLb.arrowCircleLb.visibility = View.GONE
            }
            if (isExpanded) {
                bindingLb.arrowCircleLb.text = getString(R.string.notification_right_arrow)
            } else {
                bindingLb.arrowCircleLb.text = getString(R.string.notification_left_arrow)
            }
            bindingLb.arrowCircleLb.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingLb.smallCircleImageLb)
                bindingLb.smallCircleTextLb.visibility = View.GONE
            } else {
                bindingLb.smallCircleTextLb.text = "Discount"
                bindingLb.smallCircleTextLb.setTextColor(resources.getColor(R.color.white))
                bindingLb.smallCircleTextLb.typeface = Typeface.MONOSPACE
                bindingLb.smallCircleImageLb.visibility = View.GONE
                bindingLb.smallCircleTextLb.topDown = isTopToBottom
                bindingLb.smallCircleTextLb.isCircle = true
                bindingLb.smallCircleTextLb.isRight = isRight
            }
            bindingLb.smallCircleContainerLb.setOnClickListener {
                if (isExpanded) {
                    isExpanded = false
                    bindingLb.bigContainerLb.visibility = View.GONE
                    bindingLb.arrowCircleLb.text = getString(R.string.notification_left_arrow)
                } else {
                    isExpanded = true
                    bindingLb.bigContainerLb.visibility = View.VISIBLE
                    bindingLb.arrowCircleLb.text = getString(R.string.notification_right_arrow)
                }
            }
        } else {
            if (!isArrow) {
                bindingLb.arrowSquareLb.visibility = View.GONE
            }
            if (isExpanded) {
                bindingLb.arrowSquareLb.text = getString(R.string.notification_right_arrow)
            } else {
                bindingLb.arrowSquareLb.text = getString(R.string.notification_left_arrow)
            }
            bindingLb.arrowSquareLb.setTextColor(resources.getColor(R.color.white))
            if (isSmallImage) {
                Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Circle-icons-mail.svg/2048px-Circle-icons-mail.svg.png")
                    .into(bindingLb.smallSquareImageLb)
                bindingLb.smallSquareTextLb.visibility = View.GONE
            } else {
                bindingLb.smallSquareTextLb.text = "Discount"
                bindingLb.smallSquareTextLb.setTextColor(resources.getColor(R.color.white))
                bindingLb.smallSquareTextLb.typeface = Typeface.MONOSPACE
                bindingLb.smallSquareImageLb.visibility = View.GONE
                bindingLb.smallSquareTextLb.topDown = isTopToBottom
                bindingLb.smallCircleTextLb.isCircle = false
                bindingLb.smallCircleTextLb.isRight = isRight
            }
            bindingLb.smallSquareContainerLb.setOnClickListener { v ->
                if (isExpanded) {
                    isExpanded = false
                    bindingLb.bigContainerLb.visibility = View.GONE
                    bindingLb.arrowSquareLb.text = getString(R.string.notification_left_arrow)
                } else {
                    isExpanded = true
                    bindingLb.bigContainerLb.visibility = View.VISIBLE
                    bindingLb.arrowSquareLb.text = getString(R.string.notification_right_arrow)
                }
            }
        }
        if (isBackgroundImage) {
            Picasso.get()
                .load("https://digitalsynopsis.com/wp-content/uploads/2019/11/color-schemes-palettes-feature-image.jpg")
                .into(bindingLb.bigBackgroundImageLb)
        } else {
            bindingLb.bigContainerLb.setBackgroundColor(resources.getColor(R.color.blue))
            bindingLb.bigBackgroundImageLb.visibility = View.GONE
        }
        Picasso.get().load("https://upload.wikimedia.org//wikipedia/en/a/a9/MarioNSMBUDeluxe.png")
            .into(bindingLb.bigImageLb)
        bindingLb.bigContainerLb.setOnClickListener { v ->
            // TODO : Check buttonInterface first
            // TODO : send report here
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.relateddigital.com/"))
            activity.startActivity(viewIntent)
            endFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // TODO : save mModel here
    }

    private fun endFragment() {
        if (activity != null) {
            activity.fragmentManager.beginTransaction().remove(this@InAppNotificationFragment)
                .commit()
        }
    }

    companion object {
        private const val LOG_TAG = "InAppNotification"
        private const val ARG_PARAM1 = "dataKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param model Parameter 1.
         * @return A new instance of fragment InAppNotificationFragment.
         */
        fun newInstance(model: InAppNotificationModel): InAppNotificationFragment {
            val fragment = InAppNotificationFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, model)
            fragment.arguments = args
            return fragment
        }
    }
}