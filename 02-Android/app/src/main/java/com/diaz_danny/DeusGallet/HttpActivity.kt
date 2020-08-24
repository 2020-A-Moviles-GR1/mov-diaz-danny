package com.diaz_danny.DeusGallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import kotlinx.android.synthetic.main.activity_http.*

class HttpActivity : AppCompatActivity() {

    val urlPrincipal = "http://192.168.1.5:1337"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)

        buttonHttpObtener.setOnClickListener{
            obtenerUsuarios()
        }
    }

    private fun obtenerUsuarios() {
        val url = urlPrincipal + "/usuario"

        url
            .httpGet()
            .responseString{
                request, response, result ->

                when(result){
                    is com.github.kittinunf.result.Result.Success -> {
                        val data = result.get()
                        Log.i("http-klaxon", "Data: ${data}")
                    }
                    is com.github.kittinunf.result.Result.Failure -> {
                        val ex = result.getException()
                        Log.i("http-klaxon", "Error: ${ex.message}")
                    }
                }

            }

    }
}