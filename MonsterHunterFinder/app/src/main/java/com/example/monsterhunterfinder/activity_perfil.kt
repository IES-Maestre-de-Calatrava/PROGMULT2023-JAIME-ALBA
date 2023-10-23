package com.example.monsterhunterfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monsterhunterfinder.databinding.ActivityPerfilBinding

class activity_perfil : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}