package com.relateddigital.relateddigital_android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object CustomDataStoreFactory {

    private val instances = ConcurrentHashMap<String, DataStore<Preferences>>()

    fun create(context: Context, name: String): DataStore<Preferences> {
        return instances.getOrPut(name) {
            PreferenceDataStoreFactory.create {
                File(context.filesDir, "datastore/$name.preferences_pb")
            }
        }
    }
}