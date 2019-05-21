package com.example.pocketparty.data


data class CurrentPlayerContext(val timestamp: Number?, val device: Device?, val progress_ms: String?, val is_playing: Boolean?, val currently_playing_type: String?, val actions: Actions?, val item: Item?, val shuffle_state: Boolean?, val repeat_state: String?, val context: Context?)

data class Device(val id: String?, val is_active: Boolean?, val is_restricted: Boolean?, val name: String?, val type: String?, val volume_percent: Number?)