package com.berry.traveldiary.uitility

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

object CommonUtils {


    const val PREFERENCES: String = "MySharedPref"
    const val NO_IMAGE: String = "no_image"


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

    fun showToast(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

}