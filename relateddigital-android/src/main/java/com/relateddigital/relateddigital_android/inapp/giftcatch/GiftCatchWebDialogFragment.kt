package com.relateddigital.relateddigital_android.inapp.giftcatch

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

class GiftCatchWebDialogFragment : DialogFragment() {
    private var webView: WebView? = null
    private var mResponse: String? = null
    private var htmlString: String? = ""
    private var mIsRotation = false
    private lateinit var mListener: GiftCatchCompleteInterface
    private lateinit var mCopyToClipboardInterface: GiftCatchCopyToClipboardInterface
    private lateinit var mShowCodeInterface: GiftCatchShowCodeInterface

    fun display(fragmentManagerLoc: FragmentManager?): GiftCatchWebDialogFragment {
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
            htmlString = requireArguments().getString("htmlString")
            mResponse = requireArguments().getString("response")
            mJavaScriptInterface = GiftCatchJavaScriptInterface(this, mResponse!!)
            mJavaScriptInterface!!.setGiftCatchListeners(mListener, mCopyToClipboardInterface, mShowCodeInterface)
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

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.layout_web_view, container, false)
        webView = view.findViewById(R.id.webview)
        webView!!.webChromeClient = webViewClient
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.allowContentAccess = true
        webView!!.settings.allowFileAccess = true

        if (Build.VERSION.SDK_INT >= Constants.SDK_MIN_API_VERSION) {
            webView!!.settings.mediaPlaybackRequiresUserGesture = false
        }
        mJavaScriptInterface?.let { webView!!.addJavascriptInterface(it, "Android") }
        val folderPath = "file:android_asset/"
        val fileName = htmlString
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
    val javaScriptInterface: GiftCatchJavaScriptInterface?
        get() = mJavaScriptInterface

    fun setGiftCatchListeners(
        listener: GiftCatchCompleteInterface,
        copyToClipboardInterface: GiftCatchCopyToClipboardInterface,
        showCodeInterface: GiftCatchShowCodeInterface
    ) {
        mListener = listener
        mCopyToClipboardInterface = copyToClipboardInterface
        mShowCodeInterface = showCodeInterface
    }

    companion object {
        const val TAG = "WebViewDialogFragment"

        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "htmlString"
        private const val ARG_PARAM2 = "response"
        private var mJavaScriptInterface: GiftCatchJavaScriptInterface? = null
        fun newInstance(htmlString: String?, response: String?): GiftCatchWebDialogFragment {
            val fragment = GiftCatchWebDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, htmlString)
            args.putString(ARG_PARAM2, response)
            mJavaScriptInterface = GiftCatchJavaScriptInterface(fragment, response!!)
            fragment.arguments = args
            return fragment
        }
    }
}