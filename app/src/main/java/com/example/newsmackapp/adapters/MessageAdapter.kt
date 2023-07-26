package com.example.newsmackapp.adapters

import android.content.Context
import android.service.autofill.UserData
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsmackapp.databinding.MessageListViewBinding
import com.example.newsmackapp.model.Message
import com.example.newsmackapp.services.UserDataServices
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MessageListViewBinding) : RecyclerView.ViewHolder(binding.root){
        val userImage = binding.messageUserImg
        val timeStamp = binding.messageTimestamp
        val userName = binding.messageUserName
        val messageBody = binding.messageBodyText

        fun bindMessage(context: Context, message: Message){
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataServices.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = message.timeStamp
            messageBody?.text = message.message

        }

        fun returnDateString(isoString : String) : String {
            val isoFormatter = SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try{
                convertedDate = isoFormatter.parse(isoString)

            } catch (e: ParseException){
                Log.d("PARSE", "Cannot parse date")
            }
            val outDateString = SimpleDateFormat("E, h:mm a", Locale.getDefault())
            return outDateString.format(convertedDate)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?. bindMessage(context, messages[position])
    }


}