package com.relateddigital.relateddigital_android.inapp.notification

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.*
import com.relateddigital.relateddigital_android.inapp.InAppButtonInterface
import com.relateddigital.relateddigital_android.model.Drawer
import com.relateddigital.relateddigital_android.model.DrawerExtendedProps
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import com.squareup.picasso.Picasso
import java.net.URI
import java.net.URISyntaxException

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

    private var response: Drawer? = null
    private var mExtendedProps: DrawerExtendedProps? = null
    private var isRight = true
    private lateinit var positionOnScreen: PositionOnScreen
    private var isTopToBottom = true
    private var isExpanded = false
    private var isSmallImage = false
    private var shape = Shape.SOFT_EDGE
    private var isArrow = false
    private var isMiniBackgroundImage = false
    private var isMaxiBackgroundImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        response = if (savedInstanceState != null) {
            savedInstanceState.getSerializable("drawer") as Drawer?
        } else {
            requireArguments().getSerializable(ARG_PARAM1) as Drawer?
        }

        if (response == null) {
            Log.e(LOG_TAG, "The data could not get properly!")
            endFragment()
        } else {
            try {
                mExtendedProps = Gson().fromJson(
                    URI(response!!.getActionData()!!.getExtendedProps()).path,
                    DrawerExtendedProps::class.java
                )
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                endFragment()
            } catch (e: Exception) {
                e.printStackTrace()
                endFragment()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view: View

        isRight = response!!.getActionData()!!.getPos() != "topLeft" &&
                response!!.getActionData()!!.getPos() != "left" &&
                response!!.getActionData()!!.getPos() != "bottomLeft"

        positionOnScreen = if (response!!.getActionData()!!.getPos() == "topRight" ||
            response!!.getActionData()!!.getPos() == "topLeft"
        ) {
            PositionOnScreen.TOP
        } else if (response!!.getActionData()!!.getPos() == "right" ||
            response!!.getActionData()!!.getPos() == "left"
        ) {
            PositionOnScreen.MIDDLE
        } else {
            PositionOnScreen.BOTTOM
        }

        isTopToBottom = mExtendedProps!!.getMiniTextOrientation() == "topToBottom"

        isSmallImage = !response!!.getActionData()!!.getContentMinimizedImage().isNullOrEmpty()

        shape = when(response!!.getActionData()!!.getShape()) {
            "circle" -> {
                Shape.CIRCLE
            }
            "roundedCorners" -> {
                Shape.SOFT_EDGE
            }
            else -> {
                Shape.SHARP_EDGE
            }
        }

        isArrow = !mExtendedProps!!.getArrowColor().isNullOrEmpty()

        isMiniBackgroundImage = !mExtendedProps!!.getMiniBackgroundImage().isNullOrEmpty()

        isMaxiBackgroundImage = !mExtendedProps!!.getMaxiBackgroundImage().isNullOrEmpty()

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
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Picasso.get().load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRt.smallSquareBackgroundImageRt)
                    }
                } else {
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        bindingRt.smallSquareContainerRt.setBackgroundColor(
                            Color.parseColor(mExtendedProps!!.getMiniBackgroundColor())
                        )
                    } else {
                        bindingRt.smallSquareContainerRt.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingRt.smallSquareBackgroundImageRt.visibility = View.GONE
                }
                bindingRt.smallCircleContainerRt.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(40f, 0f, 0f, 40f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRt.smallSquareBackgroundImageRt)
                    }
                } else {
                    bindingRt.smallSquareContainerRt.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRt.smallSquareTextRt.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRt.smallSquareImageRt.setBackgroundResource(R.drawable.rounded_corners_left)
                    val gd = bindingRt.smallSquareContainerRt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gd.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gd.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdText = bindingRt.smallSquareTextRt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdImage = bindingRt.smallSquareImageRt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingRt.smallSquareBackgroundImageRt.visibility = View.GONE
                }
                bindingRt.smallCircleContainerRt.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(500f, 0f, 0f, 500f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRt.smallCircleBackgroundImageRt)
                    }
                } else {
                    bindingRt.smallCircleContainerRt.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRt.smallCircleTextRt.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRt.smallCircleImageRt.setBackgroundResource(R.drawable.left_half_circle)
                    val gdCircle = bindingRt.smallCircleContainerRt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircle.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircle.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleText = bindingRt.smallCircleTextRt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleImage = bindingRt.smallCircleImageRt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingRt.arrowCircleRt.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingRt.arrowCircleRt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                Glide.with(requireActivity())
                    .asBitmap()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            GranularRoundedCorners(500f, 0f, 0f, 500f)
                        )
                    )
                    .load(response!!.getActionData()!!.getContentMinimizedImage())
                    .into(bindingRt.smallCircleImageRt)
                bindingRt.smallCircleTextRt.visibility = View.GONE
            } else {
                bindingRt.smallCircleTextRt.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingRt.smallCircleTextRt.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingRt.smallCircleTextRt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingRt.smallCircleTextRt.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingRt.smallCircleImageRt.visibility = View.GONE
                bindingRt.smallCircleTextRt.topDown = isTopToBottom
                bindingRt.smallCircleTextRt.isCircle = true
                bindingRt.smallCircleTextRt.isRight = isRight
            }
            bindingRt.smallCircleContainerRt.setOnClickListener {
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingRt.arrowSquareRt.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingRt.arrowSquareRt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                if (shape == Shape.SOFT_EDGE) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(40f, 0f, 0f, 40f)
                            )
                        )
                        .load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingRt.smallSquareImageRt)
                } else {
                    Picasso.get().load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingRt.smallSquareImageRt)
                }
                bindingRt.smallSquareTextRt.visibility = View.GONE
            } else {
                bindingRt.smallSquareTextRt.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingRt.smallSquareTextRt.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingRt.smallSquareTextRt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingRt.smallSquareTextRt.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
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

        if (isMaxiBackgroundImage) {
            Picasso.get().load(mExtendedProps!!.getMaxiBackgroundImage())
                .into(bindingRt.bigBackgroundImageRt)
        } else {
            if (!mExtendedProps!!.getMaxiBackgroundColor().isNullOrEmpty()) {
                bindingRt.bigContainerRt.setBackgroundColor(Color.parseColor(mExtendedProps!!.getMaxiBackgroundColor()))
            } else {
                bindingRt.bigContainerRt.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            bindingRt.bigBackgroundImageRt.visibility = View.GONE
        }

        if (!response!!.getActionData()!!.getContentMaximizedImage().isNullOrEmpty()) {
            Picasso.get().load(response!!.getActionData()!!.getContentMaximizedImage())
                .into(bindingRt.bigImageRt)
        }

        bindingRt.bigContainerRt.setOnClickListener {
            val uriString = response!!.getActionData()!!.getAndroidLnk()
            val buttonInterface: InAppButtonInterface? =
                RelatedDigital.getInAppButtonInterface()
            var report: MailSubReport?
            try {
                report = MailSubReport()
                report.impression = response!!.getActionData()!!.getReport()!!.getImpression()
                report.click = response!!.getActionData()!!.getReport()!!.getClick()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "There is no report to send!")
                e.printStackTrace()
                report = null
            }
            if (report != null) {
                InAppActionClickRequest.createInAppActionClickRequest(requireActivity(), report)
            }
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    val uri: Uri
                    try {
                        uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(
                            LOG_TAG,
                            "Can't parse notification URI, will not take any action",
                            e
                        )
                    }
                }
            }
        }
    }

    private fun adjustRm() {
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
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Picasso.get().load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRm.smallSquareBackgroundImageRm)
                    }
                } else {
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        bindingRm.smallSquareContainerRm.setBackgroundColor(
                            Color.parseColor(mExtendedProps!!.getMiniBackgroundColor())
                        )
                    } else {
                        bindingRm.smallSquareContainerRm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingRm.smallSquareBackgroundImageRm.visibility = View.GONE
                }
                bindingRm.smallCircleContainerRm.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(40f, 0f, 0f, 40f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRm.smallSquareBackgroundImageRm)
                    }
                } else {
                    bindingRm.smallSquareContainerRm.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRm.smallSquareTextRm.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRm.smallSquareImageRm.setBackgroundResource(R.drawable.rounded_corners_left)
                    val gd = bindingRm.smallSquareContainerRm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gd.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gd.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdText = bindingRm.smallSquareTextRm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdImage = bindingRm.smallSquareImageRm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingRm.smallSquareBackgroundImageRm.visibility = View.GONE
                }
                bindingRm.smallCircleContainerRm.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(500f, 0f, 0f, 500f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRm.smallCircleBackgroundImageRm)
                    }
                } else {
                    bindingRm.smallCircleContainerRm.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRm.smallCircleTextRm.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRm.smallCircleImageRm.setBackgroundResource(R.drawable.left_half_circle)
                    val gdCircle = bindingRm.smallCircleContainerRm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircle.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircle.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleText = bindingRm.smallCircleTextRm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleImage = bindingRm.smallCircleImageRm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingRm.arrowCircleRm.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingRm.arrowCircleRm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                Glide.with(requireActivity())
                    .asBitmap()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            GranularRoundedCorners(500f, 0f, 0f, 500f)
                        )
                    )
                    .load(response!!.getActionData()!!.getContentMinimizedImage())
                    .into(bindingRm.smallCircleImageRm)
                bindingRm.smallCircleTextRm.visibility = View.GONE
            } else {
                bindingRm.smallCircleTextRm.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingRm.smallCircleTextRm.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingRm.smallCircleTextRm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingRm.smallCircleTextRm.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingRm.smallCircleImageRm.visibility = View.GONE
                bindingRm.smallCircleTextRm.topDown = isTopToBottom
                bindingRm.smallCircleTextRm.isCircle = true
                bindingRm.smallCircleTextRm.isRight = isRight
            }
            bindingRm.smallCircleContainerRm.setOnClickListener {
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingRm.arrowSquareRm.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingRm.arrowSquareRm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                if (shape == Shape.SOFT_EDGE) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(40f, 0f, 0f, 40f)
                            )
                        )
                        .load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingRm.smallSquareImageRm)
                } else {
                    Picasso.get().load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingRm.smallSquareImageRm)
                }
                bindingRm.smallSquareTextRm.visibility = View.GONE
            } else {
                bindingRm.smallSquareTextRm.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingRm.smallSquareTextRm.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingRm.smallSquareTextRm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingRm.smallSquareTextRm.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingRm.smallSquareImageRm.visibility = View.GONE
                bindingRm.smallSquareTextRm.topDown = isTopToBottom
                bindingRm.smallCircleTextRm.isCircle = false
                bindingRm.smallCircleTextRm.isRight = isRight
            }
            bindingRm.smallSquareContainerRm.setOnClickListener {
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

        if (isMaxiBackgroundImage) {
            Picasso.get().load(mExtendedProps!!.getMaxiBackgroundImage())
                .into(bindingRm.bigBackgroundImageRm)
        } else {
            if (!mExtendedProps!!.getMaxiBackgroundColor().isNullOrEmpty()) {
                bindingRm.bigContainerRm.setBackgroundColor(Color.parseColor(mExtendedProps!!.getMaxiBackgroundColor()))
            } else {
                bindingRm.bigContainerRm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            bindingRm.bigBackgroundImageRm.visibility = View.GONE
        }

        if (!response!!.getActionData()!!.getContentMaximizedImage().isNullOrEmpty()) {
            Picasso.get().load(response!!.getActionData()!!.getContentMaximizedImage())
                .into(bindingRm.bigImageRm)
        }

        bindingRm.bigContainerRm.setOnClickListener {
            val uriString = response!!.getActionData()!!.getAndroidLnk()
            val buttonInterface: InAppButtonInterface? =
                RelatedDigital.getInAppButtonInterface()
            var report: MailSubReport?
            try {
                report = MailSubReport()
                report.impression = response!!.getActionData()!!.getReport()!!.getImpression()
                report.click = response!!.getActionData()!!.getReport()!!.getClick()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "There is no report to send!")
                e.printStackTrace()
                report = null
            }
            if (report != null) {
                InAppActionClickRequest.createInAppActionClickRequest(requireActivity(), report)
            }
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    val uri: Uri
                    try {
                        uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(
                            LOG_TAG,
                            "Can't parse notification URI, will not take any action",
                            e
                        )
                    }
                }
            }
        }
    }

    private fun adjustRb() {
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
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Picasso.get().load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRb.smallSquareBackgroundImageRb)
                    }
                } else {
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        bindingRb.smallSquareContainerRb.setBackgroundColor(
                            Color.parseColor(mExtendedProps!!.getMiniBackgroundColor())
                        )
                    } else {
                        bindingRb.smallSquareContainerRb.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingRb.smallSquareBackgroundImageRb.visibility = View.GONE
                }
                bindingRb.smallCircleContainerRb.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(40f, 0f, 0f, 40f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRb.smallSquareBackgroundImageRb)
                    }
                } else {
                    bindingRb.smallSquareContainerRb.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRb.smallSquareTextRb.setBackgroundResource(R.drawable.rounded_corners_left)
                    bindingRb.smallSquareImageRb.setBackgroundResource(R.drawable.rounded_corners_left)
                    val gd = bindingRb.smallSquareContainerRb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gd.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gd.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdText = bindingRb.smallSquareTextRb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdImage = bindingRb.smallSquareImageRb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingRb.smallSquareBackgroundImageRb.visibility = View.GONE
                }
                bindingRb.smallCircleContainerRb.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(500f, 0f, 0f, 500f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingRb.smallCircleBackgroundImageRb)
                    }
                } else {
                    bindingRb.smallCircleContainerRb.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRb.smallCircleTextRb.setBackgroundResource(R.drawable.left_half_circle)
                    bindingRb.smallCircleImageRb.setBackgroundResource(R.drawable.left_half_circle)
                    val gdCircle = bindingRb.smallCircleContainerRb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircle.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircle.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleText = bindingRb.smallCircleTextRb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleImage = bindingRb.smallCircleImageRb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingRb.arrowCircleRb.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingRb.arrowCircleRb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                Glide.with(requireActivity())
                    .asBitmap()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            GranularRoundedCorners(500f, 0f, 0f, 500f)
                        )
                    )
                    .load(response!!.getActionData()!!.getContentMinimizedImage())
                    .into(bindingRb.smallCircleImageRb)
                bindingRb.smallCircleTextRb.visibility = View.GONE
            } else {
                bindingRb.smallCircleTextRb.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingRb.smallCircleTextRb.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingRb.smallCircleTextRb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingRb.smallCircleTextRb.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingRb.smallCircleImageRb.visibility = View.GONE
                bindingRb.smallCircleTextRb.topDown = isTopToBottom
                bindingRb.smallCircleTextRb.isCircle = true
                bindingRb.smallCircleTextRb.isRight = isRight
            }
            bindingRb.smallCircleContainerRb.setOnClickListener {
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingRb.arrowSquareRb.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingRb.arrowSquareRb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                if (shape == Shape.SOFT_EDGE) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(40f, 0f, 0f, 40f)
                            )
                        )
                        .load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingRb.smallSquareImageRb)
                } else {
                    Picasso.get().load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingRb.smallSquareImageRb)
                }
                bindingRb.smallSquareTextRb.visibility = View.GONE
            } else {
                bindingRb.smallSquareTextRb.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingRb.smallSquareTextRb.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingRb.smallSquareTextRb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingRb.smallSquareTextRb.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
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

        if (isMaxiBackgroundImage) {
            Picasso.get().load(mExtendedProps!!.getMaxiBackgroundImage())
                .into(bindingRb.bigBackgroundImageRb)
        } else {
            if (!mExtendedProps!!.getMaxiBackgroundColor().isNullOrEmpty()) {
                bindingRb.bigContainerRb.setBackgroundColor(Color.parseColor(mExtendedProps!!.getMaxiBackgroundColor()))
            } else {
                bindingRb.bigContainerRb.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            bindingRb.bigBackgroundImageRb.visibility = View.GONE
        }

        if (!response!!.getActionData()!!.getContentMaximizedImage().isNullOrEmpty()) {
            Picasso.get().load(response!!.getActionData()!!.getContentMaximizedImage())
                .into(bindingRb.bigImageRb)
        }

        bindingRb.bigContainerRb.setOnClickListener {
            val uriString = response!!.getActionData()!!.getAndroidLnk()
            val buttonInterface: InAppButtonInterface? =
                RelatedDigital.getInAppButtonInterface()
            var report: MailSubReport?
            try {
                report = MailSubReport()
                report.impression = response!!.getActionData()!!.getReport()!!.getImpression()
                report.click = response!!.getActionData()!!.getReport()!!.getClick()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "There is no report to send!")
                e.printStackTrace()
                report = null
            }
            if (report != null) {
                InAppActionClickRequest.createInAppActionClickRequest(requireActivity(), report)
            }
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    val uri: Uri
                    try {
                        uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(
                            LOG_TAG,
                            "Can't parse notification URI, will not take any action",
                            e
                        )
                    }
                }
            }
        }
    }

    private fun adjustLt() {
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
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Picasso.get().load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLt.smallSquareBackgroundImageLt)
                    }
                } else {
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        bindingLt.smallSquareContainerLt.setBackgroundColor(
                            Color.parseColor(
                                mExtendedProps!!.getMiniBackgroundColor()
                            )
                        )
                    } else {
                        bindingLt.smallSquareContainerLt.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLt.smallSquareBackgroundImageLt.visibility = View.GONE
                }
                bindingLt.smallCircleContainerLt.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(0f, 40f, 40f, 0f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLt.smallSquareBackgroundImageLt)
                    }
                } else {
                    bindingLt.smallSquareContainerLt.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLt.smallSquareTextLt.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLt.smallSquareImageLt.setBackgroundResource(R.drawable.rounded_corners_right)
                    val gd = bindingLt.smallSquareContainerLt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gd.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gd.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdText = bindingLt.smallSquareTextLt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdImage = bindingLt.smallSquareImageLt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLt.smallSquareBackgroundImageLt.visibility = View.GONE
                }
                bindingLt.smallCircleContainerLt.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(0f, 500f, 500f, 0f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLt.smallCircleBackgroundImageLt)
                    }
                } else {
                    bindingLt.smallCircleContainerLt.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLt.smallCircleTextLt.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLt.smallCircleImageLt.setBackgroundResource(R.drawable.right_half_circle)
                    val gdCircle = bindingLt.smallCircleContainerLt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircle.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircle.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleText = bindingLt.smallCircleTextLt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleImage = bindingLt.smallCircleImageLt.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingLt.arrowCircleLt.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingLt.arrowCircleLt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                Glide.with(requireActivity())
                    .asBitmap()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            GranularRoundedCorners(0f, 500f, 500f, 0f)
                        )
                    )
                    .load(response!!.getActionData()!!.getContentMinimizedImage())
                    .into(bindingLt.smallCircleImageLt)
                bindingLt.smallCircleTextLt.visibility = View.GONE
            } else {
                bindingLt.smallCircleTextLt.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingLt.smallCircleTextLt.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingLt.smallCircleTextLt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingLt.smallCircleTextLt.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingLt.smallCircleImageLt.visibility = View.GONE
                bindingLt.smallCircleTextLt.topDown = isTopToBottom
                bindingLt.smallCircleTextLt.isCircle = true
                bindingLt.smallCircleTextLt.isRight = isRight
            }
            bindingLt.smallCircleContainerLt.setOnClickListener {
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingLt.arrowSquareLt.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingLt.arrowSquareLt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                if (shape == Shape.SOFT_EDGE) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 40f, 40f, 0f)
                            )
                        )
                        .load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingLt.smallSquareImageLt)
                } else {
                    Picasso.get().load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingLt.smallSquareImageLt)
                }
                bindingLt.smallSquareTextLt.visibility = View.GONE
            } else {
                bindingLt.smallSquareTextLt.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingLt.smallSquareTextLt.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingLt.smallSquareTextLt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingLt.smallSquareTextLt.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingLt.smallSquareImageLt.visibility = View.GONE
                bindingLt.smallSquareTextLt.topDown = isTopToBottom
                bindingLt.smallCircleTextLt.isCircle = false
                bindingLt.smallCircleTextLt.isRight = isRight
            }
            bindingLt.smallSquareContainerLt.setOnClickListener {
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

        if (isMaxiBackgroundImage) {
            Picasso.get().load(mExtendedProps!!.getMaxiBackgroundImage())
                .into(bindingLt.bigBackgroundImageLt)
        } else {
            if (!mExtendedProps!!.getMaxiBackgroundColor().isNullOrEmpty()) {
                bindingLt.bigContainerLt.setBackgroundColor(Color.parseColor(mExtendedProps!!.getMaxiBackgroundColor()))
            } else {
                bindingLt.bigContainerLt.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            bindingLt.bigBackgroundImageLt.visibility = View.GONE
        }

        if (!response!!.getActionData()!!.getContentMaximizedImage().isNullOrEmpty()) {
            Picasso.get().load(response!!.getActionData()!!.getContentMaximizedImage())
                .into(bindingLt.bigImageLt)
        }

        bindingLt.bigContainerLt.setOnClickListener {
            val uriString = response!!.getActionData()!!.getAndroidLnk()
            val buttonInterface: InAppButtonInterface? =
                RelatedDigital.getInAppButtonInterface()
            var report: MailSubReport?
            try {
                report = MailSubReport()
                report.impression = response!!.getActionData()!!.getReport()!!.getImpression()
                report.click = response!!.getActionData()!!.getReport()!!.getClick()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "There is no report to send!")
                e.printStackTrace()
                report = null
            }
            if (report != null) {
                InAppActionClickRequest.createInAppActionClickRequest(requireActivity(), report)
            }
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    val uri: Uri
                    try {
                        uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(
                            LOG_TAG,
                            "Can't parse notification URI, will not take any action",
                            e
                        )
                    }
                }
            }
        }
    }

    private fun adjustLm() {
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
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Picasso.get().load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLm.smallSquareBackgroundImageLm)
                    }
                } else {
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        bindingLm.smallSquareContainerLm.setBackgroundColor(
                            Color.parseColor(
                                mExtendedProps!!.getMiniBackgroundColor()
                            )
                        )
                    } else {
                        bindingLm.smallSquareContainerLm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLm.smallSquareBackgroundImageLm.visibility = View.GONE
                }
                bindingLm.smallCircleContainerLm.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(0f, 40f, 40f, 0f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLm.smallSquareBackgroundImageLm)
                    }
                } else {
                    bindingLm.smallSquareContainerLm.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLm.smallSquareTextLm.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLm.smallSquareImageLm.setBackgroundResource(R.drawable.rounded_corners_right)
                    val gd = bindingLm.smallSquareContainerLm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gd.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gd.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdText = bindingLm.smallSquareTextLm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdImage = bindingLm.smallSquareImageLm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLm.smallSquareBackgroundImageLm.visibility = View.GONE
                }
                bindingLm.smallCircleContainerLm.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(0f, 500f, 500f, 0f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLm.smallCircleBackgroundImageLm)
                    }
                } else {
                    bindingLm.smallCircleContainerLm.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLm.smallCircleTextLm.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLm.smallCircleImageLm.setBackgroundResource(R.drawable.right_half_circle)
                    val gdCircle = bindingLm.smallCircleContainerLm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircle.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircle.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleText = bindingLm.smallCircleTextLm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleImage = bindingLm.smallCircleImageLm.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLm.smallCircleBackgroundImageLm.visibility = View.GONE
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingLm.arrowCircleLm.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingLm.arrowCircleLm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                Glide.with(requireActivity())
                    .asBitmap()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            GranularRoundedCorners(0f, 500f, 500f, 0f)
                        )
                    )
                    .load(response!!.getActionData()!!.getContentMinimizedImage())
                    .into(bindingLm.smallCircleImageLm)
                bindingLm.smallCircleTextLm.visibility = View.GONE
            } else {
                bindingLm.smallCircleTextLm.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingLm.smallCircleTextLm.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingLm.smallCircleTextLm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingLm.smallCircleTextLm.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingLm.smallCircleImageLm.visibility = View.GONE
                bindingLm.smallCircleTextLm.topDown = isTopToBottom
                bindingLm.smallCircleTextLm.isCircle = true
                bindingLm.smallCircleTextLm.isRight = isRight
            }
            bindingLm.smallCircleContainerLm.setOnClickListener {
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingLm.arrowSquareLm.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingLm.arrowSquareLm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                if (shape == Shape.SOFT_EDGE) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 40f, 40f, 0f)
                            )
                        )
                        .load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingLm.smallSquareImageLm)
                } else {
                    Picasso.get().load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingLm.smallSquareImageLm)
                }
                bindingLm.smallSquareTextLm.visibility = View.GONE
            } else {
                bindingLm.smallSquareTextLm.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingLm.smallSquareTextLm.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingLm.smallSquareTextLm.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingLm.smallSquareTextLm.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingLm.smallSquareImageLm.visibility = View.GONE
                bindingLm.smallSquareTextLm.topDown = isTopToBottom
                bindingLm.smallCircleTextLm.isCircle = false
                bindingLm.smallCircleTextLm.isRight = isRight
            }
            bindingLm.smallSquareContainerLm.setOnClickListener {
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

        if (isMaxiBackgroundImage) {
            Picasso.get().load(mExtendedProps!!.getMaxiBackgroundImage())
                .into(bindingLm.bigBackgroundImageLm)
        } else {
            if (!mExtendedProps!!.getMaxiBackgroundColor().isNullOrEmpty()) {
                bindingLm.bigContainerLm.setBackgroundColor(Color.parseColor(mExtendedProps!!.getMaxiBackgroundColor()))
            } else {
                bindingLm.bigContainerLm.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            bindingLm.bigBackgroundImageLm.visibility = View.GONE
        }

        if (!response!!.getActionData()!!.getContentMaximizedImage().isNullOrEmpty()) {
            Picasso.get().load(response!!.getActionData()!!.getContentMaximizedImage())
                .into(bindingLm.bigImageLm)
        }

        bindingLm.bigContainerLm.setOnClickListener {
            val uriString = response!!.getActionData()!!.getAndroidLnk()
            val buttonInterface: InAppButtonInterface? =
                RelatedDigital.getInAppButtonInterface()
            var report: MailSubReport?
            try {
                report = MailSubReport()
                report.impression = response!!.getActionData()!!.getReport()!!.getImpression()
                report.click = response!!.getActionData()!!.getReport()!!.getClick()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "There is no report to send!")
                e.printStackTrace()
                report = null
            }
            if (report != null) {
                InAppActionClickRequest.createInAppActionClickRequest(requireActivity(), report)
            }
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    val uri: Uri
                    try {
                        uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(
                            LOG_TAG,
                            "Can't parse notification URI, will not take any action",
                            e
                        )
                    }
                }
            }
        }
    }

    private fun adjustLb() {
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
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Picasso.get().load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLb.smallSquareBackgroundImageLb)
                    }
                } else {
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        bindingLb.smallSquareContainerLb.setBackgroundColor(
                            Color.parseColor(
                                mExtendedProps!!.getMiniBackgroundColor()
                            )
                        )
                    } else {
                        bindingLb.smallSquareContainerLb.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLb.smallSquareBackgroundImageLb.visibility = View.GONE
                }
                bindingLb.smallCircleContainerLb.visibility = View.GONE
            }
            Shape.SOFT_EDGE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(0f, 40f, 40f, 0f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLb.smallSquareBackgroundImageLb)
                    }
                } else {
                    bindingLb.smallSquareContainerLb.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLb.smallSquareTextLb.setBackgroundResource(R.drawable.rounded_corners_right)
                    bindingLb.smallSquareImageLb.setBackgroundResource(R.drawable.rounded_corners_right)
                    val gd = bindingLb.smallSquareContainerLb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gd.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gd.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdText = bindingLb.smallSquareTextLb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdImage = bindingLb.smallSquareImageLb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLb.smallSquareBackgroundImageLb.visibility = View.GONE
                }
                bindingLb.smallCircleContainerLb.visibility = View.GONE
            }
            Shape.CIRCLE -> {
                if (isMiniBackgroundImage) {
                    if (!isSmallImage) {
                        Glide.with(requireActivity())
                            .asBitmap()
                            .transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    GranularRoundedCorners(0f, 500f, 500f, 0f)
                                )
                            )
                            .load(mExtendedProps!!.getMiniBackgroundImage())
                            .into(bindingLb.smallCircleBackgroundImageLb)
                    }
                } else {
                    bindingLb.smallCircleContainerLb.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLb.smallCircleTextLb.setBackgroundResource(R.drawable.right_half_circle)
                    bindingLb.smallCircleImageLb.setBackgroundResource(R.drawable.right_half_circle)
                    val gdCircle = bindingLb.smallCircleContainerLb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircle.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircle.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleText = bindingLb.smallCircleTextLb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleText.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleText.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    val gdCircleImage = bindingLb.smallCircleImageLb.background as GradientDrawable
                    if (!mExtendedProps!!.getMiniBackgroundColor().isNullOrEmpty()) {
                        gdCircleImage.setColor(Color.parseColor(mExtendedProps!!.getMiniBackgroundColor()))
                    } else {
                        gdCircleImage.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    bindingLb.smallCircleBackgroundImageLb.visibility = View.GONE
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingLb.arrowCircleLb.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingLb.arrowCircleLb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                Glide.with(requireActivity())
                    .asBitmap()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            GranularRoundedCorners(0f, 500f, 500f, 0f)
                        )
                    )
                    .load(response!!.getActionData()!!.getContentMinimizedImage())
                    .into(bindingLb.smallCircleImageLb)
                bindingLb.smallCircleTextLb.visibility = View.GONE
            } else {
                bindingLb.smallCircleTextLb.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingLb.smallCircleTextLb.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingLb.smallCircleTextLb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingLb.smallCircleTextLb.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
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
            if (!mExtendedProps!!.getArrowColor().isNullOrEmpty()) {
                bindingLb.arrowSquareLb.setTextColor(Color.parseColor(mExtendedProps!!.getArrowColor()))
            } else {
                bindingLb.arrowSquareLb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            if (isSmallImage) {
                if (shape == Shape.SOFT_EDGE) {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .transform(
                            MultiTransformation(
                                CenterCrop(),
                                GranularRoundedCorners(0f, 40f, 40f, 0f)
                            )
                        )
                        .load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingLb.smallSquareImageLb)
                } else {
                    Picasso.get().load(response!!.getActionData()!!.getContentMinimizedImage())
                        .into(bindingLb.smallSquareImageLb)
                }
                bindingLb.smallSquareTextLb.visibility = View.GONE
            } else {
                bindingLb.smallSquareTextLb.text =
                    response!!.getActionData()!!.getContentMinimizedText()
                if (!mExtendedProps!!.getMiniTextColor().isNullOrEmpty()) {
                    bindingLb.smallSquareTextLb.setTextColor(
                        Color.parseColor(
                            mExtendedProps!!.getMiniTextColor()
                        )
                    )
                } else {
                    bindingLb.smallSquareTextLb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                bindingLb.smallSquareTextLb.typeface = mExtendedProps!!.getMiniFontFamily(requireActivity())
                bindingLb.smallSquareImageLb.visibility = View.GONE
                bindingLb.smallSquareTextLb.topDown = isTopToBottom
                bindingLb.smallCircleTextLb.isCircle = false
                bindingLb.smallCircleTextLb.isRight = isRight
            }
            bindingLb.smallSquareContainerLb.setOnClickListener {
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

        if (isMaxiBackgroundImage) {
            Picasso.get().load(mExtendedProps!!.getMaxiBackgroundImage())
                .into(bindingLb.bigBackgroundImageLb)
        } else {
            if (!mExtendedProps!!.getMaxiBackgroundColor().isNullOrEmpty()) {
                bindingLb.bigContainerLb.setBackgroundColor(Color.parseColor(mExtendedProps!!.getMaxiBackgroundColor()))
            } else {
                bindingLb.bigContainerLb.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            bindingLb.bigBackgroundImageLb.visibility = View.GONE
        }

        if (!response!!.getActionData()!!.getContentMaximizedImage().isNullOrEmpty()) {
            Picasso.get().load(response!!.getActionData()!!.getContentMaximizedImage())
                .into(bindingLb.bigImageLb)
        }

        bindingLb.bigContainerLb.setOnClickListener {
            val uriString = response!!.getActionData()!!.getAndroidLnk()
            val buttonInterface: InAppButtonInterface? =
                RelatedDigital.getInAppButtonInterface()
            var report: MailSubReport?
            try {
                report = MailSubReport()
                report.impression = response!!.getActionData()!!.getReport()!!.getImpression()
                report.click = response!!.getActionData()!!.getReport()!!.getClick()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "There is no report to send!")
                e.printStackTrace()
                report = null
            }
            if (report != null) {
                InAppActionClickRequest.createInAppActionClickRequest(requireActivity(), report)
            }
            if (buttonInterface != null) {
                RelatedDigital.setInAppButtonInterface(null)
                buttonInterface.onPress(uriString)
            } else {
                if (!uriString.isNullOrEmpty()) {
                    val uri: Uri
                    try {
                        uri = Uri.parse(uriString)
                        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                        requireActivity().startActivity(viewIntent)
                    } catch (e: Exception) {
                        Log.i(
                            LOG_TAG,
                            "Can't parse notification URI, will not take any action",
                            e
                        )
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("drawer", response)
    }

    private fun endFragment() {
        if (activity != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this@InAppNotificationFragment)
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
        fun newInstance(model: Drawer): InAppNotificationFragment {
            val fragment = InAppNotificationFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, model)
            fragment.arguments = args
            return fragment
        }
    }
}