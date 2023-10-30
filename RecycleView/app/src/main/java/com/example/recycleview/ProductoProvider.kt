package com.example.recycleview

import com.google.firebase.firestore.QuerySnapshot

class ProductoProvider {
    var productosList: MutableList<Producto> = mutableListOf()
    // AQUÍ voy a tener la lista con todos los
    // productos que quiero representar.


    // Tras eso, me creo un pequeño método que recoja toda
    // la información del Firestore y la meta en esa lista.

    // Al hacer una consulta, recibimos un QuerySnapshot.
    // Vamos leyendo elemento por elemento y metiéndolo
    // en la mutableList.
    fun actualizarLista(resultado: QuerySnapshot) {
        lateinit var producto: Producto

        for (documento in resultado) {
            // En lugar de traerme los campos de manera separada,
            // me los puedo traer todos juntos y meterlos en un
            // objeto.
            // Me creo una DataClass para meterlos dentro, de esa
            // manera me traigo toda la información en forma de objetos.
            producto = documento.toObject(Producto::class.java)
            // Todos los datos que me traigo de Firebase son Strings.
            // Si quiero asignarlo a la ID, tengo que castearlo a entero.
            producto.id = documento.id.toInt()
            productosList.add(producto)
        }
    }

    // TRAS ÉSTO.
    // Ir al MainActivity y enlazar todas estas clases.
}