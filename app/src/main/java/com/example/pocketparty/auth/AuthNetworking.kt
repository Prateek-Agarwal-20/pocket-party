package com.example.pocketparty.auth

import com.beust.klaxon.Klaxon
import com.example.pocketparty.MainActivity
import com.example.pocketparty.data.SpotifyUser
import okhttp3.OkHttpClient
import okhttp3.Request

object AuthNetworking {
    /*
        Makes a call to the Spotify Web API for user data, once a valid access token is received
     */
    fun getSpotifyUserData(spotifyAccessToken : String, mainActivity: MainActivity) {
        Thread({
            val url = "https://api.spotify.com/v1/me"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + spotifyAccessToken)
                .build()
            client.newCall(request).execute().use {response ->
                val res = response.body()!!.string()
                val result = Klaxon().parse<SpotifyUser>(res)
                getTokenFromServer(result!!.id, mainActivity)
            }
        }).start()
    }

    /*
        Given a spotify id, this function queries the Firebase Function for a new custom token. When it is received, this
        custom token is sent back to Authentication to log the user in to Firebase.
     */
    private fun getTokenFromServer(accessToken: String, mainActivity: MainActivity) {
        Thread({
            val url = "https://us-central1-pocket-party.cloudfunctions.net/getCustomToken?spotifyAccessToken=" + accessToken.take(128)
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).execute().use {response ->
                val res = response.body()!!.string()
                Authentication.loginToFirebaseWithToken(res, mainActivity)
            }
        }).start()
    }
}