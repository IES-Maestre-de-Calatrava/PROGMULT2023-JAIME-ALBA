package com.example.preferencias

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.preferencias.databinding.ActivityMainBinding

// Lo vamos a hacer con fragments.

// Nos va a pasar Javier el XML, en la carpeta de compartidos.
// Primero construimos el main. En lugar de botón, pongo botón de
// menú que se vaya a mi activity de preferencias; lo que se haya
// puesto en preferencias, se va a guardar en un archivo.


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Preparamos un activityResultLauncher.
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Lo primero que tiene que hacer nada más empezar el programa es cargar
        // las preferencias. También cuando el usuario se meta en las prefs y
        // cambie algo.
        loadPref()
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                loadPref()
            }

    }

    fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun loadPref() {
        // Tengo que saber exactamente cómo se llama el archivo.
        val mySharedPreferences = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        // A moneda le pongo un valor por defecto, por si el usuario aún no ha seleccionado un tipo de moneda.
        binding.textViewMoneda.text = mySharedPreferences.getString("moneda", "euros")
    }

    // Método de abrir preferencias, para que se abra esa ventana cuando el
    // usuario le dé al botón.
    fun abrirPreferencias(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        activityResultLauncher.launch(intent)
        // Tras llegar aquí, nos creamos una activity nueva: SettingsActivity
    }
}