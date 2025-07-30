package com.relateddigital.androidexampleapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.relateddigital.androidexampleapp.databinding.ActivityMainBinding

class
MainActivity : AppCompatActivity() {

    private val broad = object :BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                "InAppLink" -> {
                    val string = intent.getStringExtra("link")
                    Toast.makeText(context,"Receiver received >${string} ",Toast.LENGTH_LONG).show()
                    Log.e("Deeplink5", "Received deeplink: $string")
                }
            }
        }
    }
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
        val intentFilter = IntentFilter("InAppLink")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broad,intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broad,intentFilter)
        }




    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broad)
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