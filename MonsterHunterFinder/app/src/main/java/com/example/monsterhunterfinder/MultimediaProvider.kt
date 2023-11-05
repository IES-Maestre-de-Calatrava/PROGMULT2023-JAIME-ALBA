package com.example.monsterhunterfinder

import com.google.firebase.firestore.QuerySnapshot

class MultimediaProvider {
    var multimediasList: MutableList<Multimedia> = mutableListOf()

    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var multimedia: Multimedia

        for (documento in resultado) {
            multimedia = documento.toObject(Multimedia::class.java)
            multimedia.id = documento.id.toInt()
            multimediasList.add(multimedia)
        }
    }
}