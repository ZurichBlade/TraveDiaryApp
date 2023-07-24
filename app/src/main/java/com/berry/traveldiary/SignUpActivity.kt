package com.berry.traveldiary

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityMainBinding
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.User
import com.berry.traveldiary.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var myDatabase: MyDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        myDatabase = MyDatabase.getDatabase(this@SignUpActivity)


        binding.addBtn.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            checkAndCreateUser()
        }

    }

    private fun checkAndCreateUser() {
        val userName = binding.edtUserName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (inputCheck(userName, email, password)) {
            CoroutineScope(Dispatchers.IO).launch {
                if (!myDatabase.userDao().isRecordExists(userName, email)) {
                    // data not exist.
                    insertDataToDatabase(userName, email, password)
                    Log.d("TAG", "data not exist")
                } else {
                    // data already exist.
                    Log.d("TAG", "data  exist")
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "User Already exist.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }


    }

    private suspend fun insertDataToDatabase(userName: String, email: String, password: String) {

        // Create User Object
        val user = User(0, userName, email, password)
        myDatabase.userDao().addUser(user)

        runOnUiThread {
            Toast.makeText(this@SignUpActivity, "User created Successfully.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun inputCheck(username: String, email: String, password: String): Boolean {
        return !(TextUtils.isEmpty(username) && TextUtils.isEmpty(email) && TextUtils.isEmpty(
            password
        ))
    }
}