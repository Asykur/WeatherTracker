package com.project.common.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {
    private val editor: SharedPreferences.Editor
        get() = preferences.edit()
    private val preferences = context.getSharedPreferences("WeatherTracker", Context.MODE_PRIVATE)

    fun setString(key: String, value: String) = editor.putString(key, value).apply()
    fun getString(key: String): String = preferences.getString(key, "") ?: ""
}