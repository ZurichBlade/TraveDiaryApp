package com.berry.traveldiary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityLoginBinding
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.User
import com.berry.traveldiary.uitility.CommonUtils.PREF_LOGIN
import com.berry.traveldiary.viewmodel.UserViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var myDatabase: MyDatabase
    private val PREFERENCES: String = "MySharedPref"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myDatabase = MyDatabase.getDatabase(this@LoginActivity)


        val loginData = getStringPref(PREF_LOGIN, this)
        Log.d("TAG", "loginData>>$loginData")
        if (!TextUtils.isEmpty(loginData)) {
            val user: User = Gson().fromJson(loginData, User::class.java)
            Log.d("TAG", "User>>$user")

            //redirect to dashboard
            val intent = Intent(this, DrawerActivity::class.java)
            startActivity(intent)
            finish()

        }


        binding.btnLogIn.setOnClickListener {

            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }

            val userName = binding.edtUserName.text.toString()
            val password = binding.edtPassword.text.toString()
            if (inputCheck(userName, password)) {
                callLogin(userName, password)
            } else {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnSignUp.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


    }


    fun setStringPref(key: String, value: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString(key, value)
        myEdit.apply()
    }

    fun getStringPref(key: String, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }


    private fun callLogin(userName: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
//            myDatabase.diaryEntryDao().addDiaryEntry(DiaryEntries(0,"title","12/10/1999","daman","description"))


            if (myDatabase.userDao().isUserExists(userName, password)) {

                //get user data.
                val userData = myDatabase.userDao().getUser(userName)
                Log.d("TAG", ">>" + userData.email)
                setStringPref(PREF_LOGIN, Gson().toJson(userData), this@LoginActivity)
                runOnUiThread {
                    val intent = Intent(this@LoginActivity, DrawerActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } else {
                Log.d("TAG", "user not  exist")
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid Username or password.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }
    }

    private fun inputCheck(username: String, password: String): Boolean {
        return !(TextUtils.isEmpty(username) && TextUtils.isEmpty(password))
    }

}