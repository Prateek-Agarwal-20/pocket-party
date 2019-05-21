package com.example.pocketparty.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LightingCue(var cueName: String, var track: Track, var cueArtistName: String, var cueArtistImage: String,
                       var numPhones: Int, var cueList: List<List<LightingCueItem>>):Parcelable