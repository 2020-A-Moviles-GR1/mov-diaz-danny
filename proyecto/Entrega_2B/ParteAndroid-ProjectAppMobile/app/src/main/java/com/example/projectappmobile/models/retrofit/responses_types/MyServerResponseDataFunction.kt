package com.example.projectappmobile.models.retrofit.responses_types

import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.retrofit.MyserverResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyServerResponseDataFunction: MyserverResponse() {

    @Expose
    @SerializedName("data")
    public var data: List<Function>? = null

    override fun toString(): String {
        return super.toString() + "MyServerResponseDataString(data=$data)"
    }


}