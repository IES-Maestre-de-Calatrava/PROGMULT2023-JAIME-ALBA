package com.example.recycleview.adapter

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recycleview.MainActivity2
import com.example.recycleview.Producto
import com.example.recycleview.databinding.ItemProductoBinding

class ProductosViewHolder (view: View): RecyclerView.ViewHolder (view) {
    val binding = ItemProductoBinding.bind(view)

    // ANTES DE NADA, ME CREO LA CLASE PRODUCTO.

    // 02/11/2023
    // El render ya no va a recibir solamente el producto, sino también
    // una función (función lamba)
    fun render (
        producto: Producto,
        deleteRegister: (Int) -> Unit, //Lo del unit significa que no devuelve nada
        // Me voy al binding del botón de borrado
        updateRegister: (Producto) -> Unit
    ) {
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



        // 02/11/2023
        binding.imageViewProducto.setOnClickListener {
            Toast.makeText(
                binding.imageViewProducto.context,
                producto.nombre,
                Toast.LENGTH_SHORT
            ).show()
        }

        // Ahora que sea, en lugar de pulsar en la foto, pulsar
        // en cualquier parte del registro
        itemView.setOnClickListener{
            Toast.makeText(
                binding.imageViewProducto.context,
                producto.descripcion,
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.buttonEliminar.setOnClickListener {
            deleteRegister(adapterPosition)
        }
        // Tras hacer ésto, me voy al productosAdapter.


        binding.buttonEditar.setOnClickListener {
            updateRegister(producto)
        }
    }
    // Me voy a hacer una cosa para que haga algo al presionar en la imagen.
    // Lo meto dentro el render.
}