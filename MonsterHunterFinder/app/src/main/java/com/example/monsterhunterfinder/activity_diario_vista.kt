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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monsterhunterfinder.adapter.EntradasAdapter
import com.example.monsterhunterfinder.databinding.ActivityDiarioVistaBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_diario_vista : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioVistaBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("cazas")


    // Variables para el RecyclerView
    private lateinit var entradaProvider: EntradaProvider
    private lateinit var entradasAdapter: EntradasAdapter
    val manager = LinearLayoutManager(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        registerForContextMenu(binding.textoFiltroArma)


        iniciarRecyclerView()

        binding.botonFiltrar.setOnClickListener {
            listarFiltrando()
        }



        binding.botonAnadir.setOnClickListener {
            lanzarActivityRegistro()
        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
                    if (resultado.data != null) {
                        val datos: Intent = resultado.data!!
                        val numEntrada: Int = entradaProvider.listaEntradas.size
                        var entradaDiario = Entrada(
                            numEntrada,
                            datos.getStringExtra("titulo")!!,
                            datos.getStringExtra("arma")!!,
                            datos.getStringExtra("resumen")!!
                        )

                        insertarRegistro(entradaDiario)
                    }
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
        iniciarRecyclerView()
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


    private fun iniciarRecyclerView() {
        val decoracion = DividerItemDecoration(this, manager.orientation)

        binding.recyclerEntradas.layoutManager = manager

        entradaProvider = EntradaProvider()
        entradasAdapter = EntradasAdapter(listaEntradas = entradaProvider.listaEntradas)

        myCollection
            .get()
            .addOnSuccessListener {
                resultado ->
                    entradaProvider.actualizarLista(resultado)
                    binding.recyclerEntradas.adapter = entradasAdapter
                    binding.recyclerEntradas.addItemDecoration(decoracion)
            }
    }


    private fun listarFiltrando() {
        val decoracion = DividerItemDecoration(this, manager.orientation)

        binding.recyclerEntradas.layoutManager = manager

        entradaProvider = EntradaProvider()
        entradasAdapter = EntradasAdapter(listaEntradas = entradaProvider.listaEntradas)

        myCollection
            .whereEqualTo("arma", binding.textoFiltroArma.text.toString())
            .get()
            .addOnSuccessListener {
                    resultado ->
                entradaProvider.actualizarLista(resultado)
                binding.recyclerEntradas.adapter = entradasAdapter
                binding.recyclerEntradas.addItemDecoration(decoracion)
            }
    }

    private fun lanzarActivityRegistro() {
        val intent = Intent(this, activity_diario_anadir::class.java)
        activityResultLauncher.launch(intent)
    }

    private fun insertarRegistro(entradaDiario: Entrada) {
        myCollection
            .document(entradaDiario.numEntrada.toString())
            .set(
                hashMapOf(
                    "titulo" to entradaDiario.titulo,
                    "arma" to entradaDiario.arma,
                    "resumen" to entradaDiario.resumen
                )
            )
            .addOnSuccessListener {
                entradaProvider.listaEntradas.add(entradaDiario.numEntrada, entradaDiario)
                entradasAdapter.notifyItemInserted(entradaDiario.numEntrada)
                manager.scrollToPositionWithOffset(entradaDiario.numEntrada, 35)
            }
    }
}