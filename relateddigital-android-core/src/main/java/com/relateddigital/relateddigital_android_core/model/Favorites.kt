package com.relateddigital.relateddigital_android_core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Favorites : Serializable {
    @SerializedName("ageGroup")
    var ageGroup: Array<String?>? = null

    @SerializedName("attr1")
    var attr1: Array<String?>? = null

    @SerializedName("attr2")
    var attr2: Array<String?>? = null

    @SerializedName("attr3")
    var attr3: Array<String?>? = null

    @SerializedName("attr4")
    var attr4: Array<String?>? = null

    @SerializedName("attr5")
    var attr5: Array<String?>? = null

    @SerializedName("attr6")
    var attr6: Array<String?>? = null

    @SerializedName("attr7")
    var attr7: Array<String?>? = null

    @SerializedName("attr8")
    var attr8: Array<String?>? = null

    @SerializedName("attr9")
    var attr9: Array<String?>? = null

    @SerializedName("attr10")
    var attr10: Array<String?>? = null

    @SerializedName("brand")
    var brand: Array<String?>? = null

    @SerializedName("category")
    var category: Array<String?>? = null

    @SerializedName("color")
    var color: Array<String?>? = null

    @SerializedName("gender")
    var gender: Array<String?>? = null

    @SerializedName("material")
    var material: Array<String?>? = null

    @SerializedName("title")
    var title: Array<String?>? = null

    override fun toString(): String {
        return "Favorites [" +
                "ageGroup = " + Arrays.toString(ageGroup) +
                "attr1 = " + Arrays.toString(attr1) +
                "attr2 = " + Arrays.toString(attr2) +
                "attr3 = " + Arrays.toString(attr3) +
                "attr4 = " + Arrays.toString(attr4) +
                "attr5 = " + Arrays.toString(attr5) +
                "attr6 = " + Arrays.toString(attr6) +
                "attr7 = " + Arrays.toString(attr7) +
                "attr8 = " + Arrays.toString(attr8) +
                "attr9 = " + Arrays.toString(attr9) +
                "attr10 = " + Arrays.toString(attr10) +
                "brand = " + Arrays.toString(brand) +
                "category = " + Arrays.toString(category) +
                "color = " + Arrays.toString(color) +
                "gender = " + Arrays.toString(gender) +
                "material = " + Arrays.toString(material) +
                ", title = " + Arrays.toString(title) + "]"
    }
}