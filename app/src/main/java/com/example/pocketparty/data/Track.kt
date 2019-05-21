package com.example.pocketparty.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(var name: String, var artist: String, var imageLink: String, var spotifyUri: String): Parcelable