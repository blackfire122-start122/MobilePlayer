package com.example.learn

data class SongItem(val name: String, val image: String, val fileSong: String)
data class User(val id: String, val username: String,val image: String,val email: String,val phone: String)
sealed class Message {
    data class OnlineUsers(val type: String, val content: List<User>) : Message()
    data class StringMessage(val type: String, val content: String) : Message()
}
