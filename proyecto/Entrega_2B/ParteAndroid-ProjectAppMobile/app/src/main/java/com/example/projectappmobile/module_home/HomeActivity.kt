package com.example.projectappmobile.module_home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.projectappmobile.R
import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.module_account.view.MyProfileActivity
import com.example.projectappmobile.module_game_room.view.ConnectRoomActivity
import com.example.projectappmobile.module_gestures.MyGesturesActivity
import com.example.projectappmobile.module_gestures.NavigationGesturesActivity
import com.example.projectappmobile.module_testing.TestActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(){

    companion object {
        val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        buttonHomeOpenGameRoom.setOnClickListener{
            startActivity(Intent(this, ConnectRoomActivity::class.java))
        }

        buttonHomeMyGestures.setOnClickListener{
            startActivity(Intent(this, MyGesturesActivity::class.java))
        }

        buttonHomeMyProfile.setOnClickListener{
            startActivity(Intent(this, MyProfileActivity::class.java))
        }

        buttonHomeNavigationGestures.setOnClickListener{
            startActivity(Intent(this, NavigationGesturesActivity::class.java))
        }

        if(User.loggedUser?.username.equals("user_test")){
            buttonHomeTestMode.setOnClickListener {
                startActivity(Intent(this, TestActivity::class.java))
            }
            buttonHomeTestMode.visibility = View.VISIBLE
        }else{
            User.loggedUser?.username?.let { Log.i(TAG, it) }
        }

    }

    override fun onBackPressed() {

        setResult(Activity.RESULT_OK)

        super.onBackPressed()
    }





}