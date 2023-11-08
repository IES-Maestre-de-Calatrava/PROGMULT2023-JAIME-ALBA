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

/**
 * Activity en la que el usuario puede añadir
 * o modificar registros del diario de caza.
 * @author Jaime
 */
class activity_diario_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioAnadirBinding
    private lateinit var objetoIntent: Intent
    private val INSERTADO_OK: Int = 1

    private lateinit var numEntrada: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        objetoIntent = intent

        if (objetoIntent.hasExtra("id")) {
            numEntrada = objetoIntent.getStringExtra("id")!!
            binding.textoTituloEntradaAnadir.setText(objetoIntent.getStringExtra("titulo"))
            binding.textoArmaUtilizadaAnadir.setText(objetoIntent.getStringExtra("arma"))
            binding.textoResumenCazaAnadir.setText(objetoIntent.getStringExtra("resumen"))
        }


        binding.botonGuardar.setOnClickListener() {
            volverConDatos()
        }
    }

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityDiarioAnadirBinding.inflate(layoutInflater)
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

    /**
     * Función que vuelve a la activity que ha llamado a la presente
     * con un intent que incluye datos recogidos de las cajas de
     * texto presentes en el layout de esta activity.
     * Para recoger dichos datos, será necesario un activityResultLauncher
     * en la activity que ha llamado a la presente.
     */
    fun volverConDatos() {
        val intent = Intent()


        if (objetoIntent.hasExtra("id")) {
            intent.putExtra("id", numEntrada)
        }

        intent.putExtra("titulo", binding.textoTituloEntradaAnadir.text.toString())
        intent.putExtra("arma", binding.textoArmaUtilizadaAnadir.text.toString())
        intent.putExtra("resumen", binding.textoResumenCazaAnadir.text.toString())

        setResult(INSERTADO_OK, intent)
        finish()
    }








}