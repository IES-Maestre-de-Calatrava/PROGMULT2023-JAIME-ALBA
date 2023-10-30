package com.example.recycleview

// Me creo una data class con los datos que van a tener
// los objetos Producto.

data class Producto (
    var id: Int = 0,
    val nombre: String="",
    val descripcion: String="",
    val foto: String=""
)

