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
        loadBellAnimation()

        // Diyalog penceresinin içeriğini ayarla
        setupDialogContent()

        binding.fabBell.setOnClickListener {
            if (binding.dialogContainer.visibility == View.VISIBLE) {
                hideDialog()
            } else {
                showDialog()
            }
        }
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

    // YENİ: Animasyonlu ikonu yüklemek için yardımcı fonksiyon
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
        loadStaticBellIcon()
        // TODO: Raporlama kodunu buraya ekle (impression report)
        binding.dialogContainer.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        loadBellAnimation()
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