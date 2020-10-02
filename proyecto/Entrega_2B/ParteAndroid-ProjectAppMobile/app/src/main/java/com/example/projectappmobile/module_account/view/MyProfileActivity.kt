package com.example.projectappmobile.module_account.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectappmobile.R
import com.example.projectappmobile.models.database.User
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        textViewMyProfileUsername.text = "Nombre de usuario: ${User.getLoggedIngUser()?.username}"
        textViewMyProfileFullName.text = "Nombre completo: ${User.getLoggedIngUser()?.full_name}"
    }
}