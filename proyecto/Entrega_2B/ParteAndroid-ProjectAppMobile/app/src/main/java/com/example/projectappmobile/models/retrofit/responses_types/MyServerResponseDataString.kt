package com.example.projectappmobile.models.retrofit.responses_types

import com.example.projectappmobile.models.retrofit.MyserverResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyServerResponseDataString: MyserverResponse() {

    @Expose
    @SerializedName("data")
    public val data: List<String?>? = null
    override fun toString(): String {
        return super.toString() + "MyServerResponseDataString(data=$data)"
    }


}