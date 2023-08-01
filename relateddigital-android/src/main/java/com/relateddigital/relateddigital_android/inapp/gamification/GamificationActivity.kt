package com.relateddigital.relateddigital_android.inapp.gamification

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap

/*class GamificationActivity : FragmentActivity(), GamificationCompleteInterface,
   GamificationCopyToClipboardInterface, GamificationShowCodeInterface {
   private var jsonStr: String? = ""
   private var response: Jackpot? = null
   private var jackpotPromotionCode = ""
   private var link = ""
   private lateinit var activity: FragmentActivity
   private lateinit var completeListener: JackpotCompleteInterface
   private lateinit var copyToClipboardListener: JackpotCopyToClipboardInterface
   private lateinit var showCodeListener: JackpotShowCodeInterface
   private var jackpotJsStr = ""

   companion object {
       private const val LOG_TAG = "Jackpot"
   }

   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       activity = this
       completeListener = this
       copyToClipboardListener = this
       showCodeListener = this
       val jsApi = JSApiClient.getClient(RelatedDigital.getRelatedDigitalModel(this).getRequestTimeoutInSecond())
           ?.create(ApiMethods::class.java)
       val headers = HashMap<String, String>()
       headers[Constants.USER_AGENT_REQUEST_KEY] = RelatedDigital.getRelatedDigitalModel(this).getUserAgent()
       val call: Call<ResponseBody> = jsApi?.getJackpotJsFile(headers)!!
       call.enqueue(object : Callback<ResponseBody?> {
           override fun onResponse(
               call: Call<ResponseBody?>,
               responseJs: Response<ResponseBody?>
           ) {
               if (responseJs.isSuccessful) {
                   Log.i(LOG_TAG, "Getting jackpot.js is successful!")
                   jackpotJsStr = responseJs.body()!!.string()

                   if(jackpotJsStr.isEmpty()) {
                       Log.e(LOG_TAG, "Getting jackpot.js failed!")
                       finish()
                   } else {
                       if (savedInstanceState != null) {
                           jsonStr = savedInstanceState.getString("jackpot-json-str", "")
                       } else {
                           val intent = intent
                           if (intent != null && intent.hasExtra("jackpot-data")) {
                               response = intent.getSerializableExtra("jackpot-data") as Jackpot?
                               if (response != null) {
                                   jsonStr = Gson().toJson(response)
                               } else {
                                   Log.e(LOG_TAG, "Could not get the jackpot data properly!")
                                   finish()
                               }
                           } else {
                               Log.e(LOG_TAG, "Could not get the jackpot data properly!")
                               finish()
                           }
                       }

                       if (jsonStr != null && jsonStr != "") {
                           val res = AppUtils.createJackpotCustomFontFiles(
                               activity, jsonStr, jackpotJsStr
                           )
                           if(res == null) {
                               Log.e(LOG_TAG, "Could not get the jackpot data properly!")
                               finish()
                           } else {
                               val webViewDialogFragment: JackpotWebDialogFragment =
                                   JackpotWebDialogFragment.newInstance(res[0], res[1], res[2])
                               webViewDialogFragment.setJackpotListeners(completeListener, copyToClipboardListener, showCodeListener)
                               if (!isFinishing && !supportFragmentManager.isDestroyed) {
                                   webViewDialogFragment.display(supportFragmentManager)
                               } else {
                                   Log.e(LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                                   finish()
                               }
                           }
                       } else {
                           Log.e(LOG_TAG, "Could not get the jackpot data properly!")
                           finish()
                       }
                   }
               } else {
                   Log.e(LOG_TAG, "Getting jackpot.js failed!")
                   finish()
               }
           }
           override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
               Log.e(LOG_TAG, "Getting jackpot.js failed! - ${t.message}")
               finish()
           }
       })
   }

   override fun onSaveInstanceState(outState: Bundle) {
       outState.putString("jackpot-json-str", jsonStr)
       super.onSaveInstanceState(outState)
   }

   override fun onDestroy() {
       super.onDestroy()
       if (jackpotPromotionCode.isNotEmpty()) {
           try {
               val extendedProps = Gson().fromJson(
                   URI(response!!.actiondata!!.extendedProps).path,
                   JackpotExtendedProps::class.java
               )

               if (!extendedProps.promocodeBannerButtonLabel.isNullOrEmpty()) {
                   if(ActivityUtils.parentActivity != null) {
                       val jackpotCodeBannerFragment =
                           JackpotCodeBannerFragment.newInstance(extendedProps, jackpotPromotionCode)

                       val transaction: FragmentTransaction =
                           (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                       transaction.replace(android.R.id.content, jackpotCodeBannerFragment)
                       transaction.commit()
                       ActivityUtils.parentActivity = null
                   }
               }
           } catch (e: Exception) {
               Log.e(LOG_TAG, "JackpotCodeBanner : " + e.message)
           }
       }

       if (link.isNotEmpty()) {
           val uri: Uri
           try {
               uri = Uri.parse(link)
               val viewIntent = Intent(Intent.ACTION_VIEW, uri)
               startActivity(viewIntent)
           } catch (e: Exception) {
               Log.w(LOG_TAG, "Can't parse notification URI, will not take any action", e)
           }
       }
   }

   override fun onCompleted() {
       finish()
   }

   override fun copyToClipboard(couponCode: String?, link: String?) {
       if(!couponCode.isNullOrEmpty()) {
           val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
           val clip = ClipData.newPlainText("Coupon Code", couponCode)
           clipboard.setPrimaryClip(clip)
           Toast.makeText(
               applicationContext,
               getString(R.string.copied_to_clipboard),
               Toast.LENGTH_LONG
           ).show()
       }
       if(!link.isNullOrEmpty()) {
           this.link = link
       }
       finish()
   }

   override fun onCodeShown(code: String) {
       jackpotPromotionCode = code
   }
}*/