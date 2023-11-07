package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        val intent = Intent(this, activity_diario_vista::class.java)
        startActivity(intent)
    }

    fun abrirBuscador(view: View) {
        val intent = Intent(this, activity_buscador::class.java)
        startActivity(intent)
    }


    fun compartir() {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.asunto))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.contenido))
                putExtra(Intent.EXTRA_EMAIL, arrayOf("jaimealbaruiz@gmail.com"))
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

    fun abrirPreferencias() {
        val abrirPref: Intent = Intent(this, activity_settings::class.java)
        startActivity(abrirPref)
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

            R.id.Preferencias -> {
                abrirPreferencias()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}