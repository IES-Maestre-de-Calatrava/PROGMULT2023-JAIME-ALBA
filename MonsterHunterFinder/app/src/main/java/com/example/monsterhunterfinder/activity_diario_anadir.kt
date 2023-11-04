package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.monsterhunterfinder.databinding.ActivityDiarioAnadirBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_diario_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioAnadirBinding
    private lateinit var objetoIntent: Intent
    private val INSERTADO_OK: Int = 1


    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("cazas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        objetoIntent = intent
        binding.botonGuardar.setOnClickListener() {
            volverConDatos()
        }
    }

    private fun crearObjetosDelXml() {
        binding=ActivityDiarioAnadirBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                abrirWeb()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun abrirWeb() {
        val lanzarWeb: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mhrise.kiranico.com/es"))
        startActivity(lanzarWeb)
    }

    fun volverConDatos() {
        val intent = Intent()
        intent.putExtra("titulo", binding.textoTituloEntradaAnadir.text.toString())
        intent.putExtra("arma", binding.textoArmaUtilizadaAnadir.text.toString())
        intent.putExtra("resumen", binding.textoResumenCazaAnadir.text.toString())
        setResult(INSERTADO_OK, intent)
        finish()
    }








}