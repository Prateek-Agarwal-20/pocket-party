package com.example.pocketparty.data

data class Actions(val disallows: Disallows?)

data class Album(val album_type: String?, val external_urls: External_urls?, val href: String?, val id: String?, val images: List<Images725528831>?, val name: String?, val type: String?, val uri: String?)

data class Artists1780310871(val external_urls: External_urls?, val href: String?, val id: String?, val name: String?, val type: String?, val uri: String?)

data class CurrentlyPlaying(val context: Context?, val timestamp: Number?, val progress_ms: Number?, val is_playing: Boolean?, val currently_playing_type: String?, val actions: Actions?, val item: Item?)

data class Context(val external_urls: External_urls?, val href: String?, val type: String?, val uri: String?)

data class Disallows(val resuming: Boolean?)

data class External_ids(val isrc: String?)

data class External_urls(val spotify: String?)

data class Images725528831(val height: Number?, val url: String?, val width: Number?)

data class Item(val album: Album?, val artists: List<Artists1780310871>?, val available_markets: List<String>?, val disc_number: Number?, val duration_ms: Number?, val explicit: Boolean?, val external_ids: External_ids?, val external_urls: External_urls?, val href: String?, val id: String?, val name: String?, val popularity: Number?, val preview_url: String?, val track_number: Number?, val type: String?, val uri: String?)