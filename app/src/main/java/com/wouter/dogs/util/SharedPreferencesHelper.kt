package com.wouter.dogs.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class SharedPreferencesHelper {

    companion object {

        private const val REF_TIME = "Pref_time"

        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance : SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) : SharedPreferencesHelper = instance ?: synchronized(LOCK) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context) : SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }

    fun saveUpdateTime(time : Long) {
        prefs?.edit(commit = true) {
            putLong(REF_TIME, time)
        }
    }

    fun getUpdateTime() = prefs?.getLong(REF_TIME, 0)


}