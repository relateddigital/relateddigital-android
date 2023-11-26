package com.relateddigital.relateddigital_android.inapp.customactions


import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class CustomActionsWebDialogFragment : DialogFragment() {
    /*  private var webView: WebView? = null
      private var mResponse: String? = null
      private var baseUrl: String? = ""
      private var htmlString: String? = ""
      private var mIsRotation = false */
      private lateinit var mListener: CustomActionsCompleteInterface
      private lateinit var mCopyToClipboardInterface: CustomActionsCopyToClipboardInterface
      private lateinit var mShowCodeInterface: CustomActionsShowCodeInterface

      fun display(fragmentManagerLoc: FragmentManager?): CustomActionsWebDialogFragment {
          val ft = fragmentManagerLoc?.beginTransaction()
          ft?.add(this, TAG)
          ft?.commitAllowingStateLoss()
          return this
      }
    /*
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
                  baseUrl = requireArguments().getString("baseUrl")
                  htmlString = requireArguments().getString("htmlString")
                  mResponse = requireArguments().getString("response")
                  mJavaScriptInterface = JackpotJavaScriptInterface(this, mResponse!!)
                  mJavaScriptInterface!!.setJackpotListeners(mListener, mCopyToClipboardInterface, mShowCodeInterface)
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
              webView!!.loadDataWithBaseURL(baseUrl, htmlString!!, "text/html", "utf-8", "about:blank")
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
          val javaScriptInterface: JackpotJavaScriptInterface?
              get() = mJavaScriptInterface
    */
      fun setCustomActionsListeners(
          listener: CustomActionsCompleteInterface,
          copyToClipboardInterface: CustomActionsCopyToClipboardInterface,
          showCodeInterface: CustomActionsShowCodeInterface
      ) {
          mListener = listener
          mCopyToClipboardInterface = copyToClipboardInterface
          mShowCodeInterface = showCodeInterface
      }

      companion object {
          const val TAG = "WebViewDialogFragment"

          // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
          private const val ARG_PARAM1 = "response"
          private const val ARG_PARAM2 = "baseUrl"
          private const val ARG_PARAM3 = "htmlString"
          private var mJavaScriptInterface: CustomActionsJavaScriptInterface? = null
          fun newInstance(baseUrl: String?, htmlString: String?, response: String?): CustomActionsWebDialogFragment {
              val fragment = CustomActionsWebDialogFragment()
              val args = Bundle()
              args.putString(ARG_PARAM1, response)
              args.putString(ARG_PARAM2, baseUrl)
              args.putString(ARG_PARAM3, htmlString)
              mJavaScriptInterface = CustomActionsJavaScriptInterface(fragment, response!!)
              fragment.arguments = args
              return fragment
          }
      }
}