package com.relateddigital.relateddigital_android.inapp.notificationbell

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentNotificationBellBinding
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.model.NotificationBell // YENİ: Doğru modeli import ettiğinizden emin olun
import com.relateddigital.relateddigital_android.model.NotificationBellExtendedProps // YENİ: Doğru modeli import ettiğinizden emin olun
import com.relateddigital.relateddigital_android.model.NotificationBellTexts // YENİ: Doğru modeli import ettiğinizden emin olun
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import java.net.URI
import java.util.*

class NotificationBellFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBellBinding
    private var notificationBell: NotificationBell? = null
    private var extendedProps: NotificationBellExtendedProps? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBellBinding.inflate(inflater, container, false)

        // Gelen veriyi al
        notificationBell = arguments?.getSerializable(ARG_PARAM1) as? NotificationBell

        if (notificationBell == null) {
            Log.e(LOG_TAG, "NotificationBell data is null. Closing fragment.")
            endFragment()
            return binding.root
        } else {
             Log.d(LOG_TAG, "NotificationBell data received: " + notificationBell!!.title)
        }

        parseExtendedProps()
        setupInitialView()

        return binding.root
    }

    private fun parseExtendedProps() {
        try {
            val decodedString = URI(notificationBell!!.actiondata!!.extendedProps).path
            extendedProps = Gson().fromJson(decodedString, NotificationBellExtendedProps::class.java)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error parsing Extendedops. Using default values.", e)
            endFragment()
        }
    }

    private fun setupInitialView() {
        // Zil ikonunu ayarla
        loadStaticBellIcon()

        // Diyalog penceresinin içeriğini ayarla
        setupDialogContent()

        var dX = 0f
        var dY = 0f
        var lastAction = 0

        binding.fabBell.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    lastAction = android.view.MotionEvent.ACTION_DOWN
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY
                    
                    // Update LayoutParams to move the view AND its anchors
                    val layoutParams = view.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                    layoutParams.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
                    layoutParams.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
                    
                    layoutParams.leftMargin = newX.toInt()
                    layoutParams.topMargin = newY.toInt()
                    view.layoutParams = layoutParams
                    
                    lastAction = android.view.MotionEvent.ACTION_MOVE
                    true
                }
                android.view.MotionEvent.ACTION_UP -> {
                    if (lastAction == android.view.MotionEvent.ACTION_DOWN) {
                        // Click event
                        if (binding.dialogContainer.visibility == View.VISIBLE) {
                            hideDialog()
                        } else {
                            showDialog()
                        }
                    } else {
                        // Snap logic with LayoutParams
                        val displayMetrics = resources.displayMetrics
                        val screenWidth = displayMetrics.widthPixels
                        val viewWidth = view.width
                        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, displayMetrics).toInt()
                        
                        val currentX = (view.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams).leftMargin
                        
                        val targetX = if (currentX + viewWidth / 2 < screenWidth / 2) {
                            // Snap Left
                            padding
                        } else {
                            // Snap Right
                            screenWidth - viewWidth - padding
                        }
                        
                        val animator = android.animation.ValueAnimator.ofInt(currentX, targetX)
                        animator.duration = 300
                        animator.addUpdateListener { animation ->
                            val value = animation.animatedValue as Int
                            val params = view.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                            params.leftMargin = value
                            view.layoutParams = params
                        }
                        animator.start()
                    }
                    true
                }
                else -> false
            }
        }
    }
    
    private fun updateDialogPosition() {
        // Bell position check
        val bellY = binding.fabBell.y
        val bellHeight = binding.fabBell.height
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        
        val layoutParams = binding.dialogContainer.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        
        // Remove old constraints
        layoutParams.topToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
        layoutParams.bottomToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
        
        if (bellY < screenHeight / 2) {
            // Top half: Show dialog BELOW the bell
            layoutParams.topToBottom = binding.fabBell.id
            binding.ivPointer.rotation = 180f // Point up
            
            // Adjust pointer constraint to be at the TOP of the card
             val pointerParams = binding.ivPointer.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
             pointerParams.topToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
             pointerParams.bottomToTop = binding.dialogCard.id
             binding.ivPointer.layoutParams = pointerParams
             
        } else {
            // Bottom half: Show dialog ABOVE the bell
            layoutParams.bottomToTop = binding.fabBell.id
             binding.ivPointer.rotation = 0f // Point down
             
             // Adjust pointer constraint to be at the BOTTOM of the card
             val pointerParams = binding.ivPointer.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
             pointerParams.bottomToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
             pointerParams.topToBottom = binding.dialogCard.id
             binding.ivPointer.layoutParams = pointerParams
        }
        
        binding.dialogContainer.layoutParams = layoutParams
    }

    private fun loadStaticBellIcon() {
        val staticIconUrl = notificationBell?.actiondata?.bell_icon
        if (!staticIconUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(staticIconUrl)
                .placeholder(R.drawable.ic_close_black_24dp) // Varsayılan bir ikon belirleyebilirsiniz
                .into(binding.fabBell)
        } else {
            binding.fabBell.setImageResource(R.drawable.ic_close_black_24dp) // URL yoksa varsayılan ikon
        }
    }

    private fun loadBellAnimation() {
        val animationUrl = notificationBell?.actiondata?.bell_animation
        if (!animationUrl.isNullOrEmpty()) {
            Glide.with(this)
                .asGif()
                .load(animationUrl)
                .placeholder(R.drawable.ic_close_black_24dp) // Varsayılan bir ikon belirleyebilirsiniz
                .into(binding.fabBell)
        } else {
            // Animasyon URL'si yoksa statik ikonu yüklemeyi dene
            loadStaticBellIcon()
        }
    }

    private fun setupDialogContent() {
        // Renkler, yazılar ve fontlar
        extendedProps?.let { props ->
            binding.dialogCard.setCardBackgroundColor(Color.parseColor(props.background_color))
            binding.ivPointer.setColorFilter(Color.parseColor(props.background_color))
            binding.tvTitle.setTextColor(Color.parseColor(props.title_text_color))
            binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, (props.title_text_size?.toFloat() ?: 7f) * 2 + 6) // Boyutlandırma
            val typeface = getFontFamily(props.font_family)
            binding.tvTitle.typeface = typeface
        }

        binding.tvTitle.text = notificationBell?.actiondata?.title
        binding.ivClose.setOnClickListener {
            endFragment()
        }

        // Tıklama dışındaki alanların tıklanabilir olmasını engelle
        binding.dialogContainer.setOnClickListener(null)


        // RecyclerView'ı ayarla
        val notificationTexts = notificationBell?.actiondata?.notification_texts ?: emptyList()
        if (notificationTexts.isNotEmpty()) {
            val adapter = NotificationBellAdapter(requireContext(), notificationTexts, extendedProps) { link ->
                if (link != null) {
                    val callback = RelatedDigital.setNotificationBellClickCallback()
                    callback?.onNotificationBellClickCallbackClick(link)
                    link.let {
                        try {
                            sendReport()
                        } catch (e: Exception) {
                            Log.e(LOG_TAG, "Could not open the link: $it", e)
                        }
                    }
                    hideDialog()
                }

            }
            binding.rvNotifications.adapter = adapter
        }
    }

    private fun sendReport() {
        var report: MailSubReport?
        try {
            report = MailSubReport()
            report.click = notificationBell?.actiondata?.report?.click
        } catch (e: Exception) {
            Log.e("Notification Bell : ", "There is no click report to send!")
            e.printStackTrace()
            report = null
        }
        if (report != null) {
            InAppActionClickRequest.createInAppActionClickRequest(requireContext(), report)
        }
    }

    private fun getFontFamily(fontFamilyString: String?): Typeface {
        return when (fontFamilyString?.lowercase(Locale.ROOT)) {
            FontFamily.Monospace.toString().lowercase(Locale.ROOT) -> Typeface.MONOSPACE
            FontFamily.SansSerif.toString().lowercase(Locale.ROOT) -> Typeface.SANS_SERIF
            FontFamily.Serif.toString().lowercase(Locale.ROOT) -> Typeface.SERIF
            else -> Typeface.DEFAULT
        }
    }

    private fun showDialog() {
        loadBellAnimation()
        updateDialogPosition()
        binding.dialogContainer.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        loadStaticBellIcon()
        binding.dialogContainer.visibility = View.GONE
    }

    private fun endFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@NotificationBellFragment)?.commit()
    }

    companion object {
        private const val LOG_TAG = "NotificationBell"
        private const val ARG_PARAM1 = "dataKey"

        fun newInstance(model: NotificationBell): NotificationBellFragment {
            val fragment = NotificationBellFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, model)
            fragment.arguments = args
            return fragment
        }
    }
}