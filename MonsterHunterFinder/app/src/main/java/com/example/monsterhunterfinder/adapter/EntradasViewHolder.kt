package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Entrada
import com.example.monsterhunterfinder.databinding.ItemEntradaBinding

class EntradasViewHolder (view: View): RecyclerView.ViewHolder (view) {
    val binding = ItemEntradaBinding.bind(view)

    fun render(entrada: Entrada) {
        binding.textoListaId.text = entrada.numEntrada.toString()
        binding.textoListaArma.text = entrada.arma
        binding.textoListaTitulo.text = entrada.titulo
    }
}