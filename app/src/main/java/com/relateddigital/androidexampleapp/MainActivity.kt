package com.relateddigital.androidexampleapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.relateddigital.androidexampleapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "MainActivity"
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        setupUi()
    }

    private fun setupUi() {
        binding.inAppNotificationPage.setOnClickListener {
            val intent = Intent(this@MainActivity, InAppNotificationActivity::class.java)
            startActivity(intent)
        }

        binding.pushNotificationPage.setOnClickListener {
            val intent = Intent(this@MainActivity, PushNotificationActivity::class.java)
            startActivity(intent)
        }

        binding.loginPage.setOnClickListener {
            val intent = Intent(this@MainActivity, EventActivity::class.java)
            startActivity(intent)
        }

        binding.storyPage.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryDemoActivity::class.java)
            startActivity(intent)
        }

        binding.bannerCarouselPage.setOnClickListener {
            val intent = Intent(this@MainActivity, BannerCarouselDemoActivity::class.java)
            startActivity(intent)
        }

        binding.swipeCarouselPage.setOnClickListener {
            val intent = Intent(this@MainActivity, SwipeCarouselActivity::class.java)
            startActivity(intent)
        }

        binding.customWeb.setOnClickListener {
            val intent = Intent(this@MainActivity, WebViewTryActivity::class.java)
            startActivity(intent)
        }
    }
}