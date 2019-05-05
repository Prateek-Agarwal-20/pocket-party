package com.example.pocketparty.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LightingCueItem(var startTime: Int, var endTime: Int): Parcelable