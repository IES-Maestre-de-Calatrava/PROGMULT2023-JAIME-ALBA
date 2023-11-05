package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.R
import com.example.monsterhunterfinder.Resena

class ResenasAdapter(
    private val resenasList: MutableList<Resena>
): RecyclerView.Adapter<ResenasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ResenasViewHolder(layoutInflater.inflate(R.layout.item_resena, parent, false))
    }

    override fun onBindViewHolder(resenasViewHolder: ResenasViewHolder, position: Int) {
        val resena = resenasList[position]
        resenasViewHolder.render(resena)
    }

    override fun getItemCount(): Int {
        return resenasList.size
    }
}