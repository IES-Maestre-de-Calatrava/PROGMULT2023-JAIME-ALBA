package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

/**
 * Clase encargada de rellenar la lista con los datos deseados,
 * de "proveer" de datos a la lista.
 * @author Jaime
 */
class CazadorProvider {
    var cazadoresList: MutableList<Cazador> = mutableListOf()

    /**
     * Carga la MutableList con los datos que provienen de FireStore
     * @param resultado: resultado obtenido tras lanzar la query
     * (consulta) a la Firestore
     */
    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var cazador: Cazador

        for (documento in resultado) {
            cazador = documento.toObject(Cazador::class.java)
            cazador.id = documento.id.toInt()
            cazadoresList.add(cazador)
        }
    }
}