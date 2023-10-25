package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityBuscadorPalabrasBinding

class activity_buscador_palabras : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorPalabrasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorPalabrasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val objetoIntent: Intent = intent
        var palabrasBuscadas = objetoIntent.getStringExtra("Palabras buscadas")
        binding.textPalabrasBuscadas.text = palabrasBuscadas

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
    }

    fun volver(view: View) {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Web -> {
                abrirKiranico()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun abrirKiranico() {
        val lanzarWeb: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mhrise.kiranico.com/es"))
        startActivity(lanzarWeb)
    }
}