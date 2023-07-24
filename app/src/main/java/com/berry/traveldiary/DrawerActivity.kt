package com.berry.traveldiary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.berry.traveldiary.databinding.ActivityDrawerBinding
import com.berry.traveldiary.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson


class DrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding
    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)


        /* binding.appBarDrawer.fab.setOnClickListener { view ->
             Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()
         }*/
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val headerLayout: View = navView.getHeaderView(0)


        val loginData = getStringPref("login_info", this)
        Log.d("TAG", "loginData>>$loginData")
        if (!TextUtils.isEmpty(loginData)) {
            userData = Gson().fromJson(loginData, User::class.java)
            headerLayout.findViewById<TextView>(R.id.tvName).text = userData.username
            headerLayout.findViewById<TextView>(R.id.tvEmail).text = userData.email
        }


        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.str_logout))
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    callLogout()
                }
                .show()
        }


    }

    private fun callLogout() {
        val preferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        //redirect to login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun setStringPref(key: String, value: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString(key, value)
        myEdit.apply()
    }

    fun getStringPref(key: String, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }


}