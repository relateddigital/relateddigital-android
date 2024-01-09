package com.relateddigital.relateddigital_android.inapp.customactions

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.inapp.notification.InAppNotificationFragment
import com.relateddigital.relateddigital_android.model.CustomActions
import com.relateddigital.relateddigital_android.model.CustomActionsExtendedProps
import com.relateddigital.relateddigital_android.model.Drawer
import com.relateddigital.relateddigital_android.model.DrawerExtendedProps
import com.relateddigital.relateddigital_android.util.AppUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CustomActionFragment : Fragment() {
    private var response: CustomActions? = null
    private var mExtendedProps: CustomActionsExtendedProps? = null
    private var position : String? = null
    private var width : Int? = null
    private var height : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        response = if (savedInstanceState != null) {
            savedInstanceState.getSerializable("customactions") as CustomActions?
        } else {
            requireArguments().getSerializable(ARG_PARAM1) as CustomActions?
        }

        if (response == null) {
            Log.e(LOG_TAG, "The data could not get properly!")
            endFragment()
        } else {
            try {
                mExtendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.extendedProps).path,
                    CustomActionsExtendedProps::class.java
                )
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                endFragment()
            } catch (e: Exception) {
                e.printStackTrace()
                endFragment()
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById(R.id.webView)
        val webSettings: WebSettings = webView.settings
        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        //Log.e("Deneme",html)

        var jsCode = response!!.actiondata!!.javascript!!
        var htmlContent = response!!.actiondata!!.content!!



        Log.e("Deneme",htmlContent)
        webView.evaluateJavascript(jsCode, null)
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", "about:blank")


        if(!mExtendedProps!!.position.isNullOrEmpty()){
             position = mExtendedProps!!.position
        }
        else {
            position = "middleCenter"
        }
        if(!mExtendedProps!!.height.toString().isNullOrEmpty()){
            height = mExtendedProps!!.height
        }
        else {
            height = 100
        }
        if (!mExtendedProps!!.width.toString().isNullOrEmpty()) {
            width = mExtendedProps!!.width
        }
        else {
            width = 100
        }

        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val closeParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        when (position) {
            "topLeft" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.topMargin= 45
                closeParams.leftMargin=((screenWidth * width!! /100.0)-90).toInt()
            }
            "topCenter" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.topMargin= 45
                closeParams.leftMargin=((screenWidth * width!! /200.0)+(screenWidth /2)-90).toInt()


            }
            "topRight" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                closeParams.topMargin= 45
                closeParams.leftMargin=45
            }
            "middleRight" -> {
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                closeParams.bottomMargin= ((screenHeight * height!! /200.0)+(screenHeight /2)-180).toInt()
                closeParams.leftMargin=45

            }
            "bottomRight" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                closeParams.bottomMargin=   ((screenHeight * height!! /100.0)-90).toInt()
                closeParams.leftMargin=45
            }
            "bottomCenter" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.bottomMargin=   ((screenHeight * height!! /100.0)-90).toInt()
                closeParams.leftMargin= ((screenWidth * width!! /200.0)+(screenWidth /2)-90).toInt()
            }
            "bottomLeft" -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.bottomMargin=   ((screenHeight * height!! /100.0)-90).toInt()
                closeParams.leftMargin=((screenWidth * width!! /100.0)-90).toInt()
            }
            "middleLeft" -> {
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.bottomMargin= ((screenHeight * height!! /200.0)+(screenHeight /2)-180).toInt()
                closeParams.leftMargin=((screenWidth * width!! /100.0)-90).toInt()
            }
            "middleCenter" -> {
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                closeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                closeParams.bottomMargin= ((screenHeight * height!! /200.0)+(screenHeight /2)-180).toInt()
                closeParams.leftMargin=((screenWidth * width!! /200.0)+(screenWidth /2)-90).toInt()

            }
            else -> {
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
            }
        }



        params.width = (screenWidth * width!! / 100.0).toInt()
        params.height = (screenHeight * height!! / 100.0).toInt()



        webView.layoutParams = params
        closeButton.layoutParams = closeParams



        webView.evaluateJavascript(jsCode) { result ->
            // JavaScript kodu çalıştıktan sonra yapılacak işlemler
            // result değişkeni, JavaScript kodunun çıktısını içerir
        }

       // webView.loadUrl("https://web.whatsapp.com/")
        webView.webViewClient = WebViewClient()
        closeButton.setOnClickListener {
            endFragment()
        }


    }

    companion object {
        private const val LOG_TAG = "CustomActionNotification"
        private const val ARG_PARAM1 = "dataKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param model Parameter 1.
         * @return A new instance of fragment InAppNotificationFragment.
         */
        fun newInstance(model: CustomActions): CustomActionFragment {
            val fragment = CustomActionFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, model)
            fragment.arguments = args
            return fragment
        }
    }

    private fun endFragment() {
        if (activity != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(this@CustomActionFragment)
                .commit()
        }
    }

    private fun formatHtmlContent(originalHtml: String): String {
        return originalHtml.trimIndent()
            .replace("\n", "")
            .replace("\\s+".toRegex(), " ")
    }

    private fun formatJsCode(originalJsCode: String): String {
        // JavaScript kodunu düzenlemek için uygun bir kod
        // Bu örnek, satır sonlarından kurtulmak ve boşlukları temizlemek için kullanılabilir
        return originalJsCode.trimIndent()
            .replace("\n", "")
            .replace("\\s+".toRegex(), " ")
    }

    private fun setRoundedCorner(radiusDP: Float) {
        //val webView: WebView = view.findViewById(R.id.webView)
        val cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            radiusDP,
            resources.displayMetrics
        ).toInt()

        val roundedCornersDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
          //  cornerRadius = cornerRadius.toFloat()
            setColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //webView.background = roundedCornersDrawable
        } else {
           // @Suppress("DEPRECATION")
            //webView.setBackgroundDrawable(roundedCornersDrawable)
        }
    }


}