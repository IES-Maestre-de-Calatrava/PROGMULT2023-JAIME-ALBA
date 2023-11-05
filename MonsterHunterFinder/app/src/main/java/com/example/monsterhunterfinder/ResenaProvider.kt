package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

class ResenaProvider {
    var resenasList: MutableList<Resena> = mutableListOf()

    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var resena: Resena
        for (documento in resultado) {
            resena = documento.toObject(Resena::class.java)
            resena.id = documento.id.toInt()
            resenasList.add(resena)
        }
    }
}