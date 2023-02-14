package com.relateddigital.relateddigital_android_core.push

import com.relateddigital.relateddigital_android_core.model.Message

interface PushMessageInterface {
    fun success(pushMessages: List<Message>)
    fun fail(errorMessage: String)
}