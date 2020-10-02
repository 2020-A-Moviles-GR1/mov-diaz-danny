package com.example.projectappmobile.models.retrofit

import com.example.projectappmobile.models.Parameters
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


public class ApiClient {



    companion object {
        private var retrofit: Retrofit? = null

        fun getApiClient(): Retrofit? {
            // Patrón Singleton
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Parameters.BASE_URL_BACKEND_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }


}