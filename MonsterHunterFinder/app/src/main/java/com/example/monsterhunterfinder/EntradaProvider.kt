package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

class EntradaProvider {
    var listaEntradas: MutableList<Entrada> = mutableListOf()

    // Recojo la info de Firestore y la meto en esa lista
    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var entrada: Entrada

        for (documento in resultado) {
            entrada = documento.toObject(Entrada::class.java)
            entrada.numEntrada = documento.id.toInt()
            listaEntradas.add(entrada)
        }
    }

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