package com.example.pocketparty

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.View.GONE
import android.view.View.VISIBLE
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
            loginBtn.visibility = GONE
//            enterBtn.visibility = GONE
            pbLoading.visibility = VISIBLE
        }

//        enterBtn.setOnClickListener {
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//        }

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

    fun redirectToSpotify() {
        // TODO: redirect to play store to download spotify
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
        i.putExtra("FIREBASE_USER", user!!)
        SpotifyAppRemoteSingleton.spotifyAppRemote = mSpotifyAppRemote!!
        i.putExtra("SPOTIFY_ACCESS_TOKEN", spotifyAccessToken)
        i.putExtra("SPOTIFY_USER_IMG", spotifyUser!!.images[0].url)
        i.putExtra("SPOTIFY_USER_DISPLAY_NAME", spotifyUser!!.display_name)
        startActivity(i)
    }
}
