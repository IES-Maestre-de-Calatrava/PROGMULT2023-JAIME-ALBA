package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Resena
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
    }
}