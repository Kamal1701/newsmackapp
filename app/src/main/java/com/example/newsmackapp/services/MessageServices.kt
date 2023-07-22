package com.example.newsmackapp.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.newsmackapp.model.Channel
import com.example.newsmackapp.utilities.URL_GET_CHANNELS

object MessageServices {

    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {
        val channelRequest = object :
            JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
                try {
                    for (x in 0 until response.length()) {
                        val channel = response.getJSONObject(x)
                        val name = channel.getString("name")
                        val chanDesc = channel.getString("description")
                        val channelId = channel.getString("_id")

                        val newChannel = Channel(name, chanDesc, channelId)
                        this.channels.add(newChannel)
                    }
                    complete(true)

                } catch (e: Exception) {
                    Log.d("JSON", "EXC:" + e.localizedMessage)
                    complete(false)
                }

            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not retrieve the channels")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset = utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header.put("Authorization", "Bearer ${AuthService.authToken}")
                return header
            }
        }
        Volley.newRequestQueue(context).add(channelRequest)
    }

}