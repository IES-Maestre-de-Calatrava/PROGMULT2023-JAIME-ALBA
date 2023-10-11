package com.example.toolbarapp20232024

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.toolbarapp20232024.databinding.Activity2Binding

class activity2 : AppCompatActivity() {

    private lateinit var binding: Activity2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding = Activity2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }
}