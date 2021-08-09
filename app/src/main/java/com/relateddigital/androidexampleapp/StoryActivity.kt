package com.relateddigital.androidexampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.relateddigital.androidexampleapp.databinding.ActivityMainBinding
import com.relateddigital.androidexampleapp.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "StoryActivity"
    }
    private lateinit var binding: ActivityStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
    }
}