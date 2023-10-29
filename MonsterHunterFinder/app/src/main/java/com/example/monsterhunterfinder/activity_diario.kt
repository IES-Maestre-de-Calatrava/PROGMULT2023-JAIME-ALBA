package com.example.monsterhunterfinder

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
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

        registerForContextMenu(binding.textoArmaUtilizada)
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

        builder.setTitle(R.string.cuidado)
            .setMessage(R.string.vaciarCamposPregunta)
            .setCancelable(false) // El usuario no puede salirse clickando fuera
            .setPositiveButton(
                R.string.vaciarCamposSi,
                DialogInterface.OnClickListener{
                    dialog, id -> vaciarCampos()
                }
            )
            .setNegativeButton(
                R.string.vaciarCamposNo,
                DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                }
            )
            .show()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        when (v) {
            binding.textoArmaUtilizada -> menuInflater.inflate(R.menu.menu_armas, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuGranEspada -> {
                binding.textoArmaUtilizada.setText(R.string.granEspada)
                true
            }

            R.id.menuEspadaLarga -> {
                binding.textoArmaUtilizada.setText(R.string.espadaLarga)
                true
            }

            R.id.menuEspadaYEscudo -> {
                binding.textoArmaUtilizada.setText(R.string.espadaYEscudo)
                true
            }

            R.id.menuEspadasDobles -> {
                binding.textoArmaUtilizada.setText(R.string.espadasDobles)
                true
            }

            R.id.menuLanza -> {
                binding.textoArmaUtilizada.setText(R.string.lanza)
                true
            }

            R.id.menuLanzaPistola -> {
                binding.textoArmaUtilizada.setText(R.string.lanzaPistola)
                true
            }

            R.id.menuMartillo -> {
                binding.textoArmaUtilizada.setText(R.string.martillo)
                true
            }

            R.id.menuCornamusa -> {
                binding.textoArmaUtilizada.setText(R.string.cornamusa)
                true
            }

            R.id.menuHachaEspada -> {
                binding.textoArmaUtilizada.setText(R.string.hachaEspada)
                true
            }

            R.id.menuHachaCargada -> {
                binding.textoArmaUtilizada.setText(R.string.hachaCargada)
                true
            }

            R.id.menuGlaiveInsecto -> {
                binding.textoArmaUtilizada.setText(R.string.glaiveInsecto)
                true
            }

            R.id.menuBallestaLigera -> {
                binding.textoArmaUtilizada.setText(R.string.ballestaLigera)
                true
            }

            R.id.menuBallestaPesada -> {
                binding.textoArmaUtilizada.setText(R.string.ballestaPesada)
                true
            }

            R.id.menuArco -> {
                binding.textoArmaUtilizada.setText(R.string.arco)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}