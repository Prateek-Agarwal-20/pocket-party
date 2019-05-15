package com.example.pocketparty

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import com.example.pocketparty.auth.Authentication
import com.example.pocketparty.auth.Authentication.AUTH_REQUEST_CODE
import com.example.pocketparty.data.SpotifyAppRemoteSingleton
import com.example.pocketparty.data.SpotifyUser
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_screen.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var spotifyAccessToken: String? = null
    private var spotifyUser: SpotifyUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        loginBtn.setOnClickListener {
            Authentication.connectToSpotify(this)
        }

        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the spotify login activity
        if (requestCode == AUTH_REQUEST_CODE) {
            Authentication.loginWithSpotify(this, resultCode, intent, login_layout)
        }
    }

    fun setSpotifyAppRemote(spotifyAppRemote: SpotifyAppRemote) {
        mSpotifyAppRemote = spotifyAppRemote
    }

    fun saveSpotifyAccessToken(accessToken: String) {
        spotifyAccessToken = accessToken
    }

    fun setSpotifyUser(u: SpotifyUser) {
        spotifyUser = u
    }

    fun finishedLoginFlowSuccess(user: FirebaseUser?) {
        var i = Intent(this@LoginActivity, MainActivity::class.java)
        i.putExtra("FIREBASE_USER", user)
        SpotifyAppRemoteSingleton.spotifyAppRemote = mSpotifyAppRemote!!
        i.putExtra("SPOTIFY_ACCESS_TOKEN", spotifyAccessToken)
        // i.putExtra("SPOTIFY_USER", spotifyUser) TODO: for obtaining the user's profile, parsed by Klaxon according to the SpotifyUser class
        startActivity(i)
    }
}