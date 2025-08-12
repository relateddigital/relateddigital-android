package com.relateddigital.relateddigital_android.inapp.survey

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants

class WebViewDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "WebViewDialogFragment"
        private const val ARG_PARAM1 = "response"
        private const val ARG_PARAM2 = "baseUrl"
        private const val ARG_PARAM3 = "htmlString"

        private var mJavaScriptInterface: WebViewJavascriptInterface? = null

        fun newInstance(baseUrl: String, htmlString: String, response: String): WebViewDialogFragment {
            val fragment = WebViewDialogFragment()
            val args = Bundle().apply {
                putString(ARG_PARAM1, response)
                putString(ARG_PARAM2, baseUrl)
                putString(ARG_PARAM3, htmlString)
            }
            mJavaScriptInterface = WebViewJavascriptInterface(fragment, response)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var webView: WebView
    private var mResponse: String? = null
    private var baseUrl: String = ""
    private var htmlString: String = ""
    private var mIsRotation = false
    private var mListener: SurveyCompleteInterface? = null

    fun display(fragmentManager: FragmentManager): WebViewDialogFragment {
        show(fragmentManager, TAG)
        return this
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
        arguments?.let {
            baseUrl = it.getString(ARG_PARAM2).orEmpty()
            htmlString = it.getString(ARG_PARAM3).orEmpty()
            mResponse = it.getString(ARG_PARAM1)
            mJavaScriptInterface = WebViewJavascriptInterface(this, mResponse ?: "")
        }
    }

    override fun onSaveInstanceState(@NonNull outState: Bundle) {
        super.onSaveInstanceState(outState)
        mIsRotation = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mIsRotation) {
            activity?.finish()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_web_view, container, false)
        webView = view.findViewById(R.id.webview)

        webView.settings.apply {
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            if (Build.VERSION.SDK_INT >= Constants.SDK_MIN_API_VERSION) {
                mediaPlaybackRequiresUserGesture = false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.d("WebViewConsole", "${cm.message()} -- From line ${cm.lineNumber()} of ${cm.sourceId()}")
                return true
            }
        }

        mJavaScriptInterface?.let { webView.addJavascriptInterface(it, "Android") }

        Log.d(TAG, "loadDataWithBaseURL çağrılıyor.")
        Log.d(TAG, "Base URL: $baseUrl")
        Log.d(TAG, "Response (JSON): $mResponse")
        // HTML çok uzun olabilir, gerekirse açabilirsiniz
        // Log.d(TAG, "HTML String: $htmlString")

        webView.loadDataWithBaseURL(baseUrl, htmlString, "text/html", "utf-8", "about:blank")

        return view
    }

    fun setSurveyListeners(listener: SurveyCompleteInterface) {
        mListener = listener
    }
}