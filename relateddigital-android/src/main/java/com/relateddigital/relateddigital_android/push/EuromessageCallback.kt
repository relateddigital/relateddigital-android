package com.relateddigital.relateddigital_android.push

interface EuromessageCallback {
    fun success()
    fun fail(errorMessage: String?)
}