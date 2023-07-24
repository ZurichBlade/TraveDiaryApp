package com.berry.traveldiary.uitility

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class CommonUtils {


    val PREFERENCES: String = "MySharedPref"

    fun setStringPref(key: String, value: String, context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString(key, value)
        myEdit.apply()
    }

    fun getStringPref(key: String, context: Context): String? {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")

    }

}