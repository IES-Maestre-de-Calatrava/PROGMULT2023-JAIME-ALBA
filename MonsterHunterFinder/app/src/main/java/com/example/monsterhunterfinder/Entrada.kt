package com.example.monsterhunterfinder

/**
 * Clase de datos que se utilizará en el diario del
 * cazador, actuando como registros o entradas de
 * dicho diario.
 * @author Jaime
 */
data class Entrada (
    var numEntrada: Int = 0,
    val titulo: String="",
    val arma: String="",
    val resumen: String=""
)