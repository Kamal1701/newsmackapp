package com.example.newsmackapp.services

import android.graphics.Color
import com.example.newsmackapp.controller.App
import java.util.Scanner

object UserDataServices {

    var id=""
    var avatarColor =""
    var avatarName = ""
    var email = ""
    var name = ""

    fun returnAvatarColor(component : String) : Int {
        val strippedColor = component
            .replace("[","")
            .replace("]","")
            .replace(",","")
        var r = 0
        var g = 0
        var b = 0
        val scanner = Scanner(strippedColor)
        if(scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r,g,b)
    }

    fun logout(){
        id=""
        avatarColor =""
        avatarName = ""
        email = ""
        name = ""
        App.sharedPrefs.userEmail = ""
        App.sharedPrefs.isLoggedIn = false
        App.sharedPrefs.authToken = ""
    }
}