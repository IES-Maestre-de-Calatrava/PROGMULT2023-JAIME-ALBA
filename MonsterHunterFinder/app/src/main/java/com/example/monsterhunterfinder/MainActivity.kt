package com.example.monsterhunterfinder

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.monsterhunterfinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_activity, menu)
        return true
    }

    fun abrirPerfil(view: View) {
        val intent = Intent(this, activity_perfil::class.java)
        startActivity(intent)
    }

    fun abrirDiario(view: View) {
        val intent = Intent(this, activity_diario::class.java)
        startActivity(intent)
    }

    /**fun abrirBuscador(view: View) {
        val intent = Intent(this, activity_buscador_principal::class.java)
        startActivity(intent)
    }*/


    fun compartir() {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "¿Aburrido de cazar en solitario? ¡Descarga ya Monster Hunter Finder!")
            }
        )
    }

    fun lanzarAcercaDe() {
        Toast.makeText(this, "¡Sígueme en Twitter! @HajiTheHunter", Toast.LENGTH_LONG).show()
    }

    fun abrirWeb() {
        val lanzarWeb: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mhrise.kiranico.com/es"))
        startActivity(lanzarWeb)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.AcercaDe -> {
                lanzarAcercaDe()
                true
            }

            R.id.Web -> {
                abrirWeb()
                true
            }

            R.id.CompartirApp -> {
                compartir()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}