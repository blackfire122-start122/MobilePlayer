package com.example.learn

data class SongItem(val name: String, val image: String, val fileSong: String)
data class User(val roomId: String)
sealed class Message {
    data class OnlineUsers(val type: String, val content: List<User>) : Message()
    data class StringMessage(val type: String, val content: String) : Message()

}
