package com.relateddigital.androidexampleapp

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity


class WebViewTryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_try)

        val myWebView: WebView = findViewById(R.id.myWebView)
        /*val webSettings: WebSettings = myWebView.settings

        webSettings.javaScriptEnabled = true // JavaScript'i etkinleştir
        webSettings.domStorageEnabled = true


        val gameCode = """
            function initGame() {
                var score = 0;

                // Hedef elementini seç
                var target = document.getElementById('target');

                // Hedef tıklandığında skoru artır ve yeni bir konumda yerleştir
                target.addEventListener('click', function() {
                    score++;
                    document.getElementById('score').innerText = 'Score: ' + score;
                    target.style.left = Math.random() * 80 + '%';
                    target.style.top = Math.random() * 80 + '%';
                });
            }

            initGame();
        """.trimIndent()
        val htmlContent = """
           <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Simple Game</title>
                <style>
                    body {
                        background-color: black;
                        color: white;
                        text-align: center;
                        font-family: 'Arial', sans-serif;
                    }

                    #target {
                        position: absolute;
                        width: 50px;
                        height: 50px;
                        background-color: red;
                        cursor: pointer;
                    }
                </style>
            </head>
            <body>
                <h1>Simple Click Game</h1>
                <p id="score">Score: 0</p>
                <div id="target"></div>
                <script>$gameCode</script>
            </body>
            </html>
        """.trimIndent()

        myWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        myWebView.webChromeClient = WebChromeClient()
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        } */


        val position = "topLeft"
        val width = 90
        val height = 60


        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        when (position) {
            "topLeft" -> {
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                //params.addRule(RelativeLayout.CENTER_HORIZONTAL)
            }

        }


        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        params.width = (screenWidth * width / 100.0).toInt()
        params.height = (screenHeight * height / 100.0).toInt()


        myWebView.layoutParams = params

        /*myWebView.evaluateJavascript(gameCode) { result ->
            // JavaScript kodu çalıştıktan sonra yapılacak işlemler
            // result değişkeni, JavaScript kodunun çıktısını içerir
        } */
        myWebView.loadUrl("https://web.whatsapp.com/")
        myWebView.webViewClient = WebViewClient()
    }
}