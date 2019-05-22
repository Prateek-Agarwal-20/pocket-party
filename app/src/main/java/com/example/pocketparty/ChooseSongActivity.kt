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
            i.putExtra("TRACK_CHOSEN", Track(allTheWayUpBtn.text.toString(), "Fat Joe", "", ""))
            startActivity(i)
        }

        comingOverBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(comingOverBtn.text.toString(), "Dillon Francis", "", ""))
            startActivity(i)
        }

        feelItStillBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(feelItStillBtn.text.toString(), "Portugal. The Man", "", ""))
            startActivity(i)
        }

        flyKicksBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(flyKicksBtn.text.toString(), "AC Slater", "", ""))
            startActivity(i)
        }

        ignitionBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(ignitionBtn.text.toString(), "R Kelly", "", ""))
            startActivity(i)
        }

        lightBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(lightBtn.text.toString(), "San Holo", "", ""))
            startActivity(i)
        }

        neverBeLikeYouBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(neverBeLikeYouBtn.text.toString(), "Flume", "", ""))
            startActivity(i)
        }

        turnDownForWhatBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(turnDownForWhatBtn.text.toString(), "DJ Snake", "", ""))
            startActivity(i)
        }

        willyWonkaRemixBtn.setOnClickListener { view ->
            i.putExtra("TRACK_CHOSEN", Track(willyWonkaRemixBtn.text.toString(), "Dotan Negrin", "", ""))
            startActivity(i)
        }
    }

}
