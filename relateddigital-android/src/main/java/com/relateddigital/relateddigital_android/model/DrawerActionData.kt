package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DrawerActionData : Serializable{
    @SerializedName("shape")
    private var shape: String? = null

    @SerializedName("pos")
    private var pos: String? = null

    @SerializedName("content_minimized_image")
    private var contentMinimizedImage: String? = null

    @SerializedName("content_minimized_text")
    private var contentMinimizedText: String? = null

    @SerializedName("content_maximized_image")
    private var contentMaximizedImage: String? = null

    @SerializedName("waiting_time")
    private var waitingTime: Int? = null

    @SerializedName("ios_lnk")
    private var iosLnk: String? = null

    @SerializedName("android_lnk")
    private var androidLnk: String? = null

    @SerializedName("ExtendedProps")
    private var extendedProps: String? = null

    @SerializedName("report")
    private var report: DrawerReport? = null

    @SerializedName("copybutton_function")
    private var buttonFunction: String? = null

    @SerializedName("staticcode")
    private var staticCode: String? = null

    fun getShape(): String? {
        return shape
    }

    fun setShape(shape: String?) {
        this.shape = shape
    }

    fun getPos(): String? {
        return pos
    }

    fun setPos(pos: String?) {
        this.pos = pos
    }

    fun getContentMinimizedImage(): String? {
        return contentMinimizedImage
    }

    fun setContentMinimizedImage(contentMinimizedImage: String?) {
        this.contentMinimizedImage = contentMinimizedImage
    }

    fun getContentMinimizedText(): String? {
        return contentMinimizedText
    }

    fun setContentMinimizedText(contentMinimizedText: String?) {
        this.contentMinimizedText = contentMinimizedText
    }

    fun getContentMaximizedImage(): String? {
        return contentMaximizedImage
    }

    fun setContentMaximizedImage(contentMaximizedImage: String?) {
        this.contentMaximizedImage = contentMaximizedImage
    }

    fun getWaitingTime(): Int? {
        return waitingTime
    }

    fun setWaitingTime(waitingTime: Int?) {
        this.waitingTime = waitingTime
    }

    fun getIosLnk(): String? {
        return iosLnk
    }

    fun setIosLnk(iosLnk: String?) {
        this.iosLnk = iosLnk
    }

    fun getAndroidLnk(): String? {
        return androidLnk
    }

    fun setAndroidLnk(androidLnk: String?) {
        this.androidLnk = androidLnk
    }

    fun getExtendedProps(): String? {
        return extendedProps
    }

    fun setExtendedProps(extendedProps: String?) {
        this.extendedProps = extendedProps
    }

    fun getReport(): DrawerReport? {
        return report
    }

    fun setReport(report: DrawerReport?) {
        this.report = report
    }

    fun getButtonFunction(): String? {
        return buttonFunction
    }

    fun getStaticCode(): String? {
        return staticCode
    }
}