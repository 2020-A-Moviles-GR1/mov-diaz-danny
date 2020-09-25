package com.example.projectappmobile.models.retrofit

import com.example.projectappmobile.models.retrofit.responses_types.MyServerResponseDataString
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


public interface ApiInterface {
    @Multipart
    @POST("/file/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<MyServerResponseDataString>?

}