package com.example.projectappmobile.models.sockets

import android.content.Context
import android.os.Handler
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONException
import org.json.JSONObject

class SocketListener(
    private val context: Context,
    private val handlerMainThread: Handler,
    private val presenter: SocketInterface
) : WebSocketListener() {

    companion object {
        interface SocketInterface {
            fun onOpen()
            fun onMessage(jsonObject: JSONObject)
            fun onFailure(t: Throwable)

        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        handlerMainThread.post{
            presenter.onOpen()
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        handlerMainThread.post{
            try {
                val jsonObject = JSONObject(text)
                jsonObject.put("isSent", false)
                presenter.onMessage(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        handlerMainThread.post{
            presenter.onFailure(t)
        }
    }


}

