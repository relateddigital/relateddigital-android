package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

data class ProductVariant2Color(
    @SerializedName("size")
    var size: String? = null,

    @SerializedName("product_id")
    var productId: Int? = null,

    @SerializedName("cart_id")
    var cartId: String? = null,

    @SerializedName("stock")
    var stock: Int? = null
) : Serializable {

    val product_id: Int?
        get() = productId

    val cart_id: String?
        get() = cartId

    companion object {
        fun fromJsonObject(jsonObject: JSONObject?): ProductVariant2Color? {
            if (jsonObject == null) return null
            val color = ProductVariant2Color()
            if (jsonObject.has("size") && !jsonObject.isNull("size")) {
                color.size = jsonObject.optString("size")
            }
            if (jsonObject.has("product_id") && !jsonObject.isNull("product_id")) {
                color.productId = try {
                    jsonObject.getInt("product_id")
                } catch (e: Exception) {
                    try {
                        jsonObject.getString("product_id").toInt()
                    } catch (ignored: Exception) {
                        null
                    }
                }
            }
            if (jsonObject.has("cart_id") && !jsonObject.isNull("cart_id")) {
                color.cartId = jsonObject.optString("cart_id")
            }
            if (jsonObject.has("stock") && !jsonObject.isNull("stock")) {
                color.stock = try {
                    jsonObject.getInt("stock")
                } catch (e: Exception) {
                    try {
                        jsonObject.getString("stock").toInt()
                    } catch (ignored: Exception) {
                        null
                    }
                }
            }
            return color
        }
    }
}
