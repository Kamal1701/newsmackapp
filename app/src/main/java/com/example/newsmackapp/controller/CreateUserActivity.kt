package com.example.newsmackapp.controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.newsmackapp.databinding.ActivityCreateUserBinding
import com.example.newsmackapp.services.AuthService
import com.example.newsmackapp.services.UserDataServices
import java.util.Random

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun onCreateCreateUserBtnClicked(view: View) {
        val userName = binding.createUserNameTxt.text.toString()
        val userEmail = binding.createEmailTxt.text.toString()
        val userPassword = binding.createUserPasswordTxt.text.toString()
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
                                println(UserDataServices.avatarName)
                                println(UserDataServices.avatarColor)
                                println(UserDataServices.name)
                                println(UserDataServices.email)
                                finish()

                            }
                        }
                    }
                }
            }
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
}