package com.example.pocketparty.network

import com.example.pocketparty.data.CurrentPlayerContext
import com.example.pocketparty.data.CurrentPlayerDevices
import com.example.pocketparty.data.CurrentlyPlaying
import retrofit2.Call
import retrofit2.http.GET

interface SpotifyAPI {
    @GET("/v1/me/player/currently-playing")
    fun getCurrentlyPlaying() : Call<CurrentlyPlaying>

    @GET("/v1/me/player")
    fun getPlayerContext() : Call<CurrentPlayerContext>

    @GET("/v1/me/player/devices")
    fun getPlayerDevices(): Call<CurrentPlayerDevices>
}