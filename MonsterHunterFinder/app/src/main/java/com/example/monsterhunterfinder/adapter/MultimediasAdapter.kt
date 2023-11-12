package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Multimedia
import com.example.monsterhunterfinder.R

/**
 * Clase que recibe una lista de multimedia a mostrar, encargándose de mostrar
 * los datos y gestionarla.
 *
 * Extiende de RecyclerView.Adapter, por lo que será necesario implementar los
 * métodos que herede de dicha clase.
 *
 *  El constructor tiene como propiedades:
 *  @property multimediasList Lista mutable de multimedia a mostrar: es mutable para
 *  que su contenido pueda ser modificado
 *
 *  @return Devuelve un RecyclerView.Adapter con el ViewHolder personalizado
 *
 *  @author Jaime
 */
class MultimediasAdapter(
    private val multimediasList: MutableList<Multimedia>
    ): RecyclerView.Adapter<MultimediasViewHolder>() {

    /**
     * Método que realiza el inflado de cada layout individual de la RecyclerView.
     * Aún no se asignan los datos.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultimediasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MultimediasViewHolder(layoutInflater.inflate(R.layout.item_multimedia, parent, false))
    }

    /**
     * Método que realiza la asignación de datos al layout inflado.
     *
     * @param multimediasViewHolder La clase que lleva a cabo el renderizado
     * @param position La posición del elemento que está renderizando
     */
    override fun onBindViewHolder(multimediasViewHolder: MultimediasViewHolder, position: Int) {
        val multimedia = multimediasList[position]
        multimediasViewHolder.render(multimedia)
    }

    /**
     * Método que devuelve el número de elementos que hay en la lista.
     *
     * @return Tamaño (número de elementos) de la lista que se está
     * manejando
     */
    override fun getItemCount(): Int {
        return multimediasList.size
    }

}