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

/**
 * Activity mediante la que el usuario puede visualizar
 * versiones completas de entradas que haya añadido a su
 * diario de caza.
 * @author Jaime
 */
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

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityDiarioVerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
    }

    /**
     * Función que finaliza la activity actual, devolviendo
     * al usuario a aquella activity desde la que hubiera
     * accedido.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
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

    /**
     * Función que lanza un intent para abrir una
     * activity con la página web contenida en la
     * String que se le indique al parser.
     */
    fun abrirWeb() {
        val lanzarWeb: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mhrise.kiranico.com/es"))
        startActivity(lanzarWeb)
    }

}