package com.example.toolbarapp20232024

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.toolbarapp20232024.databinding.Activity2Binding

class activity2 : AppCompatActivity() {

    private lateinit var binding: Activity2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.appbar.toolbarApp2)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun crearObjetosDelXml() {
        binding = Activity2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // En R está todo lo relativo al proyecto

            R.id.acercaDe -> {
                lanzarAcercaDe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun lanzarAcercaDe() {
        // Mensaje que aparece por pantalla y se quita al poquito rato
        Toast.makeText(this, "Estoy completamente desquiciado :D", Toast.LENGTH_LONG).show()
    }
}