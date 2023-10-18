package com.example.menucontextual20232024

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import com.example.menucontextual20232024.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        registerForContextMenu(binding.editTextIdioma)
        // Ejercicio: conseguir hacer lo mismo para el EditText de Oficio
        registerForContextMenu(binding.editTextOficio)
    }


    // Paso como parámetros el ContextMenu que voy a usar y la view en la que voy a hacer
    // la pulsación larga
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when (v){
            binding.editTextIdioma -> menuInflater.inflate(R.menu.menu_idiomas, menu)
            binding.editTextOficio -> menuInflater.inflate(R.menu.menu_oficio, menu)
        }

    }


    // Función para que ajuste un texto en el EditText según la opción seleccionada
    // en el menú contextual
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ingles -> {
                binding.editTextIdioma.setText("Ingles")
                true
            }

            R.id.frances -> {
                binding.editTextIdioma.setText("Traidor a la sangre")
                true
            }

            R.id.aleman -> {
                binding.editTextIdioma.setText("Alemán")
                true
            }

            R.id.informatica -> {
                binding.editTextOficio.setText("Pobre desgraciado")
                true
            }

            R.id.comercial -> {
                binding.editTextOficio.setText("Comercial")
                true
            }

            R.id.administrativo -> {
                binding.editTextOficio.setText("Administrativo")
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }




    private fun crearObjetosDelXml() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}