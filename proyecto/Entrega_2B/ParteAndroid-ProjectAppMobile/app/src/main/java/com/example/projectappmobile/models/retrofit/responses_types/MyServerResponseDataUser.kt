package com.example.projectappmobile.models.retrofit.responses_types

import com.example.projectappmobile.models.database.User
import com.example.projectappmobile.models.retrofit.MyserverResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyServerResponseDataUser: MyserverResponse() {

    @Expose
    @SerializedName("data")
    public val data: List<User>? = null
    override fun toString(): String {
        return super.toString() + "MyServerResponseDataUser(data=$data)"
    }


}