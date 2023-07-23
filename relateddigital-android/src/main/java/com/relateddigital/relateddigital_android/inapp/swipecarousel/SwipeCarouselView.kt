package com.relateddigital.relateddigital_android.inapp.swipecarousel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.relateddigital.relateddigital_android.R

class SwipeCarouselView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var expandableView: ViewGroup
    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var closeButton: Button
    private var isExpanded = false
    private lateinit var rv : RecyclerView
    private var adapter : SwipeCarouselAdapter? = null


    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.carousel_animated_view, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        expandableView = findViewById(R.id.expandable_view)
        imageView = findViewById(R.id.image_view)
        //titleTextView = findViewById(R.id.title_text_view)
        //descriptionTextView = findViewById(R.id.description_text_view)
        closeButton = findViewById(R.id.close_button)
        rv = findViewById(R.id.rv)

        adapter =  SwipeCarouselAdapter(context)
        rv.adapter = adapter
        expandableView.visibility = View.GONE

        imageView.setOnClickListener {
            toggleExpandedState()
        }

        closeButton.setOnClickListener {
            toggleExpandedState()
        }
    }

    private fun toggleExpandedState() {
        if (isExpanded) {
            collapseView()
        } else {
            expandView()
        }
    }

    private fun expandView() {
        expandableView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        val targetHeight = expandableView.measuredHeight

        val parentView = parent as? ViewGroup

        parentView?.let {
            it.removeView(this)
            it.addView(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        expandableView.visibility = View.VISIBLE
        expandableView.alpha = 0f
        expandableView.translationY = -targetHeight.toFloat()

        val slideDownAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        slideDownAnimation.duration = 100
        expandableView.startAnimation(slideDownAnimation)

        expandableView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    isExpanded = true
                }
            })

        imageView.isEnabled = false
        //val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_down)
        //imageView.startAnimation(rotateAnimation)
        imageView.visibility = GONE
    }

    private fun collapseView() {
        val parentView = parent as? ViewGroup

        parentView?.let {
            it.removeView(this)
            it.addView(this)
        }

        val initialHeight = expandableView.height

        val slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUpAnimation.duration = 1200
        expandableView.startAnimation(slideUpAnimation)

        expandableView.animate()
            .alpha(0f)
            .translationY(-initialHeight.toFloat())
            .setDuration(1200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    expandableView.visibility = View.GONE
                    imageView.visibility = VISIBLE
                    isExpanded = false
                }

            }
            )

        imageView.isEnabled = true
        //val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_up)
        //imageView.startAnimation(rotateAnimation)


    }
}