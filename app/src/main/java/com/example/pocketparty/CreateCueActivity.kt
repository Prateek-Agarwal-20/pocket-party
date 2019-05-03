package com.example.pocketparty

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_cue.*

class CreateCueActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var camId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cue)

        setup()
    }

    private fun setup(){
        setUpFlashParams()
        setupFlashOntouch()
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
}
