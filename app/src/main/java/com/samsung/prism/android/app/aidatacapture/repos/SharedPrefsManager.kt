package com.samsung.prism.android.app.aidatacapture.repos

import android.content.Context
import android.content.SharedPreferences
import com.samsung.prism.android.app.aidatacapture.constants.Constants

class SharedPrefsManager(private val context: Context) {
    private var SharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREP_NAME, Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = SharedPref.edit()
    fun saveLongValue(key: String?, value: Long): Boolean {
        editor.putLong(key, value).apply()
        return true
    }

    fun getLongValue(key: String?, defaultValue: Long): Long {
        return SharedPref.getLong(key, defaultValue)
    }

    fun saveStringValue(key: String?, value: String?): Boolean {
        editor.putString(key, value).apply()
        return true
    }

    fun saveBoolValue(key: String?, value: Boolean?): Boolean {
        editor.putBoolean(key, value!!).apply()
        return true
    }

    fun saveIntValue(key: String?, value: Int): Boolean {
        editor.putInt(key, value).apply()
        return true
    }

    fun getStringValue(key: String?, defaultValue: String?): String? {
        return SharedPref.getString(key, defaultValue)
    }

    fun getBoolValue(key: String?, defaultValue: Boolean): Boolean {
        return SharedPref.getBoolean(key, defaultValue)
    }

    fun getIntValue(key: String?, defaultValue: Int): Int {
        return SharedPref.getInt(key, defaultValue)
    }

    fun isUserSignedIn(key: String?): Boolean {
        return SharedPref.getBoolean(key, false)
    }

}