package com.relateddigital.relateddigital_android.push

import com.relateddigital.relateddigital_android.model.Message

interface PushMessageInterface {
    fun success(pushMessages: List<Message>)
    fun fail(errorMessage: String)
}