package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityDiarioVerBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_diario_ver : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioVerBinding

    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("cazas")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val objetoIntent: Intent = intent
        var datosEnviados = objetoIntent.getStringExtra("Identificador")
        if (datosEnviados != null) {
            myCollection
                .document(datosEnviados)
                .get()
                .addOnSuccessListener {
                        resultado ->
                    val titulo = resultado.getString("titulo")
                    val arma = resultado.getString("arma")
                    val resumen = resultado.getString("resumen")

                    binding.textoTituloEntradaVer.setText(titulo)
                    binding.textoArmaUtilizadaVer.setText(arma)
                    binding.textoResumenCazaVer.setText(resumen)
                }
        }



    }

    private fun crearObjetosDelXml() {
        binding=ActivityDiarioVerBinding.inflate(layoutInflater)
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

}