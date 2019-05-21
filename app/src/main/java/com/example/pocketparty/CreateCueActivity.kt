package com.example.pocketparty

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.example.pocketparty.data.*
import com.example.pocketparty.network.SpotifyAPI
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.activity_create_cue.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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
    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private lateinit var track_name: String
    private lateinit var track_uri: String
    private lateinit var spotifyAccessToken: String
    private var BASE_URL: String = "https://api.spotify.com/"
    private lateinit var spotifyAPI: SpotifyAPI
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cue)

        val i = getIntent()
        if(intent.extras != null) {
            track_name = i.getStringExtra("TRACK_NAME")
            track_uri = i.getStringExtra("TRACK_URI")
            spotifyAccessToken = i.getStringExtra("SPOTIFY_ACCESS_TOKEN")
            mSpotifyAppRemote = SpotifyAppRemoteSingleton.spotifyAppRemote
        }

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

        mSpotifyAppRemote = SpotifyAppRemoteSingleton.spotifyAppRemote
        setupNetworking()
    }

    private fun setUpMediaPlayer(){
        //Todo - prateek update for spotify
        mpWillyWonka = MediaPlayer.create(this, R.raw.willywonkaremix)
    }

    private fun playSong(){
        //mpWillyWonka.start()
        Log.i("PLAYCHECK", "check in play method: ${songIsPlaying()}")
        Log.i("PLAYCHECK", "track uri: " + track_uri)

//        val playCall = spotifyAPI.playFromSpotify()
//        playCall.enqueue(object: Callback<PlayResponse> {
//            override fun onFailure(call: Call<PlayResponse>, t: Throwable) {
//                Log.d("PLAYERROR", t.message)
//            }
//
//            override fun onResponse(call: Call<PlayResponse>, response: Response<PlayResponse>) {
//                Log.d("PLAYED_SUCCESS", " WE DID IT")
//            }
//
//        })
        mSpotifyAppRemote!!.playerApi.resume()
        isPlaying = true
    }

    private fun pauseSong(){
        mSpotifyAppRemote!!.playerApi.pause()
        isPlaying = false
    }

    private fun stopSong(){
        mpWillyWonka.stop()
        isPlaying = false
    }

    private fun seekSongTo(progress: Int){
        //Todo - Prateek, update for spotyify
        mpWillyWonka.seekTo(progress)
    }

    private fun songIsPlaying(): Boolean{
//        return mpWillyWonka.isPlaying
        return isPlaying
    }

    private fun getSongCurrentPosition(): Int{
        //Todo - Prateek, update for spotyify
        return mpWillyWonka.currentPosition
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
        getSongDuration()
        //musicTimer.scheduleAtFixedRate(musicTimerTask(), 0, updateAmount.toLong())
        setSeekListener()
    }

    private fun setupNetworking() {
        val httpClient = OkHttpClient.Builder();
        httpClient.addInterceptor(Interceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .header("Authorization", "Bearer " + spotifyAccessToken)
                .method(original.method(), original.body())
                .build()

            chain.proceed(request)
        })

        val client = httpClient.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        spotifyAPI = retrofit.create(SpotifyAPI::class.java)
    }

    private fun getSongDuration(){
        //Todo - Prateek, update for spotify
        val duration = mpWillyWonka.duration
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

     fun handleFlashButtonDown() {
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
