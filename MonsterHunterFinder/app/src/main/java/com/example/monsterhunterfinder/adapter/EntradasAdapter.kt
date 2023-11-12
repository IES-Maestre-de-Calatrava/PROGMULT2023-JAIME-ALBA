package com.example.monsterhunterfinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Entrada
import com.example.monsterhunterfinder.R

/**
 *  Clase que recibe una lista de entradas a mostrar, encargándose de mostrar
 *  los datos y gestionarlos.
 *
 *  Extiende de RecyclerView.Adapter, por lo que será necesario implementar los
 *  métodos que herede de dicha clase.
 *
 *  El constructor tiene como propiedades:
 *  @property listaEntradas Lista mutable de entradas a mostrar: es mutable para
 *  que su contenido pueda ser modificado
 *  @property borrarRegistro Método para eliminar registros; su implementación se
 *  encuentra en activity_diario_vista
 *  @property lanzarActivityModificar Método para abrir actividad del detalle; su
 *  implementación se encuentra en activity_diario_vista
 *
 *  @return Devuelve un RecyclerView.Adapter con el ViewHolder personalizado
 *
 *  @author Jaime
 */
class EntradasAdapter (
    private val listaEntradas: MutableList<Entrada>,
    private val borrarRegistro:(Int, Int) -> Unit,
    private val lanzarActivityModificar:(Int, Entrada) -> Unit
): RecyclerView.Adapter<EntradasViewHolder>() {

    /**
     * Método que realiza el inflado de cada layout individual de la RecyclerView.
     * Aún no se asignan los datos.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EntradasViewHolder(layoutInflater.inflate(R.layout.item_entrada, parent, false))
    }

    /**
     * Método que realiza la asignación de datos al layout inflado.
     *
     * @param entradasViewHolder La clase que lleva a cabo el renderizado
     * @param position La posición del elemento que está renderizando
     */
    override fun onBindViewHolder(entradasViewHolder: EntradasViewHolder, position: Int) {
        val entrada = listaEntradas[position]
        entradasViewHolder.render(entrada, borrarRegistro, lanzarActivityModificar)
    }

    /**
     * Método que devuelve el número de elementos que hay en la lista.
     *
     * @return Tamaño (número de elementos) de la lista que se está
     * manejando
     */
    override fun getItemCount(): Int {
        return listaEntradas.size
    }

}