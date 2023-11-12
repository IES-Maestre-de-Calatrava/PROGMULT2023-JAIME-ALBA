package com.example.monsterhunterfinder

/**
 * Clase de datos empleada para representar
 * las reviews u opiniones que unos cazadores
 * dejan en el perfil de otros, a modo de
 * feedback.
 * @author Jaime
 */
data class Resena (
    var id: Int = 0,
    var cazador: String = "",
    var experiencia: String = ""
)