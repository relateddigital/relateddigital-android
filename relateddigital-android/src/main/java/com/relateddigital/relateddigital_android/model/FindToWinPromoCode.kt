package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FindToWinPromoCode: Serializable {
    @SerializedName("rangebottom")
    var rangeBottom: Int? = null

    @SerializedName("rangetop")
    var rangeTop: Int? = null

    @SerializedName("staticcode")
    var staticCode: String? = null
}