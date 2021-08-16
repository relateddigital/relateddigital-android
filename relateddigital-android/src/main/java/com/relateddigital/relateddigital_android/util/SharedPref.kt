package com.relateddigital.relateddigital_android.util

import android.content.Context

class SharedPref {
    companion object {
        fun writeString(context: Context, key: String, value: String) {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val spEditor = sp.edit()
            spEditor.putString(key, value)
            spEditor.apply()
        }

        fun writeInt(context: Context, key: String, value: Int) {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val spEditor = sp.edit()
            spEditor.putInt(key, value)
            spEditor.apply()
        }

        fun writeBoolean(context: Context, key: String, value: Boolean) {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val spEditor = sp.edit()
            spEditor.putBoolean(key, value)
            spEditor.apply()
        }

        fun writeLong(context: Context, key: String, value: Long) {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val spEditor = sp.edit()
            spEditor.putLong(key, value)
            spEditor.apply()
        }

        fun readString(context: Context, key: String, defaultValue: String = ""): String {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            return sp.getString(key, defaultValue)!!
        }

        fun readInt(context: Context, key: String, defaultValue: Int = 0): Int {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            return sp.getInt(key, defaultValue)
        }

        fun readBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            return sp.getBoolean(key, defaultValue)
        }

        fun readLong(context: Context, key: String, defaultValue: Long = 0): Long {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            return sp.getLong(key, defaultValue)
        }

        fun clearKey(context: Context, key: String) {
            val appName = context.packageName
            val sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val spEditor = sp.edit()
            spEditor.remove(key)
            spEditor.apply()
        }
    }
}