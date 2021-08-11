package com.relateddigital.relateddigital_android.model

import android.app.Activity

data class Request(val domain: Domain, val queryMap: HashMap<String,String>,
                   val headerMap: HashMap<String,String>, val parent: Activity?) {
}