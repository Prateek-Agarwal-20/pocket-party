package com.example.pocketparty.data

import org.parceler.Parcel
import java.io.Serializable

@Parcel
data class LightingCueItem(var startTime: Int, var endTime: Int): Serializable