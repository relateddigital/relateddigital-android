package com.relateddigital.relateddigital_android.model

class LoadBalanceCookie {
    private var loggerCookieKey = ""
    private var loggerCookieValue = ""
    private var realTimeCookieKey = ""
    private var realTimeCookieValue = ""
    private var loggerOM3rdCookieValue = ""
    private var realOM3rdTimeCookieValue = ""

    fun getLoggerCookieKey(): String {
        return loggerCookieKey
    }

    fun setLoggerCookieKey(loggerCookieKey: String) {
        this.loggerCookieKey = loggerCookieKey
    }

    fun getLoggerCookieValue(): String {
        return loggerCookieValue
    }

    fun setLoggerCookieValue(loggerCookieValue: String) {
        this.loggerCookieValue = loggerCookieValue
    }

    fun getRealTimeCookieKey(): String {
        return realTimeCookieKey
    }

    fun setRealTimeCookieKey(realTimeCookieKey: String) {
        this.realTimeCookieKey = realTimeCookieKey
    }

    fun getRealTimeCookieValue(): String {
        return realTimeCookieValue
    }

    fun setRealTimeCookieValue(realTimeCookieValue: String) {
        this.realTimeCookieValue = realTimeCookieValue
    }

    fun getLoggerOM3rdCookieValue(): String {
        return loggerOM3rdCookieValue
    }

    fun setLoggerOM3rdCookieValue(loggerOM3rdCookieValue: String) {
        this.loggerOM3rdCookieValue = loggerOM3rdCookieValue
    }

    fun getRealOM3rdTimeCookieValue(): String {
        return realOM3rdTimeCookieValue
    }

    fun setRealOM3rdTimeCookieValue(realOM3rdTimeCookieValue: String) {
        this.realOM3rdTimeCookieValue = realOM3rdTimeCookieValue
    }
}