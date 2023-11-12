package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.R
import com.example.monsterhunterfinder.Resena

/**
 * Clase que recibe una lista de reseñas a mostrar, encargándose de mostrar
 * los datos y gestionarlas.
 *
 * Extiende de RecyclerView.Adapter, por lo que será necesario implementar los
 * métodos que herede de dicha clase.
 *
 *  El constructor tiene como propiedades:
 *  @property resenasList Lista mutable de reseñas a mostrar: es mutable para
 *  que su contenido pueda ser modificado
 *
 *  @return Devuelve un RecyclerView.Adapter con el ViewHolder personalizado
 *
 *  @author Jaime
 */
class ResenasAdapter(
    private val resenasList: MutableList<Resena>
): RecyclerView.Adapter<ResenasViewHolder>() {

    /**
     * Método que realiza el inflado de cada layout individual de la RecyclerView.
     * Aún no se asignan los datos.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ResenasViewHolder(layoutInflater.inflate(R.layout.item_resena, parent, false))
    }

    /**
     * Método que realiza la asignación de datos al layout inflado.
     *
     * @param resenasViewHolder La clase que lleva a cabo el renderizado
     * @param position La posición del elemento que está renderizando
     */
    override fun onBindViewHolder(resenasViewHolder: ResenasViewHolder, position: Int) {
        val resena = resenasList[position]
        resenasViewHolder.render(resena)
    }

    /**
     * Método que devuelve el número de elementos que hay en la lista.
     *
     * @return Tamaño (número de elementos) de la lista que se está
     * manejando
     */
    override fun getItemCount(): Int {
        return resenasList.size
    }
}