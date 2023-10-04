package com.example.comunicacionactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.comunicacionactivities.databinding.ActivityActividadCaso2Binding


/**
 * Activity que recibe los datos y los muestra por pantalla
 */
class activity_actividad_caso_2 : AppCompatActivity() {

    private lateinit var binding: ActivityActividadCaso2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        /**
         * Declaro un objeto para el intent que recibe en la llamada,
         * un objeto para ese intent que nos están enviando
         */
        val objetoIntent: Intent = intent

        /**
         * Me declaro variables para recoger los datos que venían
         * en el intent
         */

        /**
         * Tener cuidado, no todo se manda como String
         */
        var datosEnviados = objetoIntent.getStringExtra("DatosEnviados")
        /**
         * Básicamente, le pido que me recoja el contenido de esa variable que
         * le nombro, la que he enviado desde el main
         */

        /**
         * Para pintar en pantalla:
         */
        binding.textView6.text = datosEnviados
    }

    private fun crearObjetosDelXml() {
        binding = ActivityActividadCaso2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }
}