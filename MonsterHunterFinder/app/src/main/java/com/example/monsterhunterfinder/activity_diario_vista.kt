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
import com.example.monsterhunterfinder.databinding.ActivityDiarioVistaBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_diario_vista : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioVistaBinding

    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("cazas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        registerForContextMenu(binding.textoFiltroArma)

        listarTodos()

        binding.botonFiltrar.setOnClickListener {
            listarFiltrando()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
    }

    private fun crearObjetosDelXml() {
        binding=ActivityDiarioVistaBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        when (v) {
            binding.textoFiltroArma -> menuInflater.inflate(R.menu.menu_armas, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuGranEspada -> {
                binding.textoFiltroArma.setText(R.string.granEspada)
                true
            }

            R.id.menuEspadaLarga -> {
                binding.textoFiltroArma.setText(R.string.espadaLarga)
                true
            }

            R.id.menuEspadaYEscudo -> {
                binding.textoFiltroArma.setText(R.string.espadaYEscudo)
                true
            }

            R.id.menuEspadasDobles -> {
                binding.textoFiltroArma.setText(R.string.espadasDobles)
                true
            }

            R.id.menuLanza -> {
                binding.textoFiltroArma.setText(R.string.lanza)
                true
            }

            R.id.menuLanzaPistola -> {
                binding.textoFiltroArma.setText(R.string.lanzaPistola)
                true
            }

            R.id.menuMartillo -> {
                binding.textoFiltroArma.setText(R.string.martillo)
                true
            }

            R.id.menuCornamusa -> {
                binding.textoFiltroArma.setText(R.string.cornamusa)
                true
            }

            R.id.menuHachaEspada -> {
                binding.textoFiltroArma.setText(R.string.hachaEspada)
                true
            }

            R.id.menuHachaCargada -> {
                binding.textoFiltroArma.setText(R.string.hachaCargada)
                true
            }

            R.id.menuGlaiveInsecto -> {
                binding.textoFiltroArma.setText(R.string.glaiveInsecto)
                true
            }

            R.id.menuBallestaLigera -> {
                binding.textoFiltroArma.setText(R.string.ballestaLigera)
                true
            }

            R.id.menuBallestaPesada -> {
                binding.textoFiltroArma.setText(R.string.ballestaPesada)
                true
            }

            R.id.menuArco -> {
                binding.textoFiltroArma.setText(R.string.arco)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    fun vaciarCampos() {
        binding.textoFiltroArma.setText("")
        listarTodos()
    }

    fun mostrarOpciones(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.cuidado)
            .setMessage(R.string.vaciarFiltroPregunta)
            .setCancelable(false) // El usuario no puede salirse clickando fuera
            .setPositiveButton(
                R.string.vaciarFiltroSi,
                DialogInterface.OnClickListener{
                        dialog, id -> vaciarCampos()
                }
            )
            .setNegativeButton(
                R.string.vaciarFiltroNo,
                DialogInterface.OnClickListener{
                        dialog, id -> dialog.cancel()
                }
            )
            .show()
    }

    fun añadir(view: View) {
        val abrirAñadir: Intent = Intent(this, activity_diario_anadir::class.java)
        startActivity(abrirAñadir)
    }

    fun detalles(view: View) {
        val abrirDetalles: Intent = Intent(this, activity_diario_ver::class.java)
        abrirDetalles.putExtra("ID de la entrada", binding.textoDetallesEntrada.text.toString())
        startActivity(abrirDetalles)
    }


    private fun listarTodos() {
        myCollection
            .get()
            .addOnSuccessListener {
                    resultado ->
                binding.listaEntradas.setText("")
                for (documento in resultado) {
                    binding.listaEntradas.append(
                        documento.get("numentrada").toString()+" --- "+
                        documento.get("titulo").toString()+" --- "+
                        documento.get("arma").toString()+"\n"
                    )
                }
            }
    }

    private fun listarFiltrando() {
        myCollection
            .whereEqualTo("arma", binding.textoFiltroArma.text.toString())
            .get()
            .addOnSuccessListener {
                    resultado ->
                binding.listaEntradas.setText("")
                for (documento in resultado) {
                    binding.listaEntradas.append(
                        documento.get("numentrada").toString()+" --- "+
                        documento.get("titulo").toString()+" --- "+
                        documento.get("arma").toString()+"\n"
                    )
                }
            }
    }
}