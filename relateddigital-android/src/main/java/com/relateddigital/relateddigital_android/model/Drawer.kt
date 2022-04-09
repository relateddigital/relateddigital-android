package com.relateddigital.relateddigital_android.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Drawer : Serializable{
    @SerializedName("actid")
    private var actId: Int? = null

    @SerializedName("title")
    private var title: String? = null

    @SerializedName("actiontype")
    private var actionType: String? = null

    @SerializedName("actiondata")
    private var actionData: DrawerActionData? = null

    fun getActId(): Int? {
        return actId
    }

    fun setActId(actId: Int?) {
        this.actId = actId
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getActionType(): String? {
        return actionType
    }

    fun setActionType(actionType: String?) {
        this.actionType = actionType
    }

    fun getActionData(): DrawerActionData? {
        return actionData
    }

    fun setActionData(actionData: DrawerActionData?) {
        this.actionData = actionData
    }
}