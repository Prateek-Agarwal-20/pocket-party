package com.example.pocketparty

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.pocketparty.data.SpotifyAppRemoteSingleton
import com.example.pocketparty.image.CircleTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var spotifyAccessToken: String? = null
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var profile_image_url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                // TODO: implement search here with `query` variable
            }
        }

        val i = getIntent()
        if(intent.extras != null) {
            user = i.getParcelableExtra("FIREBASE_USER")
            spotifyAccessToken = i.getStringExtra("SPOTIFY_ACCESS_TOKEN")
            mSpotifyAppRemote = SpotifyAppRemoteSingleton.spotifyAppRemote
            profile_image_url = i.getStringExtra("SPOTIFY_USER_IMG")
        }
        auth = FirebaseAuth.getInstance()

        searchBtn.setOnClickListener {
            onSearchRequested()
        }

        val toggle = object:ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Picasso.get().load(profile_image_url).transform(CircleTransform()).into(profPic);
            }
        }

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_account -> {
                // Handle the account page action
            }
            R.id.nav_listen -> {
                val fbTestIntent = Intent(this@MainActivity, FirebaseTestingActivity::class.java)
                fbTestIntent.putExtra("userID", user?.uid)
                startActivity(fbTestIntent)
            }
            R.id.nav_create -> {
                initiateCreatePipeline()
            }
            R.id.nav_edit -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun initiateCreatePipeline() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("spotify:")
        intent.putExtra(
            Intent.EXTRA_REFERRER,
            Uri.parse("android-app://" + this.getPackageName())
        )
        startActivity(intent)

        // Make sure the player is paused
        mSpotifyAppRemote!!.playerApi.pause()
        var trackSelected:Track? = null

        mSpotifyAppRemote!!.getPlayerApi().subscribeToPlayerState().setEventCallback { playerState ->
            val track = playerState.track
            if (track != null) {
                Log.d("TRACK", track.name + " by " + track.artist.name);
                trackSelected = track
                Thread.sleep(2000)
                // NOTE: This just takes the currently playing track as the chosen track. Need to implement Spotify
                // search later. some ideas for how to do it once SDK is working:
                // - subscribe to player state. once the player state track selected changes, use that track
                // - manually poll track state every 500 milliseconds for track changes
                // - add some sort of floating button on top of spotify that you can press when you're done choosing
                // - wait for the player to start playing. Once it's playing, take the currently playing song
                goToCreateCue(trackSelected)
            }
        }
    }

    private fun goToCreateCue(t: Track?){
        val i = Intent(this@MainActivity, CreateCueActivity::class.java)
        i.putExtra("TRACK_NAME", t?.name)
        i.putExtra("TRACK_URI", t?.uri)
        i.putExtra("SPOTIFY_ACCESS_TOKEN", spotifyAccessToken)
        startActivity(i)
    }

}
