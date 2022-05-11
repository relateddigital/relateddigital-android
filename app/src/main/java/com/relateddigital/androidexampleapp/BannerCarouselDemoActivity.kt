package com.relateddigital.androidexampleapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.relateddigital.androidexampleapp.databinding.ActivityBannerCarouselDemoBinding
import com.relateddigital.relateddigital_android.inapp.bannercarousel.BannerItemClickListener
import com.relateddigital.relateddigital_android.inapp.bannercarousel.BannerRequestListener

class BannerCarouselDemoActivity : AppCompatActivity() {
    private var binding: ActivityBannerCarouselDemoBinding? = null
    private var bannerItemClickListener: BannerItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerCarouselDemoBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        bannerItemClickListener = object : BannerItemClickListener {
            override fun bannerItemClicked(bannerLink: String?) {
                Toast.makeText(applicationContext, bannerLink, Toast.LENGTH_SHORT).show()
                Log.i("link banner", bannerLink!!)
                try {
                    val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(bannerLink))
                    startActivity(viewIntent)
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "The link is not formatted properly!")
                }
            }
        }

        binding!!.btnShowBanner.setOnClickListener {
            hideKeyboard(this)
            binding!!.etBannerId.clearFocus()
            showBanner()
        }
    }

    private fun showBanner() {
        val bannerId = binding!!.etBannerId.text.toString().trim()
        val bannerRequestListener = object : BannerRequestListener {
            override fun onRequestResult(isAvailable: Boolean) {
                if (!isAvailable) {
                    binding!!.bannerListView.visibility = View.GONE
                }
            }
        }
        if (bannerId.isEmpty()) {
            binding!!.bannerListView.requestBannerCarouselAction(
                context = applicationContext,
                bannerRequestListener = bannerRequestListener,
                bannerItemClickListener = bannerItemClickListener)
        } else {
            binding!!.bannerListView.requestBannerCarouselAction(
                context = applicationContext,
                actionId = bannerId,
                bannerRequestListener = bannerRequestListener,
                bannerItemClickListener = bannerItemClickListener)
        }
    }

    private fun hideKeyboard(activity: AppCompatActivity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val LOG_TAG = "BannerDemoActivity"
    }
}