package com.example.monsterhunterfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monsterhunterfinder.databinding.ActivityBuscadorPrincipalBinding

class activity_buscador_principal : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}