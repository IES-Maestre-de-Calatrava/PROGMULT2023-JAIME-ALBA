package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

/**
 * Clase encargada de rellenar la lista con los datos deseados,
 * de "proveer" de datos a la lista.
 * @author Jaime
 */
class ResenaProvider {
    var resenasList: MutableList<Resena> = mutableListOf()

    /**
     * Carga la MutableList con los datos que provienen de FireStore
     * @param resultado: resultado obtenido tras lanzar la query
     * (consulta) a la Firestore
     */
    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var resena: Resena
        for (documento in resultado) {
            resena = documento.toObject(Resena::class.java)
            resena.id = documento.id.toInt()
            resenasList.add(resena)
        }
    }
}