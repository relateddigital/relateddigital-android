package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

data class VisilabsProduct(
    @SerializedName("code")
    var code: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("img")
    var img: String? = null,

    @SerializedName("dest_url")
    var destUrl: String? = null,

    @SerializedName("brand")
    var brand: String? = null,

    @SerializedName("price")
    var price: Double? = null,

    @SerializedName("dprice")
    var dprice: Double? = null,

    @SerializedName("cur")
    var cur: String? = null,

    @SerializedName("dcur")
    var dcur: String? = null,

    @SerializedName("freeshipping")
    var freeshipping: Boolean? = null,

    @SerializedName("samedayshipping")
    var samedayshipping: Boolean? = null,

    @SerializedName("rating")
    var rating: Int? = null,

    @SerializedName("comment")
    var comment: Int? = null,

    @SerializedName("discount")
    var discount: Double? = null,

    @SerializedName("attr1")
    var attr1: String? = null,

    @SerializedName("attr2")
    var attr2: String? = null,

    @SerializedName("attr3")
    var attr3: String? = null,

    @SerializedName("attr4")
    var attr4: String? = null,

    @SerializedName("attr5")
    var attr5: String? = null,

    @SerializedName("attr6")
    var attr6: String? = null,

    @SerializedName("attr7")
    var attr7: String? = null,

    @SerializedName("attr8")
    var attr8: String? = null,

    @SerializedName("attr9")
    var attr9: String? = null,

    @SerializedName("attr10")
    var attr10: String? = null,

    @SerializedName("qs")
    var qs: String? = null,

    @SerializedName("variants2")
    var variants2: List<ProductVariant2>? = null
) : Serializable {

    companion object {
        fun fromJsonObject(jsonObject: JSONObject?): VisilabsProduct? {
            if (jsonObject == null) return null
            val product = VisilabsProduct()
            if (jsonObject.has("code") && !jsonObject.isNull("code")) product.code = jsonObject.optString("code")
            if (jsonObject.has("title") && !jsonObject.isNull("title")) product.title = jsonObject.optString("title")
            if (jsonObject.has("img") && !jsonObject.isNull("img")) product.img = jsonObject.optString("img")
            if (jsonObject.has("dest_url") && !jsonObject.isNull("dest_url")) product.destUrl = jsonObject.optString("dest_url")
            if (jsonObject.has("brand") && !jsonObject.isNull("brand")) product.brand = jsonObject.optString("brand")
            if (jsonObject.has("price") && !jsonObject.isNull("price")) product.price = jsonObject.optDouble("price", 0.0)
            if (jsonObject.has("dprice") && !jsonObject.isNull("dprice")) product.dprice = jsonObject.optDouble("dprice", 0.0)
            if (jsonObject.has("cur") && !jsonObject.isNull("cur")) product.cur = jsonObject.optString("cur")
            if (jsonObject.has("dcur") && !jsonObject.isNull("dcur")) product.dcur = jsonObject.optString("dcur")
            if (jsonObject.has("freeshipping") && !jsonObject.isNull("freeshipping")) product.freeshipping = jsonObject.optBoolean("freeshipping", false)
            if (jsonObject.has("samedayshipping") && !jsonObject.isNull("samedayshipping")) product.samedayshipping = jsonObject.optBoolean("samedayshipping", false)
            if (jsonObject.has("rating") && !jsonObject.isNull("rating")) product.rating = jsonObject.optInt("rating", 0)
            if (jsonObject.has("comment") && !jsonObject.isNull("comment")) product.comment = jsonObject.optInt("comment", 0)
            if (jsonObject.has("discount") && !jsonObject.isNull("discount")) product.discount = jsonObject.optDouble("discount", 0.0)
            if (jsonObject.has("attr1") && !jsonObject.isNull("attr1")) product.attr1 = jsonObject.optString("attr1")
            if (jsonObject.has("attr2") && !jsonObject.isNull("attr2")) product.attr2 = jsonObject.optString("attr2")
            if (jsonObject.has("attr3") && !jsonObject.isNull("attr3")) product.attr3 = jsonObject.optString("attr3")
            if (jsonObject.has("attr4") && !jsonObject.isNull("attr4")) product.attr4 = jsonObject.optString("attr4")
            if (jsonObject.has("attr5") && !jsonObject.isNull("attr5")) product.attr5 = jsonObject.optString("attr5")
            if (jsonObject.has("attr6") && !jsonObject.isNull("attr6")) product.attr6 = jsonObject.optString("attr6")
            if (jsonObject.has("attr7") && !jsonObject.isNull("attr7")) product.attr7 = jsonObject.optString("attr7")
            if (jsonObject.has("attr8") && !jsonObject.isNull("attr8")) product.attr8 = jsonObject.optString("attr8")
            if (jsonObject.has("attr9") && !jsonObject.isNull("attr9")) product.attr9 = jsonObject.optString("attr9")
            if (jsonObject.has("attr10") && !jsonObject.isNull("attr10")) product.attr10 = jsonObject.optString("attr10")
            if (jsonObject.has("qs") && !jsonObject.isNull("qs")) product.qs = jsonObject.optString("qs")

            if (jsonObject.has("variants2") && !jsonObject.isNull("variants2")) {
                val variantsArray = jsonObject.optJSONArray("variants2")
                if (variantsArray != null) {
                    val variantsList = mutableListOf<ProductVariant2>()
                    for (i in 0 until variantsArray.length()) {
                        val variantObj = variantsArray.optJSONObject(i)
                        val v = ProductVariant2.fromJsonObject(variantObj)
                        if (v != null) {
                            variantsList.add(v)
                        }
                    }
                    product.variants2 = variantsList
                }
            }
            return product
        }
    }
}
