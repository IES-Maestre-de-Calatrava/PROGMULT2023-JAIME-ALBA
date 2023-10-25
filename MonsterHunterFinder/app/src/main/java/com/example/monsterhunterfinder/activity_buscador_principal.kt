package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityBuscadorPrincipalBinding

class activity_buscador_principal : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }

    fun abrirBuscadorPalabras(view: View) {
        val intent = Intent(this, activity_buscador_palabras::class.java)
        intent.putExtra("Palabras buscadas", binding.editTextBuscar.text.toString())
        startActivity(intent)
    }
}