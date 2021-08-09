package com.relateddigital.androidexampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.relateddigital.androidexampleapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    companion object{
        private const val LOG_TAG = "LoginActivity"
    }
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
    }
}