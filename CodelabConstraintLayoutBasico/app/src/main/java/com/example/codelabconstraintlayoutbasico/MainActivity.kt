package com.example.codelabconstraintlayoutbasico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// Crear un diseño que incluya un cuadro de texto
// y un botón, y que la app envíe el texto a otra
// activity si se presiona el botón.

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}