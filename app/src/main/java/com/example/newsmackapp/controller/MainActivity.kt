package com.example.newsmackapp.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.newsmackapp.R
import com.example.newsmackapp.databinding.ActivityMainBinding
import com.example.newsmackapp.services.AuthService
import com.example.newsmackapp.services.UserDataServices
import com.example.newsmackapp.utilities.BROADCAST_USER_DATA_CHANGE

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
        hideKeyboard()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn){
                binding.andriodDrawerNavInclude.userNameNavHeader.text = UserDataServices.name
                binding.andriodDrawerNavInclude.userEmailNavHeader.text = UserDataServices.email
                val resourceId = resources.getIdentifier(UserDataServices.avatarName, "drawable", packageName)
                binding.andriodDrawerNavInclude.userImgNavHeader.setImageResource(resourceId)
                binding.andriodDrawerNavInclude.userImgNavHeader.setBackgroundColor(UserDataServices.returnAvatarColor(UserDataServices.avatarColor))
                binding.andriodDrawerNavInclude.LoginBtnNavHeader.text = "Logout"
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onLoginBtnNavClicked(view: View){

        if(AuthService.isLoggedIn){
            UserDataServices.logout()
            binding.andriodDrawerNavInclude.userNameNavHeader.text = ""
            binding.andriodDrawerNavInclude.userEmailNavHeader.text = ""
            binding.andriodDrawerNavInclude.userImgNavHeader.setImageResource(R.drawable.profiledefault)
            binding.andriodDrawerNavInclude.userImgNavHeader.setBackgroundColor(Color.TRANSPARENT)
            binding.andriodDrawerNavInclude.LoginBtnNavHeader.text = "Login"

        } else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelNavClicked(view: View){

        if(AuthService.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add"){ dialogInterface, i ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    hideKeyboard()

                }
                .setNegativeButton("Cancel"){ dialogInterface, i ->
                    hideKeyboard()
                }
                .show()

        }

    }

    fun messageSendBtnClicked(view: View){

    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}