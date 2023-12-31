package com.example.newsmackapp.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentContainerView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsmackapp.R
import com.example.newsmackapp.adapters.MessageAdapter
import com.example.newsmackapp.databinding.ActivityMainBinding
import com.example.newsmackapp.model.Channel
import com.example.newsmackapp.model.Message
import com.example.newsmackapp.services.AuthService
import com.example.newsmackapp.services.MessageServices
import com.example.newsmackapp.services.UserDataServices
import com.example.newsmackapp.utilities.BROADCAST_USER_DATA_CHANGE
import com.example.newsmackapp.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
//    private lateinit var contentMainBinding: ContentMainBinding
    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel: Channel? = null
    lateinit var recyclerView : RecyclerView
    lateinit var mainChannelNameTextView : TextView
    lateinit var messageTextEditField : EditText

    private fun setupAdapters() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageServices.channels)
        binding.channelList.adapter = channelAdapter
        messageAdapter = MessageAdapter(this, MessageServices.messages)
        recyclerView.adapter = messageAdapter
//        contentMainBinding.messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
//        contentMainBinding.messageListView.layoutManager = layoutManager
        recyclerView.layoutManager = layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navigationView =  findViewById<FragmentContainerView>(R.id.nav_host_fragment_content_main)
        val view = navigationView.rootView
        recyclerView = view.findViewById(R.id.messageListView)
        mainChannelNameTextView = view.findViewById(R.id.mainChannelName)
        messageTextEditField = view.findViewById(R.id.messageTextField)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )
        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)
        setupAdapters()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )

        binding.channelList.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageServices.channels[i]
            drawerLayout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.sharedPrefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.sharedPrefs.isLoggedIn) {
                binding.andriodDrawerNavInclude.userNameNavHeader.text = UserDataServices.name
                binding.andriodDrawerNavInclude.userEmailNavHeader.text = UserDataServices.email
                val resourceId =
                    resources.getIdentifier(UserDataServices.avatarName, "drawable", packageName)
                binding.andriodDrawerNavInclude.userImgNavHeader.setImageResource(resourceId)
                binding.andriodDrawerNavInclude.userImgNavHeader.setBackgroundColor(
                    UserDataServices.returnAvatarColor(
                        UserDataServices.avatarColor
                    )
                )
                binding.andriodDrawerNavInclude.LoginBtnNavHeader.text = "Logout"
                MessageServices.getChannels { complete ->
                    if (complete) {
                        if (MessageServices.channels.count() > 0) {
                            selectedChannel = MessageServices.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onLoginBtnNavClicked(view: View) {

        if (App.sharedPrefs.isLoggedIn) {
            UserDataServices.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            binding.andriodDrawerNavInclude.userNameNavHeader.text = ""
            binding.andriodDrawerNavInclude.userEmailNavHeader.text = ""
            binding.andriodDrawerNavInclude.userImgNavHeader.setImageResource(R.drawable.profiledefault)
            binding.andriodDrawerNavInclude.userImgNavHeader.setBackgroundColor(Color.TRANSPARENT)
            binding.andriodDrawerNavInclude.LoginBtnNavHeader.text = "Login"
//            contentMainBinding.mainChannelName.text = "Please log in"
            mainChannelNameTextView.text="Please log in"
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelNavClicked(view: View) {
        if (App.sharedPrefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()
                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel") { _, _ ->
                }
                .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.sharedPrefs.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String
                val newChannel = Channel(channelName, channelDescription, channelId)
                MessageServices.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if (App.sharedPrefs.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if (channelId == selectedChannel?.id) {
                    val msgBody = args[0] as String
                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val avatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String
                    val newMessage =
                        Message(
                            msgBody,
                            channelId,
                            userName,
                            userAvatar,
                            avatarColor,
                            id,
                            timeStamp
                        )
                    MessageServices.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
//                    contentMainBinding.messageListView.smoothScrollToPosition(messageAdapter.itemCount-1)
                    recyclerView.smoothScrollToPosition(messageAdapter.itemCount-1)
                }
            }
        }
    }

    fun updateWithChannel() {
//        contentMainBinding.mainChannelName.text = "#${selectedChannel?.name}"
        mainChannelNameTextView.text = "#${selectedChannel?.name}"
        if (selectedChannel != null) {
            MessageServices.getMessages(selectedChannel!!.id) { complete ->
                if (complete) {
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount > 0){
//                        contentMainBinding.messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                        recyclerView.smoothScrollToPosition(messageAdapter.itemCount-1)
                    }
                }
            }
        }
    }

    fun messageSendBtnClicked(view: View) {
//        if (App.sharedPrefs.isLoggedIn && contentMainBinding.messageTextField.text.isNotEmpty() && selectedChannel != null) {
        if (App.sharedPrefs.isLoggedIn && messageTextEditField.text.isNotEmpty() && selectedChannel != null) {
            val userId = UserDataServices.id
            val channelId = selectedChannel!!.id
            socket.emit(
                "newMessage",
//                contentMainBinding.messageTextField.text.toString(),
                messageTextEditField.text.toString(),
                userId,
                channelId,
                UserDataServices.name,
                UserDataServices.avatarName,
                UserDataServices.avatarColor
            )
//            contentMainBinding.messageTextField.text.clear()
            messageTextEditField.text.clear()
            hideKeyboard()
        }

    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}