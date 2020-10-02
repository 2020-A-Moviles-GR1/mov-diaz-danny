package com.example.projectappmobile.models.database

public class User (
    val username: String,
    val full_name: String
) {
    companion object {
        var loggedUser: User? = null

        fun getLoggedIngUser(): User?{
            return loggedUser
        }
    }
}