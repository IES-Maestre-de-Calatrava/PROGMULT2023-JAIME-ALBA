package com.example.monsterhunterfinder

/**
 * Clase de datos a utilizar en la galería ubicada
 * en el perfil de cada usuario; lo único interesante
 * que contienen es el enlace por el que se accede a
 * ellas, mediante el cual es posible visualizarlas.
 * @author Jaime
 */
data class Multimedia (
    var id: Int = 0,
    var enlace: String = ""
)