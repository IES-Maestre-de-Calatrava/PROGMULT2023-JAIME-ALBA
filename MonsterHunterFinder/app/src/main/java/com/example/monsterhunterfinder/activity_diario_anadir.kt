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


    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("cazas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.botonGuardar.setOnClickListener() {
            guardarRegistro()
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

    private fun guardarRegistro() {
        myCollection.document(binding.textoNumEntradaAnadir.text.toString()).set(
            hashMapOf(
                "numentrada" to binding.textoNumEntradaAnadir.text.toString(),
                "titulo" to binding.textoTituloEntradaAnadir.text.toString(),
                "arma" to binding.textoArmaUtilizadaAnadir.text.toString(),
                "resumen" to binding.textoResumenCazaAnadir.text.toString()
            )
        )
        resultadoOperacion("Registro guardado correctamente")
    }

    private fun resultadoOperacion(mensaje: String){
        binding.textoNumEntradaAnadir.setText("")
        binding.textoTituloEntradaAnadir.setText("")
        binding.textoArmaUtilizadaAnadir.setText("")
        binding.textoResumenCazaAnadir.setText("")
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }






}