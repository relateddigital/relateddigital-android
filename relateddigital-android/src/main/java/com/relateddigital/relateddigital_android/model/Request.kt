package com.relateddigital.relateddigital_android.model

data class Request(val domain: Domain, val queryMap: HashMap<String,String>,
                   val headerMap: HashMap<String,String>) {
}