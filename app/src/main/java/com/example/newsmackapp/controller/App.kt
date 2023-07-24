package com.example.newsmackapp.controller

import android.app.Application
import com.example.newsmackapp.utilities.SharedPrefs

class App : Application() {

    companion object{
        lateinit var sharedPrefs: SharedPrefs
    }

    override fun onCreate() {
        sharedPrefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}