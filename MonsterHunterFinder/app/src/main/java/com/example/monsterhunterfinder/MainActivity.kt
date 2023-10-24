package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun abrirPerfil(view: View) {
        val intent = Intent(this, activity_perfil::class.java)
        startActivity(intent)
    }

    fun abrirMensajes(view: View) {
        val intent = Intent(this, activity_mensajes_general::class.java)
        startActivity(intent)
    }

    fun abrirBuscador(view: View) {
        val intent = Intent(this, activity_buscador_principal::class.java)
        startActivity(intent)
    }
}