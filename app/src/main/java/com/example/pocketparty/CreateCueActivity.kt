package com.example.pocketparty

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_cue.*
import java.util.*

class CreateCueActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var camId: String
    private lateinit var mpWillyWonka: MediaPlayer
    private var updateAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cue)

        setup()
    }

    private fun setup(){
        setUpFlashParams()
        setupFlashOntouch()
        mpWillyWonka = MediaPlayer.create(this, R.raw.willywonkaremix)
        setupProgressBar()
    }

    //Required parameters to operate the flashlight
    fun setUpFlashParams() {
        cameraManager = applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camId = cameraManager.cameraIdList[0]
    }

    fun setupFlashOntouch(){
        btnFlash.setOnTouchListener { v, event ->
            flashLightClick(event)
            true
        }
    }

    fun setupProgressBar(){
        val duration = mpWillyWonka.duration
        sbSongSeek.max = duration
        updateAmount = duration/100
        val musicTimer = Timer()
        musicTimer.scheduleAtFixedRate(musicTimerTask(), 0, 1000)
        Toast.makeText(this, "after Scheduled line", Toast.LENGTH_SHORT).show()
    }


    fun flashLightClick(event: MotionEvent){
        if (event.action == MotionEvent.ACTION_DOWN) {
            try {
                cameraManager.setTorchMode(camId, true)
            } catch (e: CameraAccessException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "DAMNIT it wont turn ON", Toast.LENGTH_LONG).show()
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            try {
                cameraManager.setTorchMode(camId, false)
            } catch (e: CameraAccessException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "DAMNIT it wont turn OFF", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun playButtonClick(view: View){
        if(mpWillyWonka.isPlaying){
            mpWillyWonka.pause()
            btnPlay.isSelected = false
        } else{
            mpWillyWonka.start()
            btnPlay.isSelected = true
        }
    }

    inner class musicTimerTask: TimerTask(){
        override fun run() {
            runOnUiThread {
//                Log.i("TAG", "running")
                if(mpWillyWonka.isPlaying && updateAmount * sbSongSeek.progress < sbSongSeek.max){
                    sbSongSeek.progress += 1
//                    Log.i("TAG", "in loop")
                }
            }
        }

    }
}
