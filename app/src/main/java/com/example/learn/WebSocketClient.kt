package com.example.learn

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketClient(private val listener: PlayerSong) {
    private val client = OkHttpClient()
    private val request = Request.Builder().url("$host/ws/test").build()
    private val webSocket = client.newWebSocket(request, MyWebSocketListener())

    interface WebSocketListenerInterface {
        fun onConnected()
        fun onResponse(data: Message)
        fun onClosed()
    }

    inner class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            listener.onConnected()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Response: $text")
            try {
                val data = Gson().fromJson(text, Message::class.java)
                println(data)
                listener.onResponse(data)
            }catch (e: Exception) {
                println("[+] Except $e")
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            listener.onClosed()
        }
    }

    fun sendMessage(message: String) {
        webSocket.send(message)
    }

    fun closeWebSocket() {
        webSocket.close(1000, "App close connection")
    }
}
