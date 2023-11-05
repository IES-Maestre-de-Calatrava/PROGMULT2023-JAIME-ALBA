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