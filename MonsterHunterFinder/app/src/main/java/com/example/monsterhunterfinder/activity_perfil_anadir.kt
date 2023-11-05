package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityPerfilAnadirBinding

class activity_perfil_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilAnadirBinding
    private val FOTO_INSERTADA: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        binding.botonConfirmarAnadir.setOnClickListener {
            volverConFoto()
        }
    }

    private fun crearObjetosDelXml() {
        binding=ActivityPerfilAnadirBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }

    fun volverConFoto() {
        val intent = Intent()
        intent.putExtra("enlace", binding.textoEnlaceFoto.text.toString())
        setResult(FOTO_INSERTADA, intent)
        finish()
    }
}