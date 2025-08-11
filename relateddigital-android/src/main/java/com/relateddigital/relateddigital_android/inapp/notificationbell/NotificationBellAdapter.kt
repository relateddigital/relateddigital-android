package com.relateddigital.relateddigital_android.inapp.notificationbell

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.relateddigital.relateddigital_android.databinding.ItemNotificationBellBinding
import com.relateddigital.relateddigital_android.inapp.FontFamily
import com.relateddigital.relateddigital_android.model.NotificationBellExtendedProps
import com.relateddigital.relateddigital_android.model.NotificationBellTexts
import java.util.*

class NotificationBellAdapter(
    private val context: Context,
    private val notificationList: List<NotificationBellTexts>,
    private val extendedProps: NotificationBellExtendedProps?,
    private val itemClickListener: (String?) -> Unit
) : RecyclerView.Adapter<NotificationBellAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: ItemNotificationBellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationBellTexts) {
            binding.tvNotificationText.text = notification.text

            // Stilleri uygula
            extendedProps?.let { props ->
                binding.tvNotificationText.setTextColor(Color.parseColor(props.text_text_color))
                binding.tvNotificationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, (props.text_text_size?.toFloat() ?: 2f) * 2 + 10)
                binding.tvNotificationText.typeface = getFontFamily(props.font_family)
            }

            // Tıklama olayı
            val link = notification.android_lnk
            if (!link.isNullOrEmpty()) {
                binding.root.isClickable = true
                binding.root.setOnClickListener {
                    itemClickListener.invoke(link)
                }
            } else {
                binding.root.isClickable = false
                binding.root.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBellBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int = notificationList.size

    private fun getFontFamily(fontFamilyString: String?): Typeface {
        return when (fontFamilyString?.lowercase(Locale.ROOT)) {
            FontFamily.Monospace.toString().lowercase(Locale.ROOT) -> Typeface.MONOSPACE
            FontFamily.SansSerif.toString().lowercase(Locale.ROOT) -> Typeface.SANS_SERIF
            FontFamily.Serif.toString().lowercase(Locale.ROOT) -> Typeface.SERIF
            else -> Typeface.DEFAULT
        }
    }
}