package com.example.newsmackapp.controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.newsmackapp.databinding.ActivityLoginBinding
import com.example.newsmackapp.services.AuthService

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        binding.loginSpinner.visibility = View.INVISIBLE
        setContentView(view)
    }

    fun onLoginLoginBtnClicked(view: View) {
        enableSpinner(true)
        val email = binding.loginEmailTxt.toString()
        val password = binding.loginPasswordTxt.toString()
        hideKeyboard()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(this, email, password){loginSuccess ->
                if(loginSuccess){
                    AuthService.findUserByEmail(this){findSuccess ->
                        if(findSuccess){
                            enableSpinner(false)
                            finish()
                        } else{
                            errorToast()
                        }
                    }
                } else{
                    errorToast()
                }
            }
        } else{
            Toast.makeText(this, "Please fill both email and password",Toast.LENGTH_LONG).show()
        }
    }

    fun onLoginCreateUserBtnClicked(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable:Boolean){
        if(enable){
            binding.loginSpinner.visibility = View.VISIBLE
        } else {
            binding.loginSpinner.visibility = View.INVISIBLE

        }
        binding.loginEmailTxt.isEnabled = !enable
        binding.loginPasswordTxt.isEnabled = !enable
        binding.loginLoginBtn.isEnabled = !enable
        binding.loginCreateUserBtn.isEnabled = !enable
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}