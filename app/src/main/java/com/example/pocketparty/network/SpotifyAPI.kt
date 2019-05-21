package com.example.pocketparty.network

import com.example.pocketparty.data.CurrentPlayerContext
import com.example.pocketparty.data.CurrentPlayerDevices
import com.example.pocketparty.data.CurrentlyPlaying
import com.example.pocketparty.data.PlayResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT

interface SpotifyAPI {
    @GET("/v1/me/player/currently-playing")
    fun getCurrentlyPlaying() : Call<CurrentlyPlaying>

    @GET("/v1/me/player")
    fun getPlayerContext() : Call<CurrentPlayerContext>

    @GET("/v1/me/player/devices")
    fun getPlayerDevices(): Call<CurrentPlayerDevices>

    @PUT("/v1/me/player/play")
    fun playFromSpotify(): Call<PlayResponse>
}