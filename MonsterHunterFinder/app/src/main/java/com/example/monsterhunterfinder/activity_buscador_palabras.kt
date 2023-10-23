package com.example.monsterhunterfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}