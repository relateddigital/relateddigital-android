package com.relateddigital.androidexampleapp

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.relateddigital.androidexampleapp.databinding.ActivityLoginBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var exVisitor: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        exVisitor = Math.random().toString() + "test@gmail.com"
        binding.tvExvisitorId.setText(exVisitor)
        binding.btnLogin.setOnClickListener(View.OnClickListener {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("token", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    val token = task.result!!
                    val parameters = HashMap<String, String>()
                    parameters["OM.exVisitorID"] = "test9876@euromsg.com"
                    parameters["OM.sys.TokenID"] = token
                    parameters["OM.sys.AppID"] = "visilabs-android-test"
                    RelatedDigital.login(applicationContext, "Login", parameters, this)
                    Toast.makeText(applicationContext, "Login", Toast.LENGTH_LONG).show()
                })
        })
        binding.btnLogout.setOnClickListener(View.OnClickListener {
            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().clear().apply()
            val parameters = HashMap<String, String>()
            parameters["OM.sys.AppID"] = "visilabs-android-test"
            RelatedDigital.customEvent(applicationContext, "Logout", parameters)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext, "Logout", Toast.LENGTH_LONG).show()
        })
    }
}