package com.example.pocketparty

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.pocketparty.data.FireInterface

class FirebaseTestingActivity : AppCompatActivity() {

    private lateinit var fbInterface: FireInterface
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_testing)

        setUpFireInterface()
    }

    private fun setUpFireInterface(){
        userID = intent.getStringExtra("userID")
        if(userID.equals(null)){
            Toast.makeText(this, "failed to get userID", Toast.LENGTH_LONG).show()
        }
        fbInterface = FireInterface(this, userID)
    }

    fun createFakeCuesClick(view: View){
        fbInterface.addFakeCueList()
    }

    fun getSingleCueClick(view: View) {
        val cue = fbInterface.getAllLightingCues()[0]
        Log.i("CUE NAME", cue.cueName)
        Log.i("CUE ARTIST", cue.cueArtistName)
        Log.i("CUE IMAGE LINK", cue.cueArtistImage)
        Log.i("TRACK NAME", cue.track.name)
        Log.i("TRACK ARTIST", cue.track.artist)
        Log.i("TRACK IMAGE LINK", cue.track.imageLink)
        Log.i("TRACK SPOTIFY URI", cue.track.spotifyUri)
        Log.i("NUM PHONES", cue.numPhones.toString())
        Log.i("CUE LIST", cue.cueList[0][0].startTime.toString())
    }
}
