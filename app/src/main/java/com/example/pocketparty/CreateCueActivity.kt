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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
    private var currentCueIndex = 0
    private var currentCue = LightingCueItem(0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cue)

        doAnims()
        setup()
    }

    private fun doAnims() {
        val flashButtonAnim = AnimationUtils.loadAnimation(this@CreateCueActivity, R.anim.flashbutton_anim_create)
        btnFlash.startAnimation(flashButtonAnim)

        val seekBarAnim = AnimationUtils.loadAnimation(this@CreateCueActivity, R.anim.seekbar_anim_create)
        sbSongSeek.startAnimation(seekBarAnim)

        val leftSeekAnim = AnimationUtils.loadAnimation(this@CreateCueActivity, R.anim.leftseek_anim_create)
        btnBack.startAnimation(leftSeekAnim)

        val rightSeekAnim = AnimationUtils.loadAnimation(this@CreateCueActivity, R.anim.rightseek_anim_create)
        btnFwd.startAnimation(rightSeekAnim)

        val playButtonAnim = AnimationUtils.loadAnimation(this@CreateCueActivity, R.anim.playbutton_anim_create)
        btnPlay.startAnimation(playButtonAnim)

        val doneButtonAnim = AnimationUtils.loadAnimation(this@CreateCueActivity, R.anim.donebutton_anim)
        btnFinishCreation.startAnimation(doneButtonAnim)
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
                btnPlay.setImageResource(R.drawable.ic_play_arrow_vec)
                btnFlash.isEnabled = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mpWillyWonka.seekTo(sbSongSeek.progress)
                mpWillyWonka.start()
                btnPlay.setImageResource(R.drawable.ic_pause)
                btnFlash.isEnabled = true

                if (mpWillyWonka.currentPosition < currentCue.startTime) {
                    handleBackSeek()
                }
            }

        })
    }

    private fun handleBackSeek() {
        mpWillyWonka.pause()
        setButtonAbility(false)
        Log.i("TAG", "back seek begin")
        Log.i("TAG", "first ${!lightingCues.get(currentCueIndex - 1).equals(null)}")
        Log.i("TAG", "sceond ${lightingCues.get(currentCueIndex - 1).startTime > mpWillyWonka.currentPosition}")
        while (currentCueIndex > 0 && lightingCues.get(currentCueIndex - 1).startTime > mpWillyWonka.currentPosition) {
            currentCueIndex--
            Log.i("TAG", "cueindex on rewind: ${currentCueIndex}")
        }
        Log.i("TAG", "end reached")

        //Todo - fix indexing problem

        for (i in lightingCues.size - 1 downTo currentCueIndex) {
            lightingCues.removeAt(i)
            Log.i("TAG", "size: ${lightingCues.size}")
        }
        if (currentCueIndex > 0) {
            currentCueIndex--
            currentCue = lightingCues.get(currentCueIndex)
            currentCueIndex++
        }

        Log.i("TAG", "cues deleted")


        mpWillyWonka.start()
        setButtonAbility(true)
    }

    private fun setButtonAbility(ability: Boolean) {
        btnPlay.isEnabled = ability
        sbSongSeek.isEnabled = ability
        btnFwd.isEnabled = ability
        btnBack.isEnabled = ability
        btnFlash.isEnabled = ability
        btnFinishCreation.isEnabled = ability
    }

    fun flashLightClick(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            try {
                handleFlashButtonDown()
            } catch (e: CameraAccessException) {
                Toast.makeText(applicationContext, "Error turning flashlight on", Toast.LENGTH_LONG).show()
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            try {
                handleFlashButtonUp()
            } catch (e: CameraAccessException) {
                Toast.makeText(applicationContext, "Error turning flashlight off", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleFlashButtonDown() {
        cameraManager.setTorchMode(camId, true)
        btnFlash.isPressed = true
        lightingCues.add(LightingCueItem(mpWillyWonka.currentPosition, 0))
        currentCue = lightingCues.get(currentCueIndex)
        sbSongSeek.isEnabled = false
    }

    private fun handleFlashButtonUp() {
        cameraManager.setTorchMode(camId, false)
        btnFlash.isPressed = false
        currentCue.endTime = mpWillyWonka.currentPosition
        sbSongSeek.isEnabled = true
        currentCueIndex++
        Log.i("TAG", "cueindex: ${currentCueIndex}")
    }

    fun playButtonClick(view: View) {
        if (mpWillyWonka.isPlaying) {
            mpWillyWonka.pause()
            btnPlay.setImageResource(R.drawable.ic_play_arrow_vec)
        } else {
            mpWillyWonka.start()
            btnPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    fun leftSeekClick(view: View) {
        sbSongSeek.setProgress(0)
        mpWillyWonka.seekTo(0)
        deleteAllCues()
    }

    fun rightSeekClick(view: View) {
        sbSongSeek.setProgress(sbSongSeek.max)
        mpWillyWonka.seekTo(sbSongSeek.max)
    }

    fun doneButtonClick(view: View) {
        val playIntent = Intent(this@CreateCueActivity, PlayProjectActivity::class.java)

        playIntent.putParcelableArrayListExtra(LISTKEY, lightingCues)

        startActivity(playIntent)
    }

    private fun deleteAllCues() {
        lightingCues.clear()
        currentCueIndex = 0
    }

    inner class musicTimerTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (mpWillyWonka.isPlaying && sbSongSeek.progress < sbSongSeek.max) {
                    sbSongSeek.progress += updateAmount
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
