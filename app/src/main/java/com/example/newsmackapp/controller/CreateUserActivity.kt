package com.example.newsmackapp.controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.newsmackapp.databinding.ActivityCreateUserBinding
import com.example.newsmackapp.services.AuthService
import com.example.newsmackapp.utilities.BROADCAST_USER_DATA_CHANGE
import java.util.Random

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        val view = binding.root
        binding.createSpinner.visibility = View.INVISIBLE
        setContentView(view)
    }

    fun onCreateCreateUserBtnClicked(view: View) {
        enableSpinner(true)
        val userName = binding.createUserNameTxt.text.toString()
        val userEmail = binding.createEmailTxt.text.toString()
        val userPassword = binding.createUserPasswordTxt.text.toString()
        hideKeyboard()
        if(userName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()){
            AuthService.registerUser(this, userEmail, userPassword) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, userEmail, userPassword) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(
                                this,
                                userName,
                                userEmail,
                                userAvatar,
                                avatarColor,
                            ) { createSuccess ->
                                if (createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                } else{
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else{
            Toast.makeText(this, "Make sure username, email and password are filled", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }


    }

    fun onCreateUserAvatarImageviewClicked(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }
        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        binding.createUserAvatarImageview.setImageResource(resourceId)
    }

    fun onCreateGenerateBackgroundColorBtnClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)
        binding.createUserAvatarImageview.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = r.toDouble() / 255
        val savedB = r.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"
        println(avatarColor)
    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable:Boolean){
        if(enable){
            binding.createSpinner.visibility = View.VISIBLE
        } else {
            binding.createSpinner.visibility = View.INVISIBLE

        }
        binding.createCreateUserBtn.isEnabled = !enable
        binding.createUserAvatarImageview.isEnabled = !enable
        binding.createGenerateBackgroundBtn.isEnabled = !enable
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}