package com.example.pocketparty

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.example.pocketparty.data.LightingCueItem
import kotlinx.android.synthetic.main.activity_create_cue.*
import kotlinx.android.synthetic.main.activity_play_project.*
import java.util.*

class PlayProjectActivity : AppCompatActivity() {

    private lateinit var mpProjectSong: MediaPlayer
    private var updateAmount = 0
    private val projectPlayTimer = Timer()
    private lateinit var lightingCues: ArrayList<LightingCueItem>
    private var currentCueIndex = 0
    private lateinit var currentCue: LightingCueItem
    private lateinit var cameraManager: CameraManager
    private lateinit var camId: String
    private var flashEngaged = false
    private var firstTime = true
    private var enabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_project)

        setup()
    }

    inner class playThread : Thread() {
        override fun run() {
            Log.i("TAG", "run loop started")
            Log.i(
                "TAG",
                "isplaying: ${mpProjectSong.isPlaying} --- seek < max: ${sbProjectSeek.progress < sbProjectSeek.max}"
            )
            while (enabled && mpProjectSong.isPlaying && sbProjectSeek.progress < sbProjectSeek.max) {
                Log.i("PROGRESS", sbProjectSeek.progress.toString())
                Log.i("TAG", "while loop entered")
                if (!flashEngaged && mpProjectSong.currentPosition > currentCue.startTime) {
                    cameraManager.setTorchMode(camId, true)
                    flashEngaged = true
                    Log.i("TAG", "flash engage attempted - ${currentCueIndex}")
                } else if (flashEngaged && mpProjectSong.currentPosition >= currentCue.endTime) {
                    cameraManager.setTorchMode(camId, false)
                    flashEngaged = false
                    currentCueIndex++
                    if (currentCueIndex < lightingCues.size) {
                        currentCue = lightingCues.get(currentCueIndex)
                    } else {
                        enabled = false
                    }
                    Log.i("TAG", "flash disengage attempted - ${currentCueIndex}")
                }
            }
        }
    }

    private fun setup() {
        setUpFlashParams()
        lightingCues = intent.getParcelableArrayListExtra<LightingCueItem>(CreateCueActivity.LISTKEY)
        Log.i("TAG", "recieved as: ${lightingCues}")
        mpProjectSong = MediaPlayer.create(this, R.raw.willywonkaremix)
        setupProgressBar()
    }

    //Required parameters to operate the flashlight
    fun setUpFlashParams() {
        cameraManager = applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camId = cameraManager.cameraIdList[0]
    }


    fun setupProgressBar() {
        val duration = mpProjectSong.duration
        sbProjectSeek.max = duration
        updateAmount = duration / 100
        projectPlayTimer.scheduleAtFixedRate(projectPlayTimerTask(), 0, updateAmount.toLong())
        setProjectSeekListener()
    }

    private fun setProjectSeekListener() {
        sbProjectSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mpProjectSong.pause()
                btnProjectPlay.setImageResource(R.drawable.ic_play_arrow_vec)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mpProjectSong.seekTo(sbProjectSeek.progress)
                mpProjectSong.start()
                btnProjectPlay.setImageResource(R.drawable.ic_pause)
            }

        })
    }

    fun playProjectClick(view: View) {
        if (firstTime) {
            currentCue = lightingCues.get(currentCueIndex)
            playThread().start()
            firstTime = false
            enabled = true
        }
        if (mpProjectSong.isPlaying) {
            mpProjectSong.pause()
            btnProjectPlay.setImageResource(R.drawable.ic_play_arrow_vec)
        } else {
            mpProjectSong.start()
            btnProjectPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    fun leftProjectSeekClick(view: View) {
        sbProjectSeek.setProgress(0)
        mpProjectSong.seekTo(0)
    }

    fun rightProjectSeekClick(view: View) {
        sbProjectSeek.setProgress(sbProjectSeek.max)
        mpProjectSong.seekTo(sbProjectSeek.max)
    }

    inner class projectPlayTimerTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (mpProjectSong.isPlaying && sbProjectSeek.progress < sbProjectSeek.max) {
                    sbProjectSeek.progress += updateAmount
                } else if (sbProjectSeek.progress == sbProjectSeek.max) {
                    Toast.makeText(this@PlayProjectActivity, "Song over!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        mpProjectSong.stop()
        projectPlayTimer.cancel()
    }
}
