package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Multimedia
import com.example.monsterhunterfinder.databinding.ItemMultimediaBinding
import com.bumptech.glide.Glide

/**
 *  Clase encargada de ubicar los datos de un elemento
 *  individual (aquellos que aparecerán listados en la
 *  RecyclerView). Extiende de RecyclerView.ViewHolder.
 *
 * @param view Layout inflado para poder bindearlo (enlazarlo)
 * y ubicar los datos en las views de que disponga
 * @author Jaime
 */
class MultimediasViewHolder(view: View):  RecyclerView.ViewHolder(view) {
    val binding = ItemMultimediaBinding.bind(view)

    /**
     * Método que renderizará cada elemento del RecyclerView,
     * asignando los datos de cada elemento a las views presentes
     * en el layout que se esté utilizando.
     *
     * @param multimedia La multimedia a mostrar
     */
    fun render(multimedia: Multimedia) {
        Glide.with(binding.multimedia.context)
            .load(multimedia.enlace)
            .into(binding.multimedia)
    }
}