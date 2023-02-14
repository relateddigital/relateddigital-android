package com.relateddigital.relateddigital_android_core.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.animation.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.relateddigital.relateddigital_android_core.R

object AnimationManager {
    var gradientDrawable: GradientDrawable? = null
    val scaleAnimation: Animation
        get() {
            val scale = ScaleAnimation(
                    .95f, 1.0f, .95f, 1.0f, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 1.0f)
            scale.duration = 200
            return scale
        }

    fun getMiniTranslateAnimation(context: Context): TranslateAnimation {
        val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75f,
                context.resources.displayMetrics)
        val translate = TranslateAnimation(0f, 0f, heightPx, 0f)
        translate.interpolator = DecelerateInterpolator()
        translate.duration = 200
        return translate
    }

    val translateAnimation: TranslateAnimation
        get() {
            val translate = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.0f
            )
            translate.interpolator = DecelerateInterpolator()
            translate.duration = 200
            return translate
        }

    fun getFadeInAnimation(context: Context?): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.anim_fade_in)
    }

    fun getGradient(closeButtonWrapper: LinearLayout, activity: Activity): GradientDrawable {
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val params = closeButtonWrapper.layoutParams as RelativeLayout.LayoutParams
            params.setMargins(0, 0, 0, (size.y * 0.06f).toInt()) // make bottom margin 6% of screen height
            closeButtonWrapper.layoutParams = params
        }
        val gd = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(-0x1a9f9f84, -0x1ab7b7a3, -0x1ae7e7e1, -0x1ae7e7e1))
        gd.gradientType = GradientDrawable.RADIAL_GRADIENT
        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gd.setGradientCenter(0.25f, 0.5f)
            gd.gradientRadius = Math.min(size.x, size.y) * 0.8f
        } else {
            gd.setGradientCenter(0.5f, 0.33f)
            gd.gradientRadius = Math.min(size.x, size.y) * 0.7f
        }
        return gd
    }

    fun setNoDropShadowBackgroundToView(inAppImageView: View, inAppImage: Bitmap) {
        val h = inAppImage.height / 100
        val w = inAppImage.width / 100
        val scaledImage = Bitmap.createScaledBitmap(inAppImage, w, h, false)
        var averageColor: Int
        var averageAlpha: Int
        outerloop@ for (x in 0 until w) {
            for (y in 0 until h) {
                averageColor = scaledImage.getPixel(x, y)
                averageAlpha = Color.alpha(averageColor)
                if (averageAlpha < 0xFF) {
                    inAppImageView.setBackgroundResource(R.drawable.bg_square_nodropshadow)
                    break@outerloop
                }
            }
        }
    }

    fun setBackgroundGradient(v: View, d: Drawable?) {
        v.background = d
    }

    fun getMiniScaleAnimation(context: Context): Animation {
        val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75f, context.resources.displayMetrics)
        val scale = ScaleAnimation(0.0f, 1.0f, 0.0f,
                1.0f, heightPx / 2, heightPx / 2)
        scale.interpolator = SineBounceInterpolator()
        scale.duration = 400
        scale.startOffset = 200
        return scale
    }

    private class SineBounceInterpolator : Interpolator {
        override fun getInterpolation(t: Float): Float {
            return (-(Math.pow(Math.E, (-8 * t).toDouble()) * Math.cos((12 * t).toDouble()))).toFloat() + 1
        }
    }
}