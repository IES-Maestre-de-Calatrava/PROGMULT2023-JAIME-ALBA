package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

/**
 * Clase encargada de rellenar la lista con los datos deseados,
 * de "proveer" de datos a la lista.
 * @author Jaime
 */
class EntradaProvider {
    var listaEntradas: MutableList<Entrada> = mutableListOf()

    /**
     * Carga la MutableList con los datos que provienen de FireStore
     * @param resultado: resultado obtenido tras lanzar la query
     * (consulta) a la Firestore
     */
    // Recojo la info de Firestore y la meto en esa lista
    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var entrada: Entrada

        for (documento in resultado) {
            entrada = documento.toObject(Entrada::class.java)
            entrada.numEntrada = documento.id.toInt()
            listaEntradas.add(entrada)
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
        if(!listaEntradas.isEmpty()) {
            for (entrada in listaEntradas) {
                if (entrada.numEntrada == posicion) {
                    posicion = posicion + 1
                }else{
                    break
                }
            }
        }
        return posicion
    }
}