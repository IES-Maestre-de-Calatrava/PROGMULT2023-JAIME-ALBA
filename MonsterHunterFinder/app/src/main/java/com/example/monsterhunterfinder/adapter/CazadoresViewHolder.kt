package com.example.monsterhunterfinder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Cazador
import com.example.monsterhunterfinder.databinding.ItemCazadorBinding

/**
 *  Clase encargada de ubicar los datos de un elemento
 *  individual (aquellos que aparecerán listados en la
 *  RecyclerView). Extiende de RecyclerView.ViewHolder.
 *
 * @param view Layout inflado para poder bindearlo (enlazarlo)
 * y ubicar los datos en las views de que disponga
 * @author Jaime
 */
class CazadoresViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var binding = ItemCazadorBinding.bind(view)

    /**
     * Método que renderizará cada elemento del RecyclerView,
     * asignando los datos de cada elemento a las views presentes
     * en el layout que se esté utilizando.
     *
     * @param cazador El cazador a mostrar
     */
    fun render(cazador: Cazador) {
        binding.textoNombreCazador.text = cazador.nombre
        binding.textoBioCazador.text = cazador.bio
    }
}