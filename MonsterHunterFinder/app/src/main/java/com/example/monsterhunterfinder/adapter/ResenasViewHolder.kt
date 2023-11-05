package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Resena
import com.example.monsterhunterfinder.databinding.ItemResenaBinding

class ResenasViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemResenaBinding.bind(view)

    fun render(resena: Resena) {
        binding.textoCazador.text = resena.cazador
        binding.textoResena.text = resena.experiencia
    }
}