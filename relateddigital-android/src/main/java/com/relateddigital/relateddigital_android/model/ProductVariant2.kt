package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

data class ProductVariant2(
    @SerializedName("color")
    var color: String? = null,

    @SerializedName("colors")
    var colors: List<ProductVariant2Color>? = null
) : Serializable {

    companion object {
        fun fromJsonObject(jsonObject: JSONObject?): ProductVariant2? {
            if (jsonObject == null) return null
            val variant = ProductVariant2()
            if (jsonObject.has("color") && !jsonObject.isNull("color")) {
                variant.color = jsonObject.optString("color")
            }
            if (jsonObject.has("colors") && !jsonObject.isNull("colors")) {
                val colorsArray = jsonObject.optJSONArray("colors")
                if (colorsArray != null) {
                    val list = mutableListOf<ProductVariant2Color>()
                    for (i in 0 until colorsArray.length()) {
                        val colorObj = colorsArray.optJSONObject(i)
                        val c = ProductVariant2Color.fromJsonObject(colorObj)
                        if (c != null) {
                            list.add(c)
                        }
                    }
                    variant.colors = list
                }
            }
            return variant
        }
    }
}
