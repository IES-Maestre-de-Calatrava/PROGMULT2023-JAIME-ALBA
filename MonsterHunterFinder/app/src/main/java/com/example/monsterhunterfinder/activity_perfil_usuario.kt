package com.example.monsterhunterfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monsterhunterfinder.databinding.ActivityPerfilUsuarioBinding

class activity_perfil_usuario : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilUsuarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}