package com.relateddigital.relateddigital_android.inapp.inappmessages.inlineNpsWithNumbers

import java.io.Serializable

interface NpsItemClickListener : Serializable {
    fun npsItemClicked(npsLink: String?)
}