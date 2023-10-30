package com.example.recycleview.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recycleview.Producto
import com.example.recycleview.databinding.ItemProductoBinding

class ProductosViewHolder (view: View): RecyclerView.ViewHolder (view) {
    val binding = ItemProductoBinding.bind(view)

    // ANTES DE NADA, ME CREO LA CLASE PRODUCTO.

    fun render (producto: Producto) {
        // Le vamos a dar los valores a cada uno de los
        // elementos de la pantalla.
        binding.textViewId.text = producto.id.toString()
        binding.textViewNombre.text = producto.nombre
        binding.textViewDescripcion.text = producto.descripcion
        // Tras importar la librería:
        Glide.with(binding.imageViewProducto.context)
            .load(producto.foto)
            .into(binding.imageViewProducto)


        // De momento la foto la dejamos sin poner, porque
        // hay que importar una librería externa.

        // Al render le paso un producto, UNO.
        // Si tengo una lista, le tengo que pasar el adapter
        // para que vaya renderizando todos.

        // Hace el binding, y una vez lo tiene, va
        // pasando información.

        // Tras eso, me voy a ProductosAdapter.
    }
}