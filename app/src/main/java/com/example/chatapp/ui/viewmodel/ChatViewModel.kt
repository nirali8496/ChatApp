package com.example.chatapp.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.model.ChatMessage
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ChatViewModel : ViewModel() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val _messages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    private val queue = mutableListOf<String>()

    fun connectSocket() {
//        val request = Request.Builder().url("wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV").build()
        val request = Request.Builder().url("wss://demo.piesocket.com/v3/channel_1?api_key=03aa4515462e05c48f976a1373ccb7693c7990ec").build()

/*        val request = Request.Builder()
            .url("wss://echo.websocket.events")  // ✅ Free public echo server
            .build()*/

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.e("WebSocket", "onOpen: ${response.message}")

                retryQueue()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e("WebSocket", "onMessage: ${text}")

                _messages.value?.add(ChatMessage(text, isSent = false))
                _messages.postValue(_messages.value)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Failure: ${t.message}")
                reconnect()

            }
        })
    }

    private fun reconnect() {
        Handler(Looper.getMainLooper()).postDelayed({
            connectSocket()
        }, 3000)
    }


    fun sendMessage(message: String) {
        val sent = webSocket?.send(message) ?: false
        if (sent) {
            _messages.value?.add(ChatMessage(message, isSent = true))
            _messages.postValue(_messages.value)
        } else {
            queue.add(message)
        }
    }

    fun retryQueue() {
/*        queue.toList().forEach {
            if (webSocket?.send(it) == true) {
                _messages.value?.add(ChatMessage(it, isSent = true))
                queue.remove(it)
            }
        }*/
        viewModelScope.launch {
            val iterator = queue.iterator()
            while (iterator.hasNext()) {
                val message = iterator.next()
                if (webSocket?.send(message) == true) {
                    _messages.value?.add(ChatMessage(message, isSent = true))
                    _messages.postValue(_messages.value)
                    iterator.remove()
                }
            }
        }

    }
}
