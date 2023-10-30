package com.example.recycleview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleview.Producto
import com.example.recycleview.R

class ProductosAdapter (
    // Es el encargado de dibujar toda la lista, por lo
    // que VA A TENER QUE RECIBIR una lista.
    private val productosList: MutableList<Producto>): RecyclerView.Adapter<ProductosViewHolder>() {

    // Le doy a que SÍ implemente los tres métodos que me
    // ofrece, por heredar de la clase de la que hereda.


    // Este método coge el layout pequeñito y lo
    // infla para crear objetos.

    // En este método  SÓLO construye el layout.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ProductosViewHolder(layoutInflater.inflate(R.layout.item_producto, parent, false))
        // Coge el layout que le estoy pasando y lo infla, convirtiéndolo a objetos,
        // y se lo pasa al ProductosViewHolder para que tenga el layout con objetos
        // y pueda hacer el bind cuando lo necesite.
    }


    // Ahora voy llamando al Render, para que me vaya
    // construyendo uno a uno los objetos.
    // Le va pasando datos para los objetos y se los asigna
    // a la pantallita.
    override fun onBindViewHolder(productosViewHolder: ProductosViewHolder, position: Int) {
        // Cojo el elemento de la lista que ocupa la posición que quiero
        val producto = productosList[position]
        // Coge el producto que ocupa una determinada posición
        // y llama al render para que lo muestre por pantalla
        productosViewHolder.render(producto)
    }


    // Cambio el getItemCount y lo pongo al final.
    // Este método solamente determina el número de elementos
    // que se van a mostrar por pantalla.
    override fun getItemCount(): Int {
        return productosList.size
    }

    // TRAS ÉSTO, ME CREO OTRA CLASE.
    // En la misma carpeta de com.example.recycleview, FUERA
    // DE ADAPTER, me creo una clase Kotlin ProductoProvider.
    // Me voy a ella.

}




