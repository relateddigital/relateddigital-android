package com.relateddigital.androidexampleapp

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity


class WebViewTryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_try)

        val myWebView: WebView = findViewById(R.id.myWebView)

        // Backend'den gelen verileri al
        val position = "topLeft" // Backend'den alınacak
        val width = 70 // Backend'den alınacak
        val height = 90 // Backend'den alınacak

        // LayoutParams oluşturarak webview'ın boyut ve konumunu ayarla
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Örneğin, position'a göre konumu ayarla
        when (position) {
            "topLeft" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            }
            // Diğer konumları da kontrol et ve ayarla
        }

        // Webview'ın genişlik ve yüksekliğini yüzdelik olarak ayarla
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        params.width = (screenWidth * width / 100.0).toInt()
        params.height = (screenHeight * height / 100.0).toInt()

        // LayoutParams'i webview'a uygula
        myWebView.layoutParams = params

        // Webview'ı yükle
        myWebView.loadUrl("https://web.whatsapp.com/")
        myWebView.webViewClient = WebViewClient()
    }
}