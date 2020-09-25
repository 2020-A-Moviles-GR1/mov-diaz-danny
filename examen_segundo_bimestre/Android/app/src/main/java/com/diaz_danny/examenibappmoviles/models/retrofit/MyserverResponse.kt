package com.example.projectappmobile.models.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


abstract class MyserverResponse {

    //no está dañado es mejor no moverlo
    @Expose
    @SerializedName("id_message")
    public val id_message: Int? = -999

    @Expose
    @SerializedName("server_message")
    public val server_message: String? = null

    @Expose
    @SerializedName("metadata")
    public val metadata: String? = null

    @Expose
    @SerializedName("error")
    public val error: String? = null
    override fun toString(): String {
        return "MyServerResponse(id_message=$id_message, server_message=$server_message, metadata=$metadata, error=$error)"
    }


}