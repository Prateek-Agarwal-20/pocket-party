package com.example.pocketparty.data

import android.os.Parcelable
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.parcel.Parcelize

object SpotifyAppRemoteSingleton  {
    lateinit var spotifyAppRemote : SpotifyAppRemote
}