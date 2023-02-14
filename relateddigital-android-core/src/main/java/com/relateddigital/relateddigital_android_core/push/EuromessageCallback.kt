package com.relateddigital.relateddigital_android_core.push

interface EuromessageCallback {
    fun success()
    fun fail(errorMessage: String?)
}