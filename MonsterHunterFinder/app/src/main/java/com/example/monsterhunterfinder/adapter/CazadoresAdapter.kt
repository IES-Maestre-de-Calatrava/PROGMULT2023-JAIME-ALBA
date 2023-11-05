package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Cazador
import com.example.monsterhunterfinder.R

class CazadoresAdapter(
    private val cazadoresList: MutableList<Cazador>
): RecyclerView.Adapter<CazadoresViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CazadoresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CazadoresViewHolder(layoutInflater.inflate(R.layout.item_cazador, parent, false))
    }

    override fun onBindViewHolder(cazadoresViewHolder: CazadoresViewHolder, position: Int) {
        val cazador = cazadoresList[position]
        cazadoresViewHolder.render(cazador)
    }

    override fun getItemCount(): Int {
        return cazadoresList.size
    }
}