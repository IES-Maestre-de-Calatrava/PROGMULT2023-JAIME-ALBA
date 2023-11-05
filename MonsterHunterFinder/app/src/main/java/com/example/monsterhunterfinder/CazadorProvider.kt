package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

class CazadorProvider {
    var cazadoresList: MutableList<Cazador> = mutableListOf()

    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var cazador: Cazador

        for (documento in resultado) {
            cazador = documento.toObject(Cazador::class.java)
            cazador.id = documento.id.toInt()
            cazadoresList.add(cazador)
        }
    }
}