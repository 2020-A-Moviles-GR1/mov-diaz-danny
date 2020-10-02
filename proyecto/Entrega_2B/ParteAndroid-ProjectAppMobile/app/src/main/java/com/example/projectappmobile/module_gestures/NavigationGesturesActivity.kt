package com.example.projectappmobile.module_gestures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectappmobile.R
import kotlinx.android.synthetic.main.activity_navigation_gestures.*

class NavigationGesturesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_gestures)

        buttonNavigationGesturesBack.setOnClickListener {
            finish()
        }
    }
}