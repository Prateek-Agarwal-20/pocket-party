package com.example.pocketparty

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.example.pocketparty.data.LightingCueItem
import kotlinx.android.synthetic.main.activity_create_cue.*
import java.util.*

class CreateCueActivity : AppCompatActivity() {

    companion object {
        val LISTKEY = "LISTKEY"
    }

    private lateinit var cameraManager: CameraManager
    private lateinit var camId: String
    private lateinit var mpWillyWonka: MediaPlayer
    private var updateAmount = 0
    private val lightingCues = ArrayList<LightingCueItem>()
    private val musicTimer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cue)

        setup()
    }

    private fun setup() {
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

    fun setupFlashOntouch() {
        btnFlash.setOnTouchListener { v, event ->
            flashLightClick(event)
            true
        }
    }

    fun setupProgressBar() {
        val duration = mpWillyWonka.duration
        sbSongSeek.max = duration
        updateAmount = duration / 100
        musicTimer.scheduleAtFixedRate(musicTimerTask(), 0, updateAmount.toLong())
        setSeekListener()
    }

    private fun setSeekListener() {
        sbSongSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mpWillyWonka.pause()
//                btnPlay.isSelected = false
                btnPlay.setImageResource(R.drawable.ic_play_arrow_vec)
                btnFlash.isEnabled = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mpWillyWonka.seekTo(sbSongSeek.progress)
                mpWillyWonka.start()
//                btnPlay.isSelected = true
                btnPlay.setImageResource(R.drawable.ic_pause)
                btnFlash.isEnabled = true
            }

        })
    }

    fun flashLightClick(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            try {
                cameraManager.setTorchMode(camId, true)
                btnFlash.isPressed = true
                lightingCues.add(LightingCueItem(mpWillyWonka.currentPosition, 0))
                sbSongSeek.isEnabled = false
            } catch (e: CameraAccessException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "DAMNIT it wont turn ON", Toast.LENGTH_LONG).show()
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            try {
                cameraManager.setTorchMode(camId, false)
                btnFlash.isPressed = false
                lightingCues.get(lightingCues.size-1).endTime = mpWillyWonka.currentPosition
                sbSongSeek.isEnabled = true
            } catch (e: CameraAccessException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "DAMNIT it wont turn OFF", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun playButtonClick(view: View) {
        if (mpWillyWonka.isPlaying) {
            mpWillyWonka.pause()
//          btnPlay.isSelected = false
            btnPlay.setImageResource(R.drawable.ic_play_arrow_vec)
        } else {
            mpWillyWonka.start()
//            btnPlay.isSelected = true
            btnPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    fun leftSeekClick(view: View){
        sbSongSeek.setProgress(0)
        mpWillyWonka.seekTo(0)
    }

    fun rightSeekClick(view: View){
        sbSongSeek.setProgress(sbSongSeek.max)
        mpWillyWonka.seekTo(sbSongSeek.max)
    }

    inner class musicTimerTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (mpWillyWonka.isPlaying && sbSongSeek.progress < sbSongSeek.max) {
                    sbSongSeek.progress += updateAmount
                } else if(sbSongSeek.progress == sbSongSeek.max){
                    val playIntent = Intent(this@CreateCueActivity, PlayProjectActivity::class.java)

                    playIntent.putParcelableArrayListExtra(LISTKEY, lightingCues)

                    startActivity(playIntent)
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        musicTimer.cancel()
        mpWillyWonka.stop()
    }
}
