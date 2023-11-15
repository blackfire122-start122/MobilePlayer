package com.example.learn

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceListenAgainSongs {
    @GET("/api/getListenAgainSongs/")
    fun getSongs(): Call<List<SongItem>>
}

interface ApiServiceFind{
    @GET("/api/findSong/{findStr}")
    fun findSongs(@Path("findStr") findStr: String): Call<List<SongItem>>
}

interface ApiServiceTop{
    @GET("/api/getTops")
    fun getTops(): Call<List<SongItem>>
}

interface ApiServiceGetUser{
    @GET("/api/getUser")
    fun getUser(): Call<User>
}