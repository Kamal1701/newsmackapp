package com.example.newsmackapp.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.newsmackapp.controller.App
import com.example.newsmackapp.utilities.BASE_URL
import com.example.newsmackapp.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.newsmackapp.utilities.URL_CREATE_USER
import com.example.newsmackapp.utilities.URL_GET_USER
import com.example.newsmackapp.utilities.URL_LOGIN
import com.example.newsmackapp.utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

//    var isLoggedIn = false
//    var userEmail = ""
//    var authToken = ""

    fun registerUser(
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {


        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest =
            object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
                complete(true)

            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not register user:$error")
                Log.d("ERROR", "register user URL:$URL_REGISTER")
                complete(false)

            }) {
                override fun getBodyContentType(): String {
                    return "application/json: charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }

        App.sharedPrefs.requestQueue.add(registerRequest)
    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
            Response.Listener { response ->
                try {
                    App.sharedPrefs.userEmail = response.getString("user")
                    App.sharedPrefs.authToken = response.getString("token")
                    App.sharedPrefs.isLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: " + e.localizedMessage)
                }

            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not register user:$error")

            }) {
            override fun getBodyContentType(): String {
                return "application/json: charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.sharedPrefs.requestQueue.add(loginRequest)
    }

    fun createUser(

        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null,
            Response.Listener { response ->

                try {

                    UserDataServices.name = response.getString("name")
                    UserDataServices.email = response.getString("email")
                    UserDataServices.avatarName = response.getString("avatarName")
                    UserDataServices.avatarColor = response.getString("avatarColor")
                    UserDataServices.id = response.getString("_id")
                    complete(true)

                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: " + e.localizedMessage)
                    complete(false)
                }

            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not add user:$error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json: charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPrefs.authToken}")
                return headers
            }

        }
        App.sharedPrefs.requestQueue.add(createRequest)

    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {

        val findUserRequest = object :
            JsonObjectRequest(Method.GET, "$URL_GET_USER${App.sharedPrefs.userEmail}", null,
                Response.Listener { response ->
                    try {
                        UserDataServices.name = response.getString("name")
                        UserDataServices.email = response.getString("email")
                        UserDataServices.avatarName = response.getString("avatarName")
                        UserDataServices.avatarColor = response.getString("avatarColor")
                        UserDataServices.id = response.getString("_id")

                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", "EXC:" + e.localizedMessage)
                    }
                }, Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not find user")
                    complete(false)

                }) {
            override fun getBodyContentType(): String {
                return "application/json: charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPrefs.authToken}")
                return headers
            }
        }
        App.sharedPrefs.requestQueue.add(findUserRequest)
    }

}