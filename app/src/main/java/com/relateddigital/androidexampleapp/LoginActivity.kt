package com.relateddigital.androidexampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.relateddigital.androidexampleapp.databinding.ActivityLoginBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var exVisitor: String? = null
    var token = RelatedDigital.getRelatedDigitalModel(this).getToken()
    var appID = RelatedDigital.getRelatedDigitalModel(this).getGoogleAppAlias()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        exVisitor = Math.random().toString() + "test@gmail.com"
        binding.tvExvisitorId.setText(exVisitor)
        binding.btnLogin.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            RelatedDigital.login(applicationContext, binding.tvExvisitorId.text.toString(), parameters, this)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_LONG).show()
        }
        binding.btnLogout.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().clear().apply()
            val parameters = HashMap<String, String>()
            parameters["OM.sys.AppID"] = appID
            RelatedDigital.customEvent(applicationContext, "Logout", parameters)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext, "Logout", Toast.LENGTH_LONG).show()
        }

        binding.btnPageview.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            parameters["OM.exVisitorID"] = binding.tvExvisitorId.text.toString()
            RelatedDigital.customEvent(applicationContext,"Homepage", parameters)
            Toast.makeText(applicationContext, "Homepage", Toast.LENGTH_LONG).show()
        }

        binding.btnProductview.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            parameters["OM.exVisitorID"] = binding.tvExvisitorId.text.toString()
            parameters["OM.pv"] = "1147254"
            parameters["OM.pn"] = "Sky Systems SKYMCN1SS4015 Suni Deri Takı İpliği Yeşil"
            parameters["OM.ppr"] = "30.0"
            parameters["OM.pv.1"] = "Sky Systems"
            parameters["OM.inv"] = "10"
            RelatedDigital.customEvent(applicationContext,"Product View", parameters)
            Toast.makeText(applicationContext, "Product View", Toast.LENGTH_LONG).show()
        }

        binding.btnCategoryview.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            parameters["OM.exVisitorID"] = binding.tvExvisitorId.text.toString()
            parameters["OM.clist"] = "3518"
            RelatedDigital.customEvent(applicationContext,"Category View", parameters)
            Toast.makeText(applicationContext, "Category View", Toast.LENGTH_LONG).show()
        }

        binding.btnCart.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            parameters["OM.exVisitorID"] = binding.tvExvisitorId.text.toString()
            parameters["OM.pbid"] = "c7e8999b-0872-4f16-a515-4d629f577387"
            parameters["OM.pb"] = "1147254"
            parameters["OM.ppr"] = "90.0"
            parameters["OM.pu"] = "3"
            RelatedDigital.customEvent(applicationContext,"Product View", parameters)
            Toast.makeText(applicationContext, "Product View", Toast.LENGTH_LONG).show()
        }
    }
}