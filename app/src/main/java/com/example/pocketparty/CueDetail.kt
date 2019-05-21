package com.example.pocketparty

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.example.pocketparty.data.LightingCue

import kotlinx.android.synthetic.main.activity_cue_detail.*

class CueDetail : AppCompatActivity() {
    private lateinit var lightingCue: LightingCue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cue_detail)
        setSupportActionBar(toolbar)

        lightingCue = intent.getParcelableExtra<LightingCue>("LIGHTING_CUE")
    }

}
