package com.relateddigital.relateddigital_android.inapp.countdowntimerbanner

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.databinding.FragmentCountdownTimerBannerBinding
import com.relateddigital.relateddigital_android.model.CountdownTimerBanner
import com.relateddigital.relateddigital_android.model.CountdownTimerBannerActionData
import com.relateddigital.relateddigital_android.model.CountdownTimerBannerExtendedProps
import com.relateddigital.relateddigital_android.model.MailSubReport
import com.relateddigital.relateddigital_android.network.requestHandler.InAppActionClickRequest
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CountdownTimerBannerFragment : Fragment() {

    // binding null olabileceği için (onDestroyView'da temizleniyor) nullable.
    private var binding: FragmentCountdownTimerBannerBinding? = null
    private var bannerModel: CountdownTimerBanner? = null
    private var actionData: CountdownTimerBannerActionData? = null
    private var extendedProps: CountdownTimerBannerExtendedProps? = null
    private var timer: CountDownTimer? = null

    companion object {
        private const val LOG_TAG = "CountdownTimerBanner"
        private const val ARG_PARAM1 = "dataKey"

        /**
         * Fabrika metodu.
         */
        @JvmStatic
        fun newInstance(model: CountdownTimerBanner) =
            CountdownTimerBannerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, model)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountdownTimerBannerBinding.inflate(inflater, container, false)

        arguments?.let {
            bannerModel = it.getSerializable(ARG_PARAM1) as? CountdownTimerBanner
            bannerModel?.let { model ->
                actionData = model.actiondata
            }
        }

        if (bannerModel == null || actionData == null) {
            Log.e(LOG_TAG, "CountdownTimerBanner data or actionData is null. Closing fragment.")
            endFragment() // onViewCreated'e gitmeden kapat
            return binding?.root
        }
        parseExtendedProps()
        positionBanner()

        return binding?.root
    }

    private fun positionBanner() {
        val data = actionData ?: return
        val binding = this.binding ?: return

        val position = extendedProps!!.position_on_page

        // Sadece "DownPosition" ise pozisyonu değiştir.
        if ("DownPosition".equals(position?.trim(), ignoreCase = true)) {
            // CardView'ın layout parametrelerini al
            val params = binding.bannerCardView.layoutParams as? ConstraintLayout.LayoutParams

            params?.let {
                // Varsayılan üst bağlantısını temizle
                it.topToTop = ConstraintLayout.LayoutParams.UNSET
                // Yeni alt bağlantısını ayarla
                it.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                // Yeni parametreleri CardView'a uygula
                binding.bannerCardView.layoutParams = it
            }
        }
        // "UpPosition" ise hiçbir şey yapmaya gerek yok, XML'deki varsayılanı kullanır.
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (actionData != null) {

            setupInitialView()
            startCountdown()

            if (actionData!!.waiting_time!! > 0) {
                var report: MailSubReport?
                try {
                    report = MailSubReport()
                    report.impression = actionData?.report?.impression
                    context?.let { InAppActionClickRequest.createInAppActionImressionRequest(it, report) }
                } catch (e: Exception) {
                    Log.e("CountdownTimer Report", "There is no impression report to send!")
                    e.printStackTrace()
                    report = null
                }
            }
        } else {
            endFragment() // Ekstra güvenlik
        }
    }

    /**
     * URI-decoding yöntemini kullanır.
     */
    private fun parseExtendedProps() {
        try {
            actionData?.extendedProps?.let {
                val decodedString = URI(it).path
                extendedProps = Gson().fromJson(
                    decodedString,
                    CountdownTimerBannerExtendedProps::class.java
                )
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error parsing ExtendedProps. Using default values.", e)
        }
    }

    /**
     * Görünümü ayarlar ve click listener'ları bağlar.
     */
    private fun setupInitialView() {
        val data = actionData ?: run {
            Log.e(LOG_TAG, "ActionData is null, cannot setup view.")
            return
        }
        val binding = this.binding ?: return

        binding.tvBannerText.text = data.content_body

        if (isAdded) {
            Glide.with(this)
                .load(data.img)
                .into(binding.ivBannerIcon)
        }

        // Renkleri ExtendedProps'tan uygula
        extendedProps?.let { props ->

            // 1. Ana CardView arkaplan rengi
            try {
                if (!props.background_color.isNullOrEmpty()) {
                    binding.bannerCardView.setCardBackgroundColor(Color.parseColor(props.background_color))
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Invalid background_color. JSON'da '#RRGGBB' formatı kullanın. Gelen: ${props.background_color}", e)
            }

            // 2. Kampanya metni rengi
            try {
                if (!props.content_body_text_color.isNullOrEmpty()) {
                    binding.tvBannerText.setTextColor(Color.parseColor(props.content_body_text_color))
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Invalid content_body_text_color: ${props.content_body_text_color}", e)
            }

            // 3. Kapatma butonu rengi
            try {
                val closeColor = data.close_button_color
                if (!closeColor.isNullOrEmpty()) {
                    val closeButtonColor = Color.parseColor(closeColor)
                    DrawableCompat.setTint(binding.ibClose.drawable, closeButtonColor)
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Invalid close_button_color: ${data.close_button_color}", e)
            }

            // 4. Zamanlayıcı Arka Plan Rengi
            try {
                if (!data.scratch_color.isNullOrEmpty()) {
                    val timerBackground =
                        binding.layoutTimer.background.mutate() as? GradientDrawable
                    timerBackground?.setColor(Color.parseColor(data.scratch_color))
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to set timer background color (scratch_color). Gelen: ${data.scratch_color}", e)
            }

            // 5. Zamanlayıcı METİN Rengi
            try {
                if (!props.counter_color.isNullOrEmpty()) {
                    val timerTextColor = Color.parseColor(props.counter_color)
                    binding.tvTimerDays.setTextColor(timerTextColor)
                    binding.tvTimerHours.setTextColor(timerTextColor)
                    binding.tvTimerMinutes.setTextColor(timerTextColor)
                    binding.tvTimerSubDays.setTextColor(timerTextColor)
                    binding.tvTimerSubHours.setTextColor(timerTextColor)
                    binding.tvTimerSubMinutes.setTextColor(timerTextColor)
                } else {
                    Log.e(LOG_TAG, "Invalid timer text color (counter_color). Gelen: ${props.counter_color}")
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Invalid timer text color (counter_color). Gelen: ${props.counter_color}", e)
            }
        }

        // --- Click listener'lar ---
        binding.ibClose.setOnClickListener { endFragment() }

        binding.root.setOnClickListener {
            val link = data.android_lnk
            if (!link.isNullOrEmpty()) {
                val callback = RelatedDigital.setCountdownTimerBannerClickCallback()
                callback?.let {
                    var report: MailSubReport?
                    try {

                        try {
                            report = MailSubReport()
                            report.click = actionData?.report?.click
                            context?.let { InAppActionClickRequest.createInAppActionClickRequest(it, report) }
                            callback.onCountdownTimerBannerClick(link)
                        } catch (e: Exception) {
                            Log.e("CountdownTimer Report", "There is no click report to send!")
                            e.printStackTrace()
                            report = null
                        }
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Error firing CountdownTimerBannerClickCallback", e)
                    }

                    endFragment()
                }
            }
        }
    }

    /**
     * Geri sayım sayacını başlatır.
     */
    private fun startCountdown() {
        val data = actionData ?: return
        try {
            val targetDateString = "${data.counter_Date} ${data.counter_Time}"
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val targetDate = sdf.parse(targetDateString) ?: Date()
            val now = System.currentTimeMillis()
            val millisInFuture = targetDate.time - now

            if (millisInFuture <= 0) {
                showCampaignFinished()
                return
            }

            timer = object : CountDownTimer(millisInFuture, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // binding null ise (onDestroyView çağrıldıysa) devam etme
                    binding ?: return

                    val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                    val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60

                    binding?.tvTimerDays?.text = String.format(Locale.getDefault(), "%02d", days)
                    binding?.tvTimerHours?.text = String.format(Locale.getDefault(), "%02d", hours)
                    binding?.tvTimerMinutes?.text = String.format(Locale.getDefault(), "%02d", minutes)
                }

                override fun onFinish() {
                    binding?.let {
                        showCampaignFinished()
                    }
                }
            }.start()

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to parse countdown date.", e)
            showCampaignFinished()
        }
    }

    /**
     * Kampanya bittiğinde UI'ı günceller.
     */
    private fun showCampaignFinished() {
        binding ?: return // Null kontrolü
        binding?.tvBannerText?.text = "Kampanya sona erdi!"
        binding?.layoutTimer?.visibility = View.GONE
    }

    /**
     * Fragment'ı kaldırır.
     */
    private fun endFragment() {
        // activity null değilse VE fragment eklendiyse (isAdded)
        activity?.takeIf { isAdded }?.supportFragmentManager?.beginTransaction()?.remove(this)
            ?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel() // Sayacı durdur
        timer = null
        binding = null // ViewBinding referansını temizle (çok önemli)
    }
}