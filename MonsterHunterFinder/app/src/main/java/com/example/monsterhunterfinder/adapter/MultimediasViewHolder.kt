package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Multimedia
import com.example.monsterhunterfinder.databinding.ItemMultimediaBinding
import com.bumptech.glide.Glide

class MultimediasViewHolder(view: View):  RecyclerView.ViewHolder(view) {
    val binding = ItemMultimediaBinding.bind(view)


    fun render(multimedia: Multimedia) {
        Glide.with(binding.multimedia.context)
            .load(multimedia.enlace)
            .into(binding.multimedia)
    }
}