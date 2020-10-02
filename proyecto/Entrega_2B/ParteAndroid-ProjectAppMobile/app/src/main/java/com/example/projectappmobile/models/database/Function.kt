package com.example.projectappmobile.models.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Function(
    @Expose
    @SerializedName("id")
    public val id: Int = -1,
    @Expose
    @SerializedName("name")
    public val name: String = "",
    @Expose
    @SerializedName("description")
    public val description: String? = null
) {

    companion object {
        var mapFunctionToDescription = HashMap<Int, Any>()
        var mapFunctionToName = HashMap<Int, Any>()
    }

    override fun toString(): String {
        return "Function(id=$id, name='$name', description=$description)"
    }


}