package com.relateddigital.androidexampleapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.relateddigital.androidexampleapp.databinding.ActivityInlineNpsWithNumbersBinding


import com.relateddigital.relateddigital_android.inapp.inappmessages.inlineNpsWithNumbers.NpsItemClickListener
import java.util.*

class InlineNpsWithNumbersActivity : AppCompatActivity() {
    private val LOG_TAG = "NpsActivity"
    private lateinit var binding: ActivityInlineNpsWithNumbersBinding
    private var npsItemClickListener: NpsItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInlineNpsWithNumbersBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)


        npsItemClickListener = object : NpsItemClickListener {
            override fun npsItemClicked(npsLink: String?) {
                Toast.makeText(applicationContext, npsLink, Toast.LENGTH_SHORT).show()
                Log.i("link nps", npsLink!!)
                try {
                    val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(npsLink))
                    startActivity(viewIntent)
                } catch (e: Exception) {
                    Log.e(
                        LOG_TAG,
                        "The link is not formatted properly!"
                    )
                }
            }
        }

        val properties = HashMap<String, String>()
        properties.put("OM.inapptype", "nps_with_numbers")

        binding!!.inlineNps.setNpsWithNumberAction(
            applicationContext,
            properties,
            npsItemClickListener,
            this@InlineNpsWithNumbersActivity
        )
    }
}