package com.relateddigital.relateddigital_android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import com.relateddigital.relateddigital_android.constants.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.util.Log

/**
 * Jetpack DataStore işlemlerini yöneten merkezi sınıf.
 * İki ayrı dosyayı yönetir: 'push_payloads' ve 'notification_settings'.
 */
object DataStoreManager {
    private const val LOG_TAG = "DataStoreManager"

    private val Context.payloadsDataStore: DataStore<Preferences>
        get() = CustomDataStoreFactory.create(this, name = "push_payloads")

    private val Context.settingsDataStore: DataStore<Preferences>
        get() = CustomDataStoreFactory.create(this, name = "notification_settings")


    /**
     * DataStore'da kullanılacak tüm anahtarları merkezi bir yerde topluyoruz.
     */
    object Keys {
        // Payload Anahtarları
        val PAYLOADS = stringPreferencesKey(Constants.PAYLOAD_SP_KEY)
        val PAYLOADS_BY_ID = stringPreferencesKey(Constants.PAYLOAD_SP_ID_KEY)
        val LOGIN_ID = stringPreferencesKey(Constants.NOTIFICATION_LOGIN_ID_KEY)

        // Ayar Anahtarları
        val RELATED_DIGITAL_MODEL = stringPreferencesKey(Constants.RELATED_DIGITAL_MODEL_KEY)
        val NOTIFICATION_SMALL_ICON = intPreferencesKey(Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON)
        val NOTIFICATION_SMALL_ICON_DARK_MODE = intPreferencesKey(Constants.NOTIFICATION_TRANSPARENT_SMALL_ICON_DARK_MODE)
        val NOTIFICATION_USE_LARGE_ICON = booleanPreferencesKey(Constants.NOTIFICATION_USE_LARGE_ICON)
        val NOTIFICATION_LARGE_ICON = intPreferencesKey(Constants.NOTIFICATION_LARGE_ICON)
        val NOTIFICATION_LARGE_ICON_DARK_MODE = intPreferencesKey(Constants.NOTIFICATION_LARGE_ICON_DARK_MODE)
        val NOTIFICATION_INTENT = stringPreferencesKey(Constants.INTENT_NAME)
        val NOTIFICATION_CHANNEL_NAME = stringPreferencesKey(Constants.CHANNEL_NAME)
        val NOTIFICATION_COLOR = stringPreferencesKey(Constants.NOTIFICATION_COLOR)
        val NOTIFICATION_CHANNEL_ID = stringPreferencesKey(Constants.NOTIFICATION_CHANNEL_ID_KEY)
        val NOTIFICATION_PRIORITY = stringPreferencesKey(Constants.NOTIFICATION_PRIORITY_KEY)
    }

    // --- Push Payload Yönetimi ---

    fun getPayloadsFlow(context: Context): Flow<String> {
        Log.d(LOG_TAG, "getPayloadsFlow çağrıldı. Okunacak anahtar: ${Keys.PAYLOADS.name}")
        return context.payloadsDataStore.data
            .map { preferences ->
                val payload = preferences[Keys.PAYLOADS] ?: ""
                Log.d(LOG_TAG, "getPayloadsFlow - Akıştan gelen payload verisi: ${payload.length} karakter.")
                payload
            }
    }

    /**
     * Ana payload listesini atomik olarak günceller.
     */
    suspend fun updatePayloads(context: Context, updateAction: (currentPayload: String) -> String) {
        Log.d(LOG_TAG, "updatePayloads fonksiyonu çağrıldı. Güncellenecek anahtar: ${Keys.PAYLOADS.name}")
        context.payloadsDataStore.edit { settings ->
            val currentPayload = settings[Keys.PAYLOADS] ?: ""
            Log.d(LOG_TAG, "updatePayloads - Mevcut payload okundu. Boyut: ${currentPayload.length}")
            val newPayload = updateAction(currentPayload)
            Log.d(LOG_TAG, "updatePayloads - Yeni payload oluşturuldu. Boyut: ${newPayload.length}")
            settings[Keys.PAYLOADS] = newPayload
            Log.d(LOG_TAG, "updatePayloads - Yeni payload DataStore'a yazıldı.")
        }
    }

    /**
     * Kullanıcı ID'sine özel payload listesini atomik olarak günceller.
     */
    suspend fun updatePayloadsById(context: Context, updateAction: (currentPayload: String) -> String) {
        Log.d(LOG_TAG, "updatePayloadsById fonksiyonu çağrıldı. Güncellenecek anahtar: ${Keys.PAYLOADS_BY_ID.name}")
        context.payloadsDataStore.edit { settings ->
            val currentPayload = settings[Keys.PAYLOADS_BY_ID] ?: ""
            Log.d(LOG_TAG, "updatePayloadsById - Mevcut payload okundu. Boyut: ${currentPayload.length}")
            val newPayload = updateAction(currentPayload)
            Log.d(LOG_TAG, "updatePayloadsById - Yeni payload oluşturuldu. Boyut: ${newPayload.length}")
            settings[Keys.PAYLOADS_BY_ID] = newPayload
            Log.d(LOG_TAG, "updatePayloadsById - Yeni payload DataStore'a yazıldı.")
        }
    }

    // ... Diğer payload okuma/yazma fonksiyonları (getPayloads, getLoginId vb.)
    // Not: Bu fonksiyonlar artık payloadsDataStore kullanmalı.
    suspend fun getPayloads(context: Context): String {
        Log.d(LOG_TAG, "getPayloads çağrıldı. Okunacak anahtar: ${Keys.PAYLOADS.name}")
        return context.payloadsDataStore.data.map { it[Keys.PAYLOADS] ?: "" }.first().also {
            Log.d(LOG_TAG, "getPayloads - Okunan payload verisi: ${it.length} karakter.")
        }
    }

    suspend fun getPayloadsById(context: Context): String {
        Log.d(LOG_TAG, "getPayloadsById çağrıldı. Okunacak anahtar: ${Keys.PAYLOADS_BY_ID.name}")
        return context.payloadsDataStore.data.map { it[Keys.PAYLOADS_BY_ID] ?: "" }.first().also {
            Log.d(LOG_TAG, "getPayloadsById - Okunan payload verisi: ${it.length} karakter.")
        }
    }

    suspend fun getLoginId(context: Context): String {
        Log.d(LOG_TAG, "getLoginId çağrıldı. Okunacak anahtar: ${Keys.LOGIN_ID.name}")
        return context.payloadsDataStore.data.map { it[Keys.LOGIN_ID] ?: "" }.first().also {
            Log.d(LOG_TAG, "getLoginId - Okunan login ID verisi: ${it.length} karakter.")
        }
    }

    suspend fun saveLoginId(context: Context, loginId: String) {
        Log.d(LOG_TAG, "saveLoginId çağrıldı. Kaydedilecek anahtar: ${Keys.LOGIN_ID.name}, değer: $loginId")
        context.payloadsDataStore.edit { it[Keys.LOGIN_ID] = loginId }
    }


    // --- Bildirim Ayarları Yönetimi ---

    /**
     * Verilen tüm bildirim ayarlarını 'notification_settings' dosyasına tek bir atomik işlemde kaydeder.
     */
    suspend fun saveNotificationPreferences(
        context: Context,
        modelJson: String,
        smallIcon: Int?,
        smallIconDarkMode: Int?,
        useLargeIcon: Boolean,
        largeIcon: Int?,
        largeIconDarkMode: Int?,
        pushIntent: String?,
        channelName: String?,
        color: String?,
        priority: String
    ) {
        Log.d(LOG_TAG, "saveNotificationPreferences çağrıldı.")
        context.settingsDataStore.edit { settings ->
            settings[Keys.RELATED_DIGITAL_MODEL] = modelJson
            Log.d(LOG_TAG, "Ayarlar kaydediliyor...")
            smallIcon?.let { settings[Keys.NOTIFICATION_SMALL_ICON] = it }
            smallIconDarkMode?.let { settings[Keys.NOTIFICATION_SMALL_ICON_DARK_MODE] = it }
            settings[Keys.NOTIFICATION_USE_LARGE_ICON] = useLargeIcon
            largeIcon?.let { settings[Keys.NOTIFICATION_LARGE_ICON] = it }
            largeIconDarkMode?.let { settings[Keys.NOTIFICATION_LARGE_ICON_DARK_MODE] = it }
            pushIntent?.let { settings[Keys.NOTIFICATION_INTENT] = it }
            channelName?.let { settings[Keys.NOTIFICATION_CHANNEL_NAME] = it }
            color?.let { settings[Keys.NOTIFICATION_COLOR] = it }
            settings[Keys.NOTIFICATION_PRIORITY] = priority
            Log.d(LOG_TAG, "Bildirim ayarları başarıyla kaydedildi.")
        }
    }

    /**
     * Ayarlar dosyasından belirtilen anahtara ait string veriyi okur.
     */
    suspend fun readStringFromSettings(context: Context, key: Preferences.Key<String>): String {
        Log.d(LOG_TAG, "readStringFromSettings çağrıldı. Okunacak anahtar: ${key.name}")
        return context.settingsDataStore.data.map { it[key] ?: "" }.first().also {
            Log.d(LOG_TAG, "readStringFromSettings - Okunan veri: ${it.length} karakter.")
        }
    }

    /**
     * Ayarlar dosyasına belirtilen anahtara ait string veriyi yazar.
     */
    suspend fun writeStringToSettings(context: Context, key: Preferences.Key<String>, value: String) {
        Log.d(LOG_TAG, "writeStringToSettings çağrıldı. Kaydedilecek anahtar: ${key.name}, değer: $value")
        context.settingsDataStore.edit { it[key] = value }
    }
}