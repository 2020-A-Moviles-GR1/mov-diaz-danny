package com.example.projectappmobile

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import com.example.projectappmobile.models.sockets.SocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONException
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class SocketMessagingUnitTest : SocketListener.Companion.SocketInterface{

    lateinit var context: Context
    lateinit var webSocket: WebSocket
    private val SERVER_PATH: String = "ws://192.168.1.5:3000"
    private var mock_waiting_response: Boolean = true;


    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val handler = Handler(context.mainLooper)
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener(
            context, handler, this)
        )
    }

    @Test
    fun sendMessage(){

        val jsonObject = JSONObject()
        try {
            jsonObject.put("id", 1)
            jsonObject.put("data", "function-1")
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    @Test
    fun close(){
        webSocket.close(1000, "Normal")
    }

    override fun onOpen() {
        println("Conectado al socket! en $SERVER_PATH")
    }

    override fun onMessage(jsonObject: JSONObject) {
        println(jsonObject)
        close()
        mock_waiting_response = false
    }


}