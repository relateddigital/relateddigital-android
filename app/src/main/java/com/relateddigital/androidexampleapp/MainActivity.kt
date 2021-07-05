package com.relateddigital.androidexampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.relateddigital.relateddigital_android.RelatedDigital

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val relatedDigitalInstance = RelatedDigital(this, "676D325830564761676D453D")
    }
}