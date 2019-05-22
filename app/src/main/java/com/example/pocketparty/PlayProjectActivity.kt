package com.example.pocketparty

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.example.pocketparty.data.LightingCue
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
    private lateinit var cue: LightingCue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_project)

        cue = intent.getParcelableExtra<LightingCue>("LIGHTING_CUE")

        doAnims()
        setup()
    }

    private fun doAnims() {
//        val seekBarAnim = AnimationUtils.loadAnimation(this@PlayProjectActivity, R.anim.seekbar_anim_create)
//        sbProjectSeek.startAnimation(seekBarAnim)

        val leftSeekAnim = AnimationUtils.loadAnimation(this@PlayProjectActivity, R.anim.leftseek_anim_create)
        btnProjectLeftSeek.startAnimation(leftSeekAnim)

        val rightSeekAnim = AnimationUtils.loadAnimation(this@PlayProjectActivity, R.anim.rightseek_anim_create)
        btnProjectRightSeek.startAnimation(rightSeekAnim)

        val playProjectAnim = AnimationUtils.loadAnimation(this@PlayProjectActivity, R.anim.flashbutton_anim_create)
        btnProjectPlay.startAnimation(playProjectAnim)
    }

    inner class playThread : Thread() {
        override fun run() {

            Log.i("TAG", "Pre-while")
            Log.i("TAG", "enabled: ${enabled}")
            Log.i("TAG", "playing: ${songIsPlaying()}")
            Log.i("TAG", "duration: ${getSongCurrentPosition() < getSongDuration()}")


            while (enabled && getSongCurrentPosition() < getSongDuration()){
//                && sbProjectSeek.progress < sbProjectSeek.max) {

                Log.i("REPEAT", "While loop entered")

                if (!flashEngaged && getSongCurrentPosition() > currentCue.startTime) {
                    cameraManager.setTorchMode(camId, true)
                    flashEngaged = true
                    Log.i("TAG", "flash engage attempted - ${currentCueIndex}")
                    Log.i("TIMECHECK", "ON time: ${getSongCurrentPosition()}")
                    Log.i("TIMECHECK", "ON startTime: ${currentCue.startTime}")
                    Log.i("TIMECHECK", "ON endTime: ${currentCue.endTime}")
                }
                if (getSongCurrentPosition() >= currentCue.endTime) {
                    cameraManager.setTorchMode(camId, false)
                    flashEngaged = false
                    currentCueIndex++
                    if (currentCueIndex < lightingCues.size) {
                        currentCue = lightingCues.get(currentCueIndex)
                    } else {
                        enabled = false
                        currentCueIndex--
                    }
                    Log.i("TIMECHECK", "OFF time: ${getSongCurrentPosition()}")
                    Log.i("TIMECHECK", "OFF startTime: ${currentCue.startTime}")
                    Log.i("TIMECHECK", "OFF endTime: ${currentCue.endTime}")
                    Log.i("TAG", "flash disengage attempted - ${currentCueIndex}")
                }
            }
        }
    }

    private fun setUpMediaPlayer(){
        //Todo - prateek update for spotify
        mpProjectSong = MediaPlayer.create(this, cue.track.spotifyUri.toInt())
    }

    private fun playSong(){
        //Todo - Prateek, update for spotify
        mpProjectSong.start()
    }

    private fun pauseSong(){
        //Todo - Prateek, update for spotyify
        mpProjectSong.pause()
    }

    private fun stopSong(){
        //Todo - Prateek, update for spotyify
        mpProjectSong.stop()
    }

    private fun seekSongTo(progress: Int){
        //Todo - Prateek, update for spotyify
        mpProjectSong.seekTo(progress)
    }

    private fun songIsPlaying(): Boolean{
        //Todo - Prateek, update for spotyify
        return mpProjectSong.isPlaying
    }

    private fun getSongCurrentPosition(): Int{
        //Todo - Prateek, update for spotyify
        return mpProjectSong.currentPosition
    }

    private fun getSongDuration(): Int{
        return mpProjectSong.duration
    }

    private fun setup() {
        setUpFlashParams()
        lightingCues = cue.cueList[0] as ArrayList<LightingCueItem>
        if (!lightingCues.isEmpty()) {
            Log.i("TAG", "recieved as: ${lightingCues}")
            setUpMediaPlayer()
//            setupProgressBar()
            setUpOnClicks()
        } else {
            Toast.makeText(this, "No lighting cues found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpOnClicks() {
        btnProjectPlay.setOnClickListener { playProjectClick() }
        btnProjectLeftSeek.setOnClickListener { leftProjectSeekClick() }
        btnProjectRightSeek.setOnClickListener { rightProjectSeekClick() }
    }

    //Required parameters to operate the flashlight
    fun setUpFlashParams() {
        cameraManager = applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        camId = cameraManager.cameraIdList[0]
    }


//    fun setupProgressBar() {
//        val duration = mpProjectSong.duration
//        sbProjectSeek.max = duration
//        updateAmount = duration / 100
//        projectPlayTimer.scheduleAtFixedRate(projectPlayTimerTask(), 0, updateAmount.toLong())
//        setProjectSeekListener()
//    }

//    private fun setProjectSeekListener() {
//        sbProjectSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                mpProjectSong.pause()
//                btnProjectPlay.setImageResource(R.drawable.ic_play_arrow_vec)
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                mpProjectSong.seekTo(sbProjectSeek.progress)
//                mpProjectSong.start()
//                btnProjectPlay.setImageResource(R.drawable.ic_pause)
//
//                if (mpProjectSong.currentPosition < currentCue.startTime) {
//                    handleBackSeek()
//
//                }
//
//                if(mpProjectSong.currentPosition > currentCue.endTime){
//                    handleForwardSeek()
//                }
//            }
//
//        })
//    }

//    private fun handleBackSeek() {
//        mpProjectSong.pause()
//        setButtonAbility(false)
//        playThread().join()
//        Log.i("TAG", "back seek begin")
//        while (currentCueIndex > 0 && lightingCues.get(currentCueIndex - 1).startTime > mpProjectSong.currentPosition) {
//            currentCueIndex--
//            Log.i("TAG", "cueindex on rewind: ${currentCueIndex}")
//        }
//        Log.i("TAG", "end reached")
//
//        //Todo - fix indexing problem
//
//        if (currentCueIndex > 0) {
////            currentCueIndex--
//            currentCue = lightingCues.get(currentCueIndex)
//            mpProjectSong.seekTo(currentCue.endTime)
//            sbProjectSeek.setProgress(currentCue.endTime)
//        }
//
//        mpProjectSong.start()
//        enabled = true
//        playThread().start()
//        Log.i("TIMECHECK", "----------------------------------------------------")
//        setButtonAbility(true)
//    }

//    private fun handleForwardSeek(){
//        mpProjectSong.pause()
//        setButtonAbility(false)
//        playThread().join()
//        while(currentCueIndex < lightingCues.size - 1 && lightingCues.get(currentCueIndex + 1).endTime < mpProjectSong.currentPosition){
//            currentCueIndex++
//        }
//
//        if(currentCueIndex < lightingCues.size-1){
////            currentCueIndex++
//            currentCue = lightingCues.get(currentCueIndex)
//            mpProjectSong.seekTo(currentCue.endTime)
//            sbProjectSeek.setProgress(currentCue.endTime)
//        }
//
//        mpProjectSong.start()
//        enabled = true
//        playThread().start()
//        Log.i("TIMECHECK", "----------------------------------------------------")
//        setButtonAbility(true)
//    }

    private fun setButtonAbility(ability: Boolean) {
        btnProjectLeftSeek.isEnabled = ability
        btnProjectRightSeek.isEnabled = ability
        btnProjectPlay.isEnabled = ability
//        sbProjectSeek.isEnabled = ability
    }

    fun playProjectClick() {
        if (firstTime) {
            currentCue = lightingCues.get(currentCueIndex)
            enabled = true
            playThread().start()
            firstTime = false
        }
        if (songIsPlaying()) {
            pauseSong()
            btnProjectPlay.setImageResource(R.drawable.ic_play_arrow_vec)
        } else {
            playSong()
            btnProjectPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    fun leftProjectSeekClick() {
//        sbProjectSeek.setProgress(0)
        seekSongTo(0)
        currentCueIndex = 0
        currentCue = lightingCues.get(currentCueIndex)
        playThread().join()
        enabled = true
        playThread().start()
    }

    fun rightProjectSeekClick() {
//        sbProjectSeek.setProgress(sbProjectSeek.max)
//        mpProjectSong.seekTo(sbProjectSeek.max)
        seekSongTo(getSongDuration())
        currentCueIndex = lightingCues.size - 1
        currentCue = lightingCues.get(currentCueIndex)
    }
//
//    inner class projectPlayTimerTask : TimerTask() {
//        override fun run() {
//            runOnUiThread {
//                if (mpProjectSong.isPlaying && sbProjectSeek.progress < sbProjectSeek.max) {
//                    sbProjectSeek.progress += updateAmount
//                } else if (sbProjectSeek.progress == sbProjectSeek.max) {
//                    Toast.makeText(this@PlayProjectActivity, "Song over!", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }

    override fun onStop() {
        super.onStop()
        stopSong()
        projectPlayTimer.cancel()
    }
}
