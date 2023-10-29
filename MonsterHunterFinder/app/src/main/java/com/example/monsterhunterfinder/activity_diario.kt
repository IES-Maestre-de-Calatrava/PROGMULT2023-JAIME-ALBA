package com.example.monsterhunterfinder

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.monsterhunterfinder.databinding.ActivityDiarioBinding

class activity_diario : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun crearObjetosDelXml() {
        binding=ActivityDiarioBinding.inflate(layoutInflater)
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

    fun vaciarCampos() {
        binding.textoNumEntrada.setText("")
        binding.textoArmaUtilizada.setText("")
        binding.textoResumenCaza.setText("")

    }

    fun mostrarOpciones(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("VACIAR CAMPOS")
            .setMessage("¿Quieres vaciar los campos?")
            .setCancelable(false) // El usuario no puede salirse clickando fuera
            .setPositiveButton(
                "¡Seguro!",
                DialogInterface.OnClickListener{
                    dialog, id -> vaciarCampos()
                }
            )
            .setNegativeButton(
                "Mejor no.",
                DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                }
            )
            .show()
    }
}