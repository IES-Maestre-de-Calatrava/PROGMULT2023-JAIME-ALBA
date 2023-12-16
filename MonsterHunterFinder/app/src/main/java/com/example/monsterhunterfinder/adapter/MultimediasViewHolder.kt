package com.example.monsterhunterfinder.adapter

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Multimedia
import com.example.monsterhunterfinder.databinding.ItemMultimediaBinding
import com.bumptech.glide.Glide
import com.example.monsterhunterfinder.R
import com.example.monsterhunterfinder.activity_audio
import com.example.monsterhunterfinder.activity_video

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


        // Se asigna una acción al pulsar en el elemento completo
        itemView.setOnClickListener{
            Toast.makeText(
                binding.multimedia.context,
                R.string.textoGaleria,
                Toast.LENGTH_LONG
            ).show()

            val contexto = itemView.context
            val intent = Intent(contexto, activity_video::class.java)

            val identificador: String = multimedia.id.toString()
            intent.putExtra("Identificador", identificador)

            contexto.startActivity(intent)
        }
    }
}