package com.relateddigital.relateddigital_android.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.constants.Constants

/**
 * Keeps the notification icons configured by the host app resolvable across app updates.
 *
 * The host app configures its icons with `R.drawable.*` ids, but those ids are assigned per build:
 * an id persisted by one APK can point at an unrelated resource - or at no resource at all - after
 * the app is updated or restored from a backup. The persisted value is only refreshed when
 * setIsPushNotificationEnabled() runs again, which is not guaranteed to have happened when a push
 * arrives right after an update, so a stale id used to reach setSmallIcon() and made the system
 * render its own default icon. Persisting "drawable/ic_push" next to the id survives rebuilds; the
 * numeric id is still read afterwards so installs written by older SDK versions keep working.
 */
object NotificationIconResolver {
    private const val LOG_TAG = "RDNotificationIcon"

    private val ICON_RESOURCE_TYPES = setOf("drawable", "mipmap")

    /**
     * Stores [resId] both by name and by id. Returns false when the resource cannot be used as a
     * notification icon at all, in which case nothing is written.
     */
    @JvmStatic
    fun persistIcon(context: Context, resId: Int, idKey: String, nameKey: String): Boolean {
        if (!isUsableIcon(context, resId)) {
            return false
        }
        SharedPref.writeInt(context, idKey, resId)
        val resourceKey = resourceKeyOf(context, resId)
        if (resourceKey != null) {
            SharedPref.writeString(context, nameKey, resourceKey)
        } else {
            // A stored name that no longer belongs to the stored id would outlive it, so drop it.
            SharedPref.clearKey(context, nameKey)
        }
        return true
    }

    /**
     * Returns the configured small icon, or 0 when the host app never configured a usable one.
     * The dark mode variant is optional and falls back to the light one.
     */
    @JvmStatic
    fun resolveSmallIcon(context: Context, isDarkMode: Boolean): Int {
        if (isDarkMode) {
            val darkModeIcon = resolveConfiguredIcon(
                context,
                Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON_DARK_MODE,
                Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON_DARK_MODE_NAME_KEY,
                "small icon (dark mode)"
            )
            if (darkModeIcon != 0) {
                return darkModeIcon
            }
        }
        return resolveConfiguredIcon(
            context,
            Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON,
            Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON_NAME_KEY,
            "small icon"
        )
    }

    /**
     * Small icon to hand to the builder. Never returns 0: a notification posted with an invalid
     * small icon is dropped by the system, so the launcher icon and finally an SDK icon are used.
     */
    @JvmStatic
    fun resolveSmallIconWithFallback(context: Context, isDarkMode: Boolean): Int {
        val configuredIcon = resolveSmallIcon(context, isDarkMode)
        if (configuredIcon != 0) {
            return configuredIcon
        }

        val appIcon = ImageUtils.getAppIcon(context)
        if (isUsableIcon(context, appIcon)) {
            Log.w(
                LOG_TAG,
                "No usable notification small icon is configured, falling back to the launcher " +
                        "icon. The launcher icon is rendered as a flat silhouette in the status " +
                        "bar, so pass a transparent monochrome drawable as notificationSmallIcon " +
                        "to RelatedDigital.setIsPushNotificationEnabled()."
            )
            return appIcon
        }

        Log.e(
            LOG_TAG,
            "Neither a configured small icon nor the launcher icon could be resolved, using the " +
                    "SDK icon so that the notification is still posted."
        )
        return R.drawable.ic_carousel_icon
    }

    /**
     * Returns the configured large icon as a bitmap, or the launcher icon when none is configured.
     * Vector and adaptive icons are rasterised here because BitmapFactory cannot decode them.
     */
    @JvmStatic
    fun resolveLargeIconBitmap(context: Context, isDarkMode: Boolean): Bitmap? {
        var largeIcon = 0
        if (isDarkMode) {
            largeIcon = resolveConfiguredIcon(
                context,
                Constants.NOTIFICATION_LARGE_ICON_DARK_MODE,
                Constants.NOTIFICATION_LARGE_ICON_DARK_MODE_NAME_KEY,
                "large icon (dark mode)"
            )
        }
        if (largeIcon == 0) {
            largeIcon = resolveConfiguredIcon(
                context,
                Constants.NOTIFICATION_LARGE_ICON,
                Constants.NOTIFICATION_LARGE_ICON_NAME_KEY,
                "large icon"
            )
        }
        if (largeIcon == 0) {
            largeIcon = ImageUtils.getAppIcon(context)
        }
        return toBitmap(context, largeIcon)
    }

    /**
     * A resource is only usable as a notification icon when it is an image the system can load;
     * checking that the id merely exists lets a stale id through, because after a rebuild it
     * usually still resolves - to a layout, a colour or a different drawable.
     */
    @JvmStatic
    fun isUsableIcon(context: Context?, resId: Int): Boolean {
        if (context == null || resId == 0) {
            return false
        }
        return try {
            val resourceType = context.resources.getResourceTypeName(resId)
            if (!ICON_RESOURCE_TYPES.contains(resourceType)) {
                Log.w(
                    LOG_TAG,
                    "Resource $resId is a '$resourceType', not a drawable, so it cannot be used " +
                            "as a notification icon."
                )
                false
            } else {
                ResourcesCompat.getDrawable(context.resources, resId, null) != null
            }
        } catch (e: Exception) {
            Log.w(LOG_TAG, "Resource $resId could not be loaded as a notification icon : ${e.message}")
            false
        }
    }

    private fun resolveConfiguredIcon(
        context: Context,
        idKey: String,
        nameKey: String,
        label: String
    ): Int {
        val resourceKey = SharedPref.readString(context, nameKey)
        if (resourceKey.isNotEmpty()) {
            val resIdByName = resolveResourceKey(context, resourceKey)
            if (resIdByName != 0) {
                return resIdByName
            }
            Log.w(
                LOG_TAG,
                "The configured $label '$resourceKey' no longer exists in this build. Pass it " +
                        "again through RelatedDigital.setIsPushNotificationEnabled() if it was renamed."
            )
        }

        // Written by SDK versions that only persisted the numeric id, and by this version as a
        // second source. It is validated but cannot be trusted to still mean the same drawable.
        val storedId = SharedPref.readInt(context, idKey)
        if (storedId != 0 && isUsableIcon(context, storedId)) {
            return storedId
        }
        if (storedId != 0) {
            Log.w(
                LOG_TAG,
                "The stored $label id $storedId is not a usable icon in this build. This happens " +
                        "when the app was updated after the id was persisted."
            )
        }
        return 0
    }

    private fun resourceKeyOf(context: Context, resId: Int): String? {
        return try {
            val resourceType = context.resources.getResourceTypeName(resId)
            val resourceEntry = context.resources.getResourceEntryName(resId)
            "$resourceType/$resourceEntry"
        } catch (e: Exception) {
            Log.w(LOG_TAG, "The name of resource $resId could not be read : ${e.message}")
            null
        }
    }

    private fun resolveResourceKey(context: Context, resourceKey: String): Int {
        val separatorIndex = resourceKey.indexOf('/')
        if (separatorIndex <= 0 || separatorIndex == resourceKey.length - 1) {
            return 0
        }
        val resourceType = resourceKey.substring(0, separatorIndex)
        val resourceEntry = resourceKey.substring(separatorIndex + 1)
        val resId = context.resources.getIdentifier(resourceEntry, resourceType, context.packageName)
        return if (isUsableIcon(context, resId)) resId else 0
    }

    private fun toBitmap(context: Context, resId: Int): Bitmap? {
        if (!isUsableIcon(context, resId)) {
            return null
        }
        return try {
            ResourcesCompat.getDrawable(context.resources, resId, null)?.toBitmap()
        } catch (e: Exception) {
            Log.w(LOG_TAG, "Resource $resId could not be converted to a bitmap : ${e.message}")
            null
        }
    }
}
