package com.relateddigital.androidexampleapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.relateddigital.androidexampleapp.databinding.ActivityPushNotificationBinding

class PushNotificationActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "PushNotificationActivity"
    }
    private lateinit var binding: ActivityPushNotificationBinding
    private lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPushNotificationBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        activity = this
    }
}