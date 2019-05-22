package com.example.pocketparty

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.example.pocketparty.data.Track

import kotlinx.android.synthetic.main.activity_choose_song.*
import kotlinx.android.synthetic.main.content_choose_song.*

class ChooseSongActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_song)
        setSupportActionBar(toolbar)

        val i = Intent(this@ChooseSongActivity, CreateCueActivity::class.java)

        allTheWayUpBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(allTheWayUpBtn.text.toString(), "Fat Joe", "", getSongId(allTheWayUpBtn.text.toString()).toString()))
            startActivity(i)
        }

        comingOverBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(comingOverBtn.text.toString(), "Dillon Francis", "", getSongId(comingOverBtn.text.toString()).toString()))
            startActivity(i)
        }

        feelItStillBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(feelItStillBtn.text.toString(), "Portugal. The Man", "", getSongId(feelItStillBtn.text.toString()).toString()))
            startActivity(i)
        }

        flyKicksBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(flyKicksBtn.text.toString(), "AC Slater", "", getSongId(flyKicksBtn.text.toString()).toString()))
            startActivity(i)
        }

        ignitionBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(ignitionBtn.text.toString(), "R Kelly", "", getSongId(ignitionBtn.text.toString()).toString()))
            startActivity(i)
        }

        lightBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(lightBtn.text.toString(), "San Holo", "", getSongId(lightBtn.text.toString()).toString()))
            startActivity(i)
        }

        neverBeLikeYouBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(neverBeLikeYouBtn.text.toString(), "Flume", "", getSongId(neverBeLikeYouBtn.text.toString()).toString()))
            startActivity(i)
        }

        turnDownForWhatBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(turnDownForWhatBtn.text.toString(), "DJ Snake", "", getSongId(turnDownForWhatBtn.text.toString()).toString()))
            startActivity(i)
        }

        willyWonkaRemixBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(willyWonkaRemixBtn.text.toString(), "Dotan Negrin", "", getSongId(willyWonkaRemixBtn.text.toString()).toString()))
            startActivity(i)
        }
    }

    fun getSongId(trackName: String) : Int {
        when(trackName) {
            "All The Way Up" -> return R.raw.allthewayup
            "Coming Over" -> return R.raw.comingover
            "Feel It Still" -> return R.raw.fellitstill
            "Fly Kicks" -> return R.raw.flykicks
            "Ignition" -> return R.raw.ignition
            "Light" -> return R.raw.light
            "Never Be Like You" -> return R.raw.neverbelikeyou
            "Turn Down For What" -> return R.raw.turndownforwhat
            "Willy Wonka Remix" -> return R.raw.willywonkaremix
            else -> return 0
        }
    }

}
