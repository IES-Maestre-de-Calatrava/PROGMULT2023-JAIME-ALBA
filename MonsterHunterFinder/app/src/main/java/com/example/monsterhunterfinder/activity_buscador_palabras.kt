package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityBuscadorPalabrasBinding

class activity_buscador_palabras : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorPalabrasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorPalabrasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val objetoIntent: Intent = intent
        var palabrasBuscadas = objetoIntent.getStringExtra("Palabras buscadas")
        binding.textPalabrasBuscadas.text = palabrasBuscadas
    }

    fun volver(view: View) {
        finish()
    }
}