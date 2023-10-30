package com.break4learning.LisConRecyclerViewCloudFirestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.recycleview.databinding.ActivityMain2Binding
import com.bumptech.glide.Glide

/**
 * Esta clase permite modificar e insertar nuevos datos
 *
 * @author Javier García-Retamero Redondo
 */
class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    private lateinit var idProducto: String

    private val INSERTANDO_MODIFICANDO: Int = 1

    //Declara un objeto para el intent que recibe en la llamada (similar a getIntent)
    private lateinit var objetoIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearObjetosDelXml()

        objetoIntent = intent
        //Cogemos el valor de una variable que viene en el intent

        //Si se ha enviado el id del producto estamos ante una modificación y por tanto cargamos los datos
        //en pantalla
        if (objetoIntent.hasExtra("IdProducto")) {
            idProducto = objetoIntent.getStringExtra("IdProducto")!!
            binding.editTextNombre.setText(objetoIntent.getStringExtra("NombreProducto"))
            binding.editTextDesc.setText(objetoIntent.getStringExtra("DescProducto"))
            binding.editTextEnlaceFoto.setText(objetoIntent.getStringExtra("FotoProducto"))
        }

        //Glide.with(binding.imageViewFoto.context)
        //    .load(binding.editTextEnlaceFoto.text.toString()).into(binding.imageViewFoto)

        binding.buttonCancelar.setOnClickListener {
            volver()
        }

        binding.buttonGuardar.setOnClickListener {
            volverRetornandoDatos()
        }
    }

    /**
     * Volver
     *
     * Elimina la activity para volver a la que la ha llamado
     * No devuelve datos
     *
     */
    fun volver() {
        finish()
    }

    /**
     * Volver retornando datos
     *
     */
    fun volverRetornandoDatos() {

        //Creamos un intent para devolver los datos
        val intent = Intent()

        //Si se pasó un ID entonces lo devolvemos
        //En caso contrario, no se devolvería para que se genere uno nuevo en el MainActivity
        if (objetoIntent.hasExtra("IdProducto")) {
            intent.putExtra("IdProducto", idProducto)
        }

        intent.putExtra("NombreProducto", binding.editTextNombre.text.toString())
        intent.putExtra("DescProducto", binding.editTextDesc.text.toString())
        intent.putExtra("FotoProducto", binding.editTextEnlaceFoto.text.toString())

        setResult(INSERTANDO_MODIFICANDO, intent)
        finish()
    }

    /**
     * Crea los objetos del XML de la activity para poder manipularlos en el código.
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}