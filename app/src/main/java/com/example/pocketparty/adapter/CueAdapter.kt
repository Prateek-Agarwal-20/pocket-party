package com.example.pocketparty.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketparty.CueDetail
import com.example.pocketparty.MainActivity
import com.example.pocketparty.R
import com.example.pocketparty.data.LightingCue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_single_cue.view.*

class CueAdapter: RecyclerView.Adapter<CueAdapter.ViewHolder> {
    var cues = mutableListOf<LightingCue>()

    private val context: Context

    constructor(context: Context, listItems: List<LightingCue>) : super() {
        this.context = context
        cues.addAll(listItems)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
            R.layout.list_single_cue, viewGroup, false
        )

        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return cues.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item  = cues.get(position)

        viewHolder.trackName.text = item.track.name
        viewHolder.artistName.text = item.track.artist
        Picasso.get()
            .load(item.track.imageLink)
            .placeholder(context.getDrawable(R.drawable.music_note))
            .error(context.getDrawable(R.drawable.music_note))
            .into(viewHolder.trackImage)
        Picasso.get()
            .load(item.cueArtistImage)
            .placeholder(context.getDrawable(R.drawable.default_user))
            .error(context.getDrawable(R.drawable.default_user))
            .into(viewHolder.cueArtistImage)
        viewHolder.numPhones.text = "x" + item.numPhones
        viewHolder.cueArtistName.text = item.cueArtistName
        viewHolder.cueName.text = item.cueName

        viewHolder.cardView.setOnClickListener {
            var i = Intent(context as MainActivity, CueDetail::class.java)
            i.putExtra("LIGHTING_CUE", item )
            context.startActivity(i)
        }
    }

    fun addCue(item: LightingCue) {
        cues.add(0, item)
        notifyItemInserted(0)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var artistName = itemView.artistName
        var trackName = itemView.trackName
        var trackImage = itemView.trackImage
        var cardView = itemView.card_view
        // var lightingCues?
        var numPhones = itemView.numPhones
        var cueArtistName = itemView.cueArtistName
        var cueArtistImage = itemView.cueArtistImage
        var cueName = itemView.cueName
    }
}