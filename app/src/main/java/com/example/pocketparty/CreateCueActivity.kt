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
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.example.pocketparty.data.FireInterface
import com.example.pocketparty.data.LightingCue
import com.example.pocketparty.data.LightingCueItem
import com.example.pocketparty.data.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_create_cue.*
import kotlinx.android.synthetic.main.list_single_cue.view.*
import java.util.*
import kotlin.collections.ArrayList

class CreateCueActivity : AppCompatActivity() {

    companion object {
        val LISTKEY = "LISTKEY"
    }

    private lateinit var cameraManager: CameraManager
    private lateinit var camId: String
    private lateinit var mediaPlayer: MediaPlayer
    private var updateAmount = 0
    private val lightingCues = ArrayList<LightingCueItem>()
    private val musicTimer = Timer()
    private var currentCueIndex = 0
    private var currentCue = LightingCueItem(0, 0)
    private var songChosen: Int = 0
    private lateinit var trackChosen: Track
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cue)

        //songChosen = intent.getIntExtra("SONG_ID", 0)
        trackChosen = intent.getParcelableExtra("TRACK_CHOSEN")
        songChosen = getSongId(trackChosen.name)
        user = FirebaseAuth.getInstance().currentUser!!

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
        setUpMediaPlayer()
        setupProgressBar()
    }

    private fun setUpMediaPlayer(){
        //Todo - prateek update for spotify
        mediaPlayer = MediaPlayer.create(this, songChosen)
    }

    private fun playSong(){
        //Todo - Prateek, update for spotify
        mediaPlayer.start()
        Log.i("PLAYCHECK", "check in play method: ${mediaPlayer.isPlaying}")
    }

    private fun pauseSong(){
        //Todo - Prateek, update for spotyify
        mediaPlayer.pause()
    }

    private fun stopSong(){
        //Todo - Prateek, update for spotyify
        mediaPlayer.stop()
    }

    private fun seekSongTo(progress: Int){
        //Todo - Prateek, update for spotyify
        mediaPlayer.seekTo(progress)
    }

    private fun songIsPlaying(): Boolean{
        //Todo - Prateek, update for spotyify
        return mediaPlayer.isPlaying
    }

    private fun getSongCurrentPosition(): Int{
        //Todo - Prateek, update for spotyify
        return mediaPlayer.currentPosition
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

    fun getSongId(trackName: String) : Int {
        when(trackName) {
            "All The Way Up" -> return R.raw.AllTheWayUp
            "Coming Over" -> return R.raw.ComingOver
            "Feel It Still" -> return R.raw.fellitstill
            "Fly Kicks" -> return R.raw.FlyKicks
            "Ignition" -> return R.raw.Ignition
            "Light" -> return R.raw.Light
            "Never Be Like You" -> return R.raw.NeverBeLikeYou
            "Turn Down For What" -> return R.raw.TurnDownForWhat
            "Willy Wonka Remix" -> return R.raw.willywonkaremix
            else -> return 0
        }
    }

    fun setupProgressBar() {
        getSongDuration()
        musicTimer.scheduleAtFixedRate(musicTimerTask(), 0, updateAmount.toLong())
        setSeekListener()
    }

    private fun getSongDuration(){
        //Todo - Prateek, update for spotify
        val duration = mediaPlayer.duration
        sbSongSeek.max = duration
        updateAmount = duration/100
    }

    private fun setSeekListener() {
        sbSongSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Todo - Prateek, update for spotify
                pauseSong()
                btnPlay.setImageResource(R.drawable.ic_play_arrow_vec)
                btnFlash.isEnabled = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Todo - Prateek, update for spotify
                seekSongTo(sbSongSeek.progress)
                playSong()
                btnPlay.setImageResource(R.drawable.ic_pause)
                btnFlash.isEnabled = true

                if (getSongCurrentPosition() < currentCue.startTime) {
                    handleBackSeek()
                }
            }

        })
    }

    private fun handleBackSeek() {
        pauseSong()
        setButtonAbility(false)
        Log.i("TAG", "back seek begin")
        Log.i("TAG", "first ${!lightingCues.get(currentCueIndex - 1).equals(null)}")
        Log.i("TAG", "sceond ${lightingCues.get(currentCueIndex - 1).startTime > getSongCurrentPosition()}")
        while (currentCueIndex > 0 && lightingCues.get(currentCueIndex - 1).startTime > getSongCurrentPosition()) {
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


        playSong()
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
        lightingCues.add(LightingCueItem(getSongCurrentPosition(), 0))
        currentCue = lightingCues.get(currentCueIndex)
        sbSongSeek.isEnabled = false
    }

    private fun handleFlashButtonUp() {
        cameraManager.setTorchMode(camId, false)
        btnFlash.isPressed = false
        currentCue.endTime = getSongCurrentPosition()
        sbSongSeek.isEnabled = true
        currentCueIndex++
        Log.i("TAG", "cueindex: ${currentCueIndex}")
    }

    fun playButtonClick(view: View) {
        if (songIsPlaying()) {
            pauseSong()
            btnPlay.setImageResource(R.drawable.ic_play_arrow_vec)
        } else {
            playSong()
            btnPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    fun leftSeekClick(view: View) {
        sbSongSeek.setProgress(0)
        seekSongTo(0)
        deleteAllCues()
    }

    fun rightSeekClick(view: View) {
        sbSongSeek.setProgress(sbSongSeek.max)
        seekSongTo(sbSongSeek.max)
    }

    fun saveButtonClick(view: View) {
        val cueName = view.cueName.text.toString()
        val cueArtistName = user.displayName
        var cues = ArrayList<List<LightingCueItem>>()
        cues.add(lightingCues)
        val cue = LightingCue(cueName, trackChosen, cueArtistName!!,
                        "", 1, cues)

        FireInterface(this, user!!.uid).saveProject(cue)

        Intent(this@CreateCueActivity, MainActivity::class.java)
    }

    private fun deleteAllCues() {
        lightingCues.clear()
        currentCueIndex = 0
    }

    inner class musicTimerTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                Log.i("AMOUNTCHECK", "seek prog = ${sbSongSeek.progress} / max: = ${sbSongSeek.max}")
                Log.i("PLAYCHECK", "playing: ${songIsPlaying()}")
                if (songIsPlaying() && sbSongSeek.progress < sbSongSeek.max) {
                    Log.i("AMOUNTCHECK","updateAmount = ${updateAmount}")
                    sbSongSeek.progress += updateAmount
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        musicTimer.cancel()
        stopSong()
    }
}
