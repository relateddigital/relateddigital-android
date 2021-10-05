package com.relateddigital.relateddigital_android.inapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants

class SpinToWinWebDialogFragment : DialogFragment() {
    private var webView: WebView? = null
    private var mResponse: String? = null
    private var fileName: String? = ""
    private var mIsRotation = false
    private var mListener: SpinToWinCompleteInterface? = null
    private var mCopyToClipboardInterface: SpinToWinCopyToClipboardInterface? = null

    fun display(fragmentManagerLoc: FragmentManager?): SpinToWinWebDialogFragment {
        this.show(fragmentManagerLoc!!, TAG)
        return this
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
        if (arguments != null) {
            fileName = requireArguments().getString("filename")
            mResponse = requireArguments().getString("response")
            mJavaScriptInterface = SpinToWinJavaScriptInterface(this, mResponse!!)
            mJavaScriptInterface!!.setSpinToWinListeners(mListener, mCopyToClipboardInterface)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mIsRotation = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mIsRotation) {
            if (activity != null) {
                requireActivity().finish()
            }
        }
    }

    fun getWebView(): WebView? {
        return webView
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.layout_web_view, container, false)
        webView = view.findViewById(R.id.webview)
        webView!!.webChromeClient = webViewClient
        webView!!.settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Constants.SDK_MIN_API_VERSION) {
            webView!!.settings.mediaPlaybackRequiresUserGesture = false
        }
        mJavaScriptInterface?.let { webView!!.addJavascriptInterface(it, "Android") }
        val folderPath = "file:android_asset/"
        val fileName = fileName
        val file = folderPath + fileName
        webView!!.loadUrl(file)
        webView!!.reload()
        return view
    }

    private val webViewClient: WebChromeClient
        get() = object : WebChromeClient() {
            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.d(TAG, cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId())
                return true
            }
        }
    val javaScriptInterface: SpinToWinJavaScriptInterface?
        get() = mJavaScriptInterface

    fun setSpinToWinListeners(listener: SpinToWinCompleteInterface?, copyToClipboardInterface: SpinToWinCopyToClipboardInterface?) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
    }

    companion object {
        const val TAG = "WebViewDialogFragment"

        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "filename"
        private const val ARG_PARAM2 = "response"
        private var mJavaScriptInterface: SpinToWinJavaScriptInterface? = null
        fun newInstance(filename: String?, response: String?): SpinToWinWebDialogFragment {
            val fragment = SpinToWinWebDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, filename)
            args.putString(ARG_PARAM2, response)
            mJavaScriptInterface = SpinToWinJavaScriptInterface(fragment, response!!)
            fragment.arguments = args
            return fragment
        }
    }
}