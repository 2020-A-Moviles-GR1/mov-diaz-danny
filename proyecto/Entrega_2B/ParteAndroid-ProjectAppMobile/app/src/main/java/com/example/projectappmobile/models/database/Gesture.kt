package com.example.projectappmobile.models.database

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Gesture(
    @Expose
    @SerializedName("id_function")
    val id_function: Int = -1,
    @Expose
    @SerializedName("username_user")
    val username_user: String? = "user_test",
    @Expose
    @SerializedName("name")
    val name: String? = "null"

) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id_function)
        parcel.writeString(username_user)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Gesture(id_function=$id_function, username_user=$username_user, name=$name)"
    }

    companion object CREATOR : Parcelable.Creator<Gesture> {
        override fun createFromParcel(parcel: Parcel): Gesture {
            return Gesture(parcel)
        }

        override fun newArray(size: Int): Array<Gesture?> {
            return arrayOfNulls(size)
        }
    }



}