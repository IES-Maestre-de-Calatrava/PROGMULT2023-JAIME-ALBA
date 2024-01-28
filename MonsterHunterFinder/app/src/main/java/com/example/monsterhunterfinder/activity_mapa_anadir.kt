package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.monsterhunterfinder.databinding.ActivityMapBinding
import com.example.monsterhunterfinder.databinding.ActivityMapaAnadirBinding

class activity_mapa_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityMapaAnadirBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        binding.botonVolverMapaAnadir.setOnClickListener {
            volver()
        }


        // Se recogen los datos de la activity anterior
        val intent: Intent = intent
        var latitud = intent.getStringExtra("latitud")
        var longitud = intent.getStringExtra("longitud")
        var tipo = intent.getStringExtra("tipo")

        // El texto de cabecera cambia según si se está añadiendo punto de interés
        // o punto de reunión
        if (tipo!!.equals("punto")) {
            binding.textoCabeceraAnadir.text = getString(R.string.punto)
        } else if (tipo!!.equals("reunion")) {
            binding.textoCabeceraAnadir.text = getString(R.string.reunion)
        }


        // Al darle al botón de guardar, si hay texto en ambas cajas,
        // se vuelve con los datos a la activity anterior
        binding.botonGuardarMapaAnadir.setOnClickListener {
            volverConDatos(latitud!!, longitud!!, tipo)
        }
    }

    /**
     * Función que retorna a la activity anterior portando datos; tipo, latitud,
     * longitud, título y descripción
     */
    private fun volverConDatos(latitud: String, longitud: String, tipo: String) {
        val intentVuelta = Intent()
        intentVuelta.putExtra("latitud", latitud)
        intentVuelta.putExtra("longitud", longitud)
        intentVuelta.putExtra("tipo", tipo)
        intentVuelta.putExtra("titulo", binding.textoTituloMarcadorAnadir.text.toString())
        intentVuelta.putExtra("descripcion", binding.textoDescripcionMarcadorAnadir.text.toString())
        Log.d("Probando", binding.textoTituloMarcadorAnadir.text.toString())
        Log.d("Probando", binding.textoDescripcionMarcadorAnadir.text.toString())
        setResult(123, intentVuelta)
        finish()
    }


    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding = ActivityMapaAnadirBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Función que finaliza la activity actual, devolviendo
     * al usuario a aquella activity desde la que hubiera
     * accedido.
     * También detiene la reproducción de audio si la había.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun volver() {
        finish()
    }
}