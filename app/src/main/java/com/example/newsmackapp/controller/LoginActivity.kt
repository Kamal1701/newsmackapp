package com.example.newsmackapp.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.newsmackapp.R
import com.example.newsmackapp.databinding.ActivityLoginBinding
import com.example.newsmackapp.services.AuthService

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun onLoginLoginBtnClicked(view: View) {
        val email = binding.loginEmailTxt.toString()
        val password = binding.loginPasswordTxt.toString()
        AuthService.loginUser(this, email, password){loginSuccess ->
            if(loginSuccess){
                AuthService.findUserByEmail(this){findSuccess ->
                    if(findSuccess){

                    }

                }
            }

        }
    }

    fun onLoginCreateUserBtnClicked(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }
}