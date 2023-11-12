package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Cazador
import com.example.monsterhunterfinder.R

/**
 * Clase que recibe una lista de cazadores a mostrar, encargándose de mostrar
 * los datos y gestionarlos.
 *
 * Extiende de RecyclerView.Adapter, por lo que será necesario implementar los
 * métodos que herede de dicha clase.
 *
 *  El constructor tiene como propiedades:
 *  @property cazadoresList Lista mutable de cazadores a mostrar: es mutable para
 *  que su contenido pueda ser modificado
 *
 *  @return Devuelve un RecyclerView.Adapter con el ViewHolder personalizado
 *
 *  @author Jaime
 */
class CazadoresAdapter(
    private val cazadoresList: MutableList<Cazador>
): RecyclerView.Adapter<CazadoresViewHolder>() {

    /**
     * Método que realiza el inflado de cada layout individual de la RecyclerView.
     * Aún no se asignan los datos.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CazadoresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CazadoresViewHolder(layoutInflater.inflate(R.layout.item_cazador, parent, false))
    }

    /**
     * Método que realiza la asignación de datos al layout inflado.
     *
     * @param cazadoresViewHolder La clase que lleva a cabo el renderizado
     * @param position La posición del elemento que está renderizando
     */
    override fun onBindViewHolder(cazadoresViewHolder: CazadoresViewHolder, position: Int) {
        val cazador = cazadoresList[position]
        cazadoresViewHolder.render(cazador)
    }

    /**
     * Método que devuelve el número de elementos que hay en la lista.
     *
     * @return Tamaño (número de elementos) de la lista que se está
     * manejando
     */
    override fun getItemCount(): Int {
        return cazadoresList.size
    }
}