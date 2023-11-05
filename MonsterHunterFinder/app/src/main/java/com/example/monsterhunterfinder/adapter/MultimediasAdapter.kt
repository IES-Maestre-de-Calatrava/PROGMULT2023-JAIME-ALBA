package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Multimedia
import com.example.monsterhunterfinder.R

class MultimediasAdapter(
    private val multimediasList: MutableList<Multimedia>
    ): RecyclerView.Adapter<MultimediasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultimediasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MultimediasViewHolder(layoutInflater.inflate(R.layout.item_multimedia, parent, false))
    }

    override fun onBindViewHolder(multimediasViewHolder: MultimediasViewHolder, position: Int) {
        val multimedia = multimediasList[position]
        multimediasViewHolder.render(multimedia)
    }

    override fun getItemCount(): Int {
        return multimediasList.size
    }

}