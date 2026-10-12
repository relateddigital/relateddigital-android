package com.relateddigital.androidexampleapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.relateddigital.androidexampleapp.databinding.ActivityEventBinding
import com.relateddigital.relateddigital_android.RelatedDigital
import java.util.*

class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding
    private var exVisitor: String? = null
    var token = RelatedDigital.getRelatedDigitalModel(this).getToken()
    var appID = RelatedDigital.getRelatedDigitalModel(this).getGoogleAppAlias()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
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
            RelatedDigital.customEvent(applicationContext,"Homepage", parameters, this)
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
            RelatedDigital.customEvent(applicationContext,"Product View", parameters, this)
            Toast.makeText(applicationContext, "Product View", Toast.LENGTH_LONG).show()
        }

        binding.btnCategoryview.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            parameters["OM.exVisitorID"] = binding.tvExvisitorId.text.toString()
            parameters["OM.clist"] = "3518"
            RelatedDigital.customEvent(applicationContext,"Category View", parameters, this)
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
            RelatedDigital.customEvent(applicationContext,"Product View", parameters, this)
            Toast.makeText(applicationContext, "Product View", Toast.LENGTH_LONG).show()
        }
        binding.btnSendcampaign.setOnClickListener {
            val parameters = HashMap<String, String>()
            parameters["OM.sys.TokenID"] = token
            parameters["OM.sys.AppID"] = appID
            parameters["OM.exVisitorID"] = binding.tvExvisitorId.text.toString()
            parameters["utm_campaign"] = "hosgeldin"
            parameters["utm_source"] = "relateddigital"
            parameters["utm_medium"] = "push"
            parameters["utm_content"] = "campaign_content"
            parameters["utm_term"] = "utmterm"
            RelatedDigital.sendCampaignParameters(applicationContext, parameters, this)
            Toast.makeText(applicationContext, "Send Campaign", Toast.LENGTH_LONG).show()
        }

        setupCaptureTests()
    }

    override fun onResume() {
        super.onResume()
        refreshCaptureIds()
    }

    // region Capture

    private enum class CaptureTest(val title: String) {
        VIEW_ITEM("capture: rmc_view_item"),
        ADD_TO_CART("capture: rmc_add_to_cart"),
        PURCHASE("capture: rmc_purchase (nested items)"),
        LOGIN_AND_CAPTURE("login + capture: rmc_login"),
        ALL_PROPERTY_TYPES("capture: all property types"),
        SET_PERSON_PROPERTIES("setPersonProperties (email)"),
        BATCH("capture: 25 events (batch)"),
        FLUSH("flushCapture"),
        LOGOUT("logout (new anonymous id)")
    }

    private fun setupCaptureTests() {
        CaptureTest.values().forEach { test ->
            val button = Button(this).apply {
                text = test.title
                isAllCaps = false
                setOnClickListener { runCaptureTest(test) }
            }
            binding.llCaptureButtons.addView(
                button,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            )
        }
        // Mevcut Login / Logout butonlarından sonra güncellemek için dokunmak yeterli.
        binding.tvCaptureIds.setOnClickListener { refreshCaptureIds() }
        refreshCaptureIds()
    }

    private fun runCaptureTest(test: CaptureTest) {
        when (test) {
            CaptureTest.VIEW_ITEM -> capture("rmc_view_item", mapOf(
                "item_id" to 1001,
                "item_name" to "Shoei NXR2",
                "item_brand" to "Shoei",
                "item_category" to "Motor Kaskları",
                "item_variant" to "Mat Siyah / L",
                "original_price" to 2000,
                "discounted_price" to 1500,
                "discount_rate" to 20,
                "currency" to "TRY",
                "cart_price" to 1000,
                "product_rating" to 4.8,
                "review_count" to 126,
                "stock_quantity" to 3,
                "stock_status" to "low_stock",
                "list_source" to "search",
                "search_term" to "kask",
                "product_campaign" to "Summer Sale"
            ))
            CaptureTest.ADD_TO_CART -> capture("rmc_add_to_cart", mapOf(
                "item_id" to 1001,
                "item_name" to "Shoei NXR2",
                "quantity" to 1,
                "price" to 1500,
                "currency" to "TRY",
                "cart_total" to 2500
            ))
            CaptureTest.PURCHASE -> capture("rmc_purchase", mapOf(
                "order_id" to "ORD-${(1000..9999).random()}",
                "revenue" to 2750.5,
                "currency" to "TRY",
                "items" to listOf(
                    mapOf("item_id" to 1001, "quantity" to 1, "price" to 1500),
                    mapOf("item_id" to 2002, "quantity" to 2, "price" to 625.25)
                )
            ))
            CaptureTest.LOGIN_AND_CAPTURE -> {
                val exVisitorId = binding.tvExvisitorId.text.toString().trim()
                if (exVisitorId.isEmpty()) {
                    Toast.makeText(applicationContext, "exVisitorId can not be empty", Toast.LENGTH_SHORT).show()
                    return
                }
                // login, capture tarafında $identify gönderir (anonim id + exVisitorId birleşir);
                // ardından gelen rmc_login exVisitorId ile gider.
                RelatedDigital.login(applicationContext, exVisitorId)
                capture("rmc_login", mapOf("method" to "email"))
            }
            CaptureTest.ALL_PROPERTY_TYPES -> capture("rmc_property_types", mapOf(
                "string" to "text",
                "int" to 42,
                "double" to 3.14,
                "bool" to true,
                "date" to Date(),
                "url" to Uri.parse("https://www.relateddigital.com"),
                "list" to listOf(1, "two", 3.0),
                "map" to mapOf("nested" to true),
                "nan" to Double.NaN // gönderilmez, logcat'e uyarı düşer
            ))
            CaptureTest.SET_PERSON_PROPERTIES -> {
                val email = binding.etCaptureEmail.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(applicationContext, "email can not be empty", Toast.LENGTH_SHORT).show()
                    return
                }
                RelatedDigital.setPersonProperties(
                    applicationContext,
                    mapOf("email" to email),
                    mapOf("first_seen_app" to "ExampleApp")
                )
                Toast.makeText(applicationContext, "\$set queued", Toast.LENGTH_SHORT).show()
            }
            CaptureTest.BATCH -> {
                // 20. event'te kuyruk beklemeden gönderilir, kalan 5'i en geç 10 sn içinde gider.
                repeat(25) { RelatedDigital.capture(applicationContext, "rmc_batch_test", mapOf("index" to it + 1)) }
                Toast.makeText(applicationContext, "25 events queued", Toast.LENGTH_SHORT).show()
            }
            CaptureTest.FLUSH -> {
                RelatedDigital.flushCapture(applicationContext)
                Toast.makeText(applicationContext, "Capture queue flushed", Toast.LENGTH_SHORT).show()
            }
            CaptureTest.LOGOUT -> {
                RelatedDigital.logout(applicationContext)
                Toast.makeText(applicationContext, "Logged out", Toast.LENGTH_SHORT).show()
            }
        }
        refreshCaptureIds()
    }

    private fun capture(event: String, properties: Map<String, Any?>) {
        RelatedDigital.capture(applicationContext, event, properties)
        Toast.makeText(applicationContext, "$event queued", Toast.LENGTH_SHORT).show()
    }

    private fun refreshCaptureIds() {
        val distinctId = RelatedDigital.getDistinctId(applicationContext)
        val anonymousId = RelatedDigital.getAnonymousId(applicationContext)
        val state = if (distinctId == anonymousId) "anonymous" else "identified"
        binding.tvCaptureIds.text = "distinct_id ($state):\n$distinctId\n\nanonymous_id:\n$anonymousId\n\n" +
            "Tap to refresh. In debug builds request bodies are logged to logcat (tag: RDCapture)."
    }

    // endregion
}