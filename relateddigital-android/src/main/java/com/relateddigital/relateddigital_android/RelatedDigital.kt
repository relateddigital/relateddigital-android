package com.relateddigital.relateddigital_android

import android.content.Context
import android.widget.Toast

class RelatedDigital(
    private val context: Context,
    private val organizationId : String) {
    init {
        Toast.makeText(context, organizationId, Toast.LENGTH_SHORT).show()
    }
}