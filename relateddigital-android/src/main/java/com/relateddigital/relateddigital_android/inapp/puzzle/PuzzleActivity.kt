package com.relateddigital.relateddigital_android.inapp.puzzle

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
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.api.ApiMethods
import com.relateddigital.relateddigital_android.api.JSApiClient
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.Puzzle
import com.relateddigital.relateddigital_android.model.PuzzleExtendedProps
import com.relateddigital.relateddigital_android.util.ActivityUtils
import com.relateddigital.relateddigital_android.util.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.HashMap

class PuzzleActivity : FragmentActivity(), PuzzleCompleteInterface,
    PuzzleCopyToClipboardInterface, PuzzleShowCodeInterface {
    private var jsonStr: String? = ""
    private var response: Puzzle? = null
    private var puzzlePromotionCode = ""
    private var link = ""
    private lateinit var activity: FragmentActivity
    private lateinit var completeListener: PuzzleCompleteInterface
    private lateinit var copyToClipboardListener: PuzzleCopyToClipboardInterface
    private lateinit var showCodeListener: PuzzleShowCodeInterface
    private var puzzleJsStr = ""

    companion object {
        private const val LOG_TAG = "Puzzle"
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
        val call: Call<ResponseBody> = jsApi?.getPuzzleJsFile(headers)!!
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                responseJs: Response<ResponseBody?>
            ) {
                if (responseJs.isSuccessful) {
                    Log.i(LOG_TAG, "Getting puzzle.js is successful!")
                    puzzleJsStr = responseJs.body()!!.string()

                    if(puzzleJsStr.isEmpty()) {
                        Log.e(LOG_TAG, "Getting puzzle.js failed!")
                        finish()
                    } else {
                        if (savedInstanceState != null) {
                            jsonStr = savedInstanceState.getString("puzzle-json-str", "")
                        } else {
                            val intent = intent
                            if (intent != null && intent.hasExtra("puzzle-data")) {
                                response = intent.getSerializableExtra("puzzle-data") as Puzzle?
                                if (response != null) {
                                    jsonStr = Gson().toJson(response)
                                } else {
                                    Log.e(LOG_TAG, "Could not get the puzzle data properly!")
                                    finish()
                                }
                            } else {
                                Log.e(LOG_TAG, "Could not get the puzzle data properly!")
                                finish()
                            }
                        }

                        if (jsonStr != null && jsonStr != "") {
                            val res = AppUtils.createPuzzleCustomFontFiles(
                                activity, jsonStr, puzzleJsStr
                            )
                            if(res == null) {
                                Log.e(LOG_TAG, "Could not get the puzzle data properly!")
                                finish()
                            } else {
                                val webViewDialogFragment: PuzzleWebDialogFragment =
                                    PuzzleWebDialogFragment.newInstance(res[0], res[1], res[2])
                                webViewDialogFragment.setPuzzleListeners(completeListener, copyToClipboardListener, showCodeListener)
                                if (!isFinishing && !supportFragmentManager.isDestroyed) {
                                    webViewDialogFragment.display(supportFragmentManager)
                                } else {
                                    Log.e(LOG_TAG, "Activity is finishing or FragmentManager is destroyed!")
                                    finish()
                                }
                            }
                        } else {
                            Log.e(LOG_TAG, "Could not get the puzzle data properly!")
                            finish()
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Getting puzzle.js failed!")
                    finish()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(LOG_TAG, "Getting puzzle.js failed! - ${t.message}")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("puzzle-json-str", jsonStr)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (puzzlePromotionCode.isNotEmpty()) {
            try {
                val extendedProps = Gson().fromJson(
                    URI(response!!.actiondata!!.extendedProps).path,
                    PuzzleExtendedProps::class.java
                )

                if (!extendedProps.promocodeBannerButtonLabel.isNullOrEmpty()) {
                    if(ActivityUtils.parentActivity != null) {
                        val puzzleCodeBannerFragment =
                            PuzzleCodeBannerFragment.newInstance(extendedProps, puzzlePromotionCode)

                        val transaction: FragmentTransaction =
                            (ActivityUtils.parentActivity as FragmentActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(android.R.id.content, puzzleCodeBannerFragment)
                        transaction.commit()
                        ActivityUtils.parentActivity = null
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "PuzzleCodeBanner : " + e.message)
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
        puzzlePromotionCode = code
    }
}