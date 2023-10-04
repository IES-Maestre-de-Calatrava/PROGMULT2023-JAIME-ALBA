package com.example.comunicacionactivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.comunicacionactivities.databinding.ActivityActividadCaso1Binding

class activity_actividad_caso_1 : AppCompatActivity() {

    private lateinit var binding: ActivityActividadCaso1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding = ActivityActividadCaso1Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Me creo el método que me va a cerrar la activity y
     * me va a devolver a la main.
     * Elimina la activity para volver a aquella que la
     * ha llamado.
     *
     * @param[view] Objeto que llama al método
     *
     * Me voy al XML y asocio este método al botón Cerrar.
     */
    fun volver(view: View) {
        finish()
    }
}