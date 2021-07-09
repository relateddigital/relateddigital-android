package com.relateddigital.relateddigital_android

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.relateddigital.relateddigital_android.constants.Constants
import com.relateddigital.relateddigital_android.model.RelatedDigitalModel
import com.relateddigital.relateddigital_android.util.SharedPref

object RelatedDigital {
    private var model : RelatedDigitalModel? = null
    const val LOG_TAG : String = "RelatedDigital"

    @JvmStatic
    fun init(context: Context,
             isPushNotificationEnabled: Boolean = false,
             isInAppNotificationEnabled: Boolean = false,
             isGeofenceEnabled: Boolean = false,
             googleAppAlias: String,
             huaweiAppAlias: String,
             organizationId: String,
             profileId: String,
             dataSource: String,
             requestTimeoutInSecond: Int = 30,
             maxGeofenceCount: Int = 100) {

        model = RelatedDigitalModel(isPushNotificationEnabled,
                isInAppNotificationEnabled, isGeofenceEnabled, googleAppAlias,
                huaweiAppAlias, organizationId, profileId, dataSource,
                requestTimeoutInSecond, maxGeofenceCount)

        model?.saveToSharedPrefs(context)
    }

    @JvmStatic
    fun setIsPushNotificationEnabled(context: Context, isPushNotificationEnabled: Boolean) {
        if(model != null) {
            model!!.setIsPushNotificationEnabled(context, isPushNotificationEnabled)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setIsPushNotificationEnabled(context, isPushNotificationEnabled)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = isPushNotificationEnabled,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setIsInAppNotificationEnabled(context: Context, isInAppNotificationEnabled: Boolean) {
        if(model != null) {
            model!!.setIsInAppNotificationEnabled(context, isInAppNotificationEnabled)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setIsInAppNotificationEnabled(context, isInAppNotificationEnabled)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = isInAppNotificationEnabled, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setIsGeofenceEnabled(context: Context, isGeofenceEnabled: Boolean) {
        if(model != null) {
            model!!.setIsGeofenceEnabled(context, isGeofenceEnabled)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setIsGeofenceEnabled(context, isGeofenceEnabled)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = isGeofenceEnabled,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setGoogleAppAlias(context: Context, googleAppAlias: String) {
        if(model != null) {
            model!!.setGoogleAppAlias(context, googleAppAlias)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setGoogleAppAlias(context, googleAppAlias)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = googleAppAlias, huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setHuaweiAppAlias(context: Context, huaweiAppAlias: String) {
        if(model != null) {
            model!!.setHuaweiAppAlias(context, huaweiAppAlias)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setHuaweiAppAlias(context, huaweiAppAlias)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = huaweiAppAlias, organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setOrganizationId(context: Context, organizationId: String) {
        if(model != null) {
            model!!.setOrganizationId(context, organizationId)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setOrganizationId(context, organizationId)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = organizationId,
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setProfileId(context: Context, profileId: String) {
        if(model != null) {
            model!!.setProfileId(context, profileId)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setProfileId(context, profileId)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = profileId, dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setDataSource(context: Context, dataSource: String) {
        if(model != null) {
            model!!.setDataSource(context, dataSource)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setDataSource(context, dataSource)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = dataSource, requestTimeoutInSecond = 30,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setRequestTimeoutInSecond(context: Context, requestTimeoutInSecond: Int) {
        if(model != null) {
            model!!.setRequestTimeoutInSecond(context, requestTimeoutInSecond)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setRequestTimeoutInSecond(context, requestTimeoutInSecond)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = requestTimeoutInSecond,
                        maxGeofenceCount = 100)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun setMaxGeofenceCount(context: Context, maxGeofenceCount: Int) {
        if(model != null) {
            model!!.setMaxGeofenceCount(context, maxGeofenceCount)
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.setMaxGeofenceCount(context, maxGeofenceCount)
            } else {
                model = RelatedDigitalModel(isPushNotificationEnabled = false,
                        isInAppNotificationEnabled = false, isGeofenceEnabled = false,
                        googleAppAlias = "", huaweiAppAlias = "", organizationId = "",
                        profileId = "", dataSource = "", requestTimeoutInSecond = 30,
                        maxGeofenceCount = maxGeofenceCount)

                model?.saveToSharedPrefs(context)
            }
        }
    }

    @JvmStatic
    fun getIsPushNotificationEnabled(context: Context) : Boolean{
        return if(model!=null) {
            model!!.getIsPushNotificationEnabled()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIsPushNotificationEnabled()
            } else {
                Log.e(LOG_TAG, "isPushNotificationEnabled has never been set before!!")
                false
            }
        }
    }

    @JvmStatic
    fun getIsInAppNotificationEnabled(context: Context) : Boolean{
        return if(model!=null) {
            model!!.getIsInAppNotificationEnabled()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIsInAppNotificationEnabled()
            } else {
                Log.e(LOG_TAG, "isInAppNotificationEnabled has never been set before!!")
                false
            }
        }
    }

    @JvmStatic
    fun getIsGeofenceEnabled(context: Context) : Boolean{
        return if(model!=null) {
            model!!.getIsGeofenceEnabled()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getIsGeofenceEnabled()
            } else {
                Log.e(LOG_TAG, "isGeofenceEnabled has never been set before!!")
                false
            }
        }
    }

    @JvmStatic
    fun getGoogleAppAlias(context: Context) : String{
        return if(model!=null) {
            model!!.getGoogleAppAlias()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getGoogleAppAlias()
            } else {
                Log.e(LOG_TAG, "googleAppAlias has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getHuaweiAppAlias(context: Context) : String{
        return if(model!=null) {
            model!!.getHuaweiAppAlias()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getHuaweiAppAlias()
            } else {
                Log.e(LOG_TAG, "huaweiAppAlias has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getOrganizationId(context: Context) : String{
        return if(model!=null) {
            model!!.getOrganizationId()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getOrganizationId()
            } else {
                Log.e(LOG_TAG, "organizationId has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getProfileId(context: Context) : String{
        return if(model!=null) {
            model!!.getProfileId()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getProfileId()
            } else {
                Log.e(LOG_TAG, "profileId has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getDataSource(context: Context) : String{
        return if(model!=null) {
            model!!.getDataSource()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getDataSource()
            } else {
                Log.e(LOG_TAG, "dataSource has never been set before!!")
                ""
            }
        }
    }

    @JvmStatic
    fun getRequestTimeoutInSecond(context: Context) : Int{
        return if(model!=null) {
            model!!.getRequestTimeoutInSecond()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getRequestTimeoutInSecond()
            } else {
                Log.e(LOG_TAG, "requestTimeoutInSecond has never been set before!!")
                30
            }
        }
    }

    @JvmStatic
    fun getMaxGeofenceCount(context: Context) : Int{
        return if(model!=null) {
            model!!.getMaxGeofenceCount()
        } else {
            if(!SharedPref.readString(context, Constants.RELATED_DIGITAL_MODEL_KEY).isNullOrEmpty()) {
                model = Gson().fromJson(SharedPref.readString(context,
                        Constants.RELATED_DIGITAL_MODEL_KEY), RelatedDigitalModel::class.java)
                model!!.getMaxGeofenceCount()
            } else {
                Log.e(LOG_TAG, "maxGeofenceCount has never been set before!!")
                100
            }
        }
    }
}