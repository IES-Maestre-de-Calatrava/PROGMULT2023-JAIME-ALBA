package com.example.comunicacionactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.comunicacionactivities.databinding.ActivityActividadCaso3Binding

class activity_actividad_caso_3 : AppCompatActivity() {

    private lateinit var binding: ActivityActividadCaso3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding = ActivityActividadCaso3Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Volver
     * Elimina la activity, vuelve a la inicial y a esta inicial le devuelve el
     * texto que se le haya pasado por el EditText
     */
    fun volver(view: View) {
        val intent = Intent()
        intent.putExtra("Mensaje", binding.editTextText2.text.toString())
        // Para decirle que el intent vaya como resultado:
        setResult(123, intent)
        finish()
    }
}