package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Cazador
import com.example.monsterhunterfinder.databinding.ItemCazadorBinding

class CazadoresViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var binding = ItemCazadorBinding.bind(view)

    fun render(cazador: Cazador) {
        binding.textoNombreCazador.text = cazador.nombre
        binding.textoBioCazador.text = cazador.bio
    }
}