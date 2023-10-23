package com.example.monsterhunterfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monsterhunterfinder.databinding.ActivityBuscadorMapaBinding

class activity_buscador_mapa : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorMapaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}