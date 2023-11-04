package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Entrada
import com.example.monsterhunterfinder.R

class EntradasAdapter (
    private val listaEntradas: MutableList<Entrada>,
    private val borrarRegistro:(Int) -> Unit,
    private val lanzarActivityModificar:(Int, Entrada) -> Unit
): RecyclerView.Adapter<EntradasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EntradasViewHolder(layoutInflater.inflate(R.layout.item_entrada, parent, false))
    }

    override fun onBindViewHolder(entradasViewHolder: EntradasViewHolder, position: Int) {
        val entrada = listaEntradas[position]
        entradasViewHolder.render(entrada, borrarRegistro, lanzarActivityModificar)
    }

    override fun getItemCount(): Int {
        return listaEntradas.size
    }

}