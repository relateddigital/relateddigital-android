package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ChooseFavoriteGameElements : Serializable {
    @SerializedName("favorite_images" ) var favoriteImages : ArrayList<ChooseFavoriteFavoriteImages> = arrayListOf()
}