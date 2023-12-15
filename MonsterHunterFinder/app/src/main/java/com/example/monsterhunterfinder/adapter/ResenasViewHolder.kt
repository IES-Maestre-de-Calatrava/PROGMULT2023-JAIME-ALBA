package com.example.monsterhunterfinder.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.R
import com.example.monsterhunterfinder.Resena
import com.example.monsterhunterfinder.activity_audio
import com.example.monsterhunterfinder.activity_diario_ver
import com.example.monsterhunterfinder.databinding.ItemResenaBinding

/**
 *  Clase encargada de ubicar los datos de un elemento
 *  individual (aquellos que aparecerán listados en la
 *  RecyclerView). Extiende de RecyclerView.ViewHolder.
 *
 * @param view Layout inflado para poder bindearlo (enlazarlo)
 * y ubicar los datos en las views de que disponga
 * @author Jaime
 */
class ResenasViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemResenaBinding.bind(view)

    /**
     * Método que renderizará cada elemento del RecyclerView,
     * asignando los datos de cada elemento a las views presentes
     * en el layout que se esté utilizando.
     *
     * @param resena La reseña a mostrar
     */
    fun render(resena: Resena) {
        binding.textoCazador.text = resena.cazador
        binding.textoResena.text = resena.experiencia

        // Al pulsar en un elemento reseña, se abrirá la activity de reproducción
        // de audios con el audio correspondiente
        itemView.setOnClickListener{
            val contexto = itemView.context
            val intent = Intent(contexto, activity_audio::class.java)

            val identificador: String = resena.nombreAudio
            intent.putExtra("Identificador", identificador)

            contexto.startActivity(intent)
        }
    }
}