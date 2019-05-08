package com.example.pocketparty.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.example.pocketparty.LoginActivity
import com.example.pocketparty.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_screen.*
import android.support.v4.content.ContextCompat.startActivity



object Authentication {

    private val CLIENT_ID = "30c672e2985240cbbfaee0eedccfb14b"
    val AUTH_REQUEST_CODE = 1337
    val AUTH_REDIRECT_URI = "pocket-party://spotify-login"


    /*
        This function is in charge of connecting to Spotify App Remote. This gives basic functionality
        for playback directly through the app downloaded on the phone, which is much faster than API calls
     */
    fun connectToSpotify(loginActivity: LoginActivity) {
        // Set the connection parameters
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(AUTH_REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(loginActivity, connectionParams,
            object : Connector.ConnectionListener {

                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    loginActivity.setSpotifyAppRemote(spotifyAppRemote)
                    Log.d("MainActivity", "Connected! Yay!")

                    // Now you can start interacting with App Remote
                    initiateSpotifyUserLogin(loginActivity)
                }

                override fun onFailure(error: Throwable) {
                    if(error is CouldNotFindSpotifyApp){
                        Log.d("SPOTIFY_REMOTE_LOGIN", "no spotify app")
                        Snackbar.make(
                            loginActivity.login_layout, "Please download Spotify to proceed", Snackbar.LENGTH_LONG
                        ).show()
                        loginActivity.redirectToSpotify()
                    }
                    if(error is NotLoggedInException) {
                        Log.d("SPOTIFY_REMOTE_LOGIN", "user is not logged in")
                        Snackbar.make(
                            loginActivity.login_layout, "Please login to Spotify to proceed", Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Log.d("SPOTIFY_REMOTE_LOGIN", error.message, error)
                }
            })
    }


    /*
        Starts Spotify App Remote login with the SDK.
     */
    private fun initiateSpotifyUserLogin(fromActivity: Activity) {
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, AUTH_REDIRECT_URI)

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(fromActivity, AUTH_REQUEST_CODE, request)
    }


    /*
        If the response from logging in to Spotify App Remote is positive, we go ahead and log in through the API.
        This gives us access to detailed user information.
    */
    fun loginWithSpotify(loginActivity: LoginActivity, resultCode: Int, intent: Intent?, layout: View){
        val response = AuthenticationClient.getResponse(resultCode, intent)

        when (response.type) {
            // Response was successful and contains auth token
            AuthenticationResponse.Type.TOKEN -> {
                Log.d("SPOTIFY LOGIN", "Successfully retrieved spotify login token: " + response.accessToken)
                Snackbar.make(
                    layout, "Successfully logged in to Spotify!", Snackbar.LENGTH_SHORT
                ).show()

                loginActivity.saveSpotifyAccessToken(response.accessToken)

                AuthNetworking.getSpotifyUserData(response.accessToken, loginActivity) // Triggers -> getTokenFromServer -> loginToFirebaseWithToken
            }
            // Auth flow returned an error
            AuthenticationResponse.Type.ERROR -> {
                Log.d("SPOTIFY_LOGIN", "error retrieving spotify login token")
                Snackbar.make(
                    layout, "Error retreiving spotify login token " + response.error, Snackbar.LENGTH_LONG
                ).show()
            }
            else -> {
                Log.d("SPOTIFY_LOGIN", "some other error")
            }
        }
    }


    /*
        Given the custom token received from the Firebase Function, login to Firebase, thus setting the currentUser attribute
        in FirebaseAuth. It automatically checks for duplicate users (as seen in the differing snackbar messages)
     */
    fun loginToFirebaseWithToken(customToken:String, loginActivity: LoginActivity) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCustomToken(customToken).addOnCompleteListener(loginActivity) { task ->
            if (task.isSuccessful) {
                var isNew = task.getResult()!!.getAdditionalUserInfo().isNewUser();
                if (isNew) {
                    Snackbar.make(loginActivity.login_layout, "Welcome " + auth.uid + "!", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(loginActivity.login_layout, "Welcome back " + auth.uid + "!", Snackbar.LENGTH_LONG).show()
                }

                Log.d("FIREBASE_SIGN_IN", "successfully signed in user " + auth.uid)
                val user = auth.currentUser
                loginActivity.finishedLoginFlowSuccess(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w("FIREBASE_SIGN_IN", "signInWithCustomToken:failure", task.exception)
                Snackbar.make(
                    loginActivity.login_layout, "Authentication failed. Check logs or try again", Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}