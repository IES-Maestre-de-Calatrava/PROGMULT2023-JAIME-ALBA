package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

/**
 * Clase encargada de rellenar la lista con los datos deseados,
 * de "proveer" de datos a la lista.
 * @author Jaime
 */
class MultimediaProvider {
    var multimediasList: MutableList<Multimedia> = mutableListOf()

    /**
     * Carga la MutableList con los datos que provienen de FireStore
     * @param resultado: resultado obtenido tras lanzar la query
     * (consulta) a la Firestore
     */
    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var multimedia: Multimedia

        multimediasList.clear()

        for (documento in resultado) {
            multimedia = documento.toObject(Multimedia::class.java)
            multimedia.id = documento.id.toInt()
            multimediasList.add(multimedia)
        }
    }


    /**
     * Método mediante el que es posible obtener automáticamente una
     * id numérica en una secuencia ascendente, rellenando huecos en
     * el caso de que sean encontrados.
     * Méritos del algoritmo a Miguel Peláez Duro.
     */
    fun getId(): Int {
        var posicion = 0
        if(!multimediasList.isEmpty()) {
            for (multimedia in multimediasList) {
                if (multimedia.id == posicion) {
                    posicion = posicion + 1
                }else{
                    break
                }
            }
        }
        return posicion
    }
}