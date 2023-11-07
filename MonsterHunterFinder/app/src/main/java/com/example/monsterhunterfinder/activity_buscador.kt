package com.example.monsterhunterfinder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monsterhunterfinder.adapter.CazadoresAdapter
import com.example.monsterhunterfinder.adapter.EntradasAdapter
import com.example.monsterhunterfinder.databinding.ActivityBuscadorBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_buscador : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorBinding

    private val db = FirebaseFirestore.getInstance()
    private val coleccionCazadores = db.collection("cazadores")

    private lateinit var cazadorProvider: CazadorProvider
    private lateinit var cazadoresAdapter: CazadoresAdapter
    val managerCazadores = LinearLayoutManager(this)

    var opcionBusqueda: Int = 0
    private lateinit var misPreferencias: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        iniciarRecycleViewCazadores()

        misPreferencias = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        registerForContextMenu(binding.barraBusqueda)

        binding.barraBusqueda.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.barraBusqueda.clearFocus()
                if (query==""){
                    iniciarRecycleViewCazadores()
                }else{
                    when (opcionBusqueda) {
                        1 -> listarCazadoresFiltrandoArma(query)
                        2 -> listarCazadoresFiltrandoJuegofav(query)
                        3 -> listarCazadoresFiltrandoDias(query)
                        else -> listarCazadoresFiltrando(query)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText==""){
                    iniciarRecycleViewCazadores()
                }else{
                    when (opcionBusqueda) {
                        1 -> listarCazadoresFiltrandoArma(newText)
                        2 -> listarCazadoresFiltrandoJuegofav(newText)
                        3 -> listarCazadoresFiltrandoDias(newText)
                        else -> listarCazadoresFiltrando(newText)
                    }
                }
                return false
            }

        })
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when (v){
            binding.barraBusqueda -> menuInflater.inflate(R.menu.menu_buscar_favoritos, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.BuscarArmaFavorita -> {
                opcionBusqueda=1
                binding.barraBusqueda.setQuery(misPreferencias.getString("arma", "Gran espada"), false)
                true
            }

            R.id.BuscarJuegoFavorito -> {
                opcionBusqueda=2
                binding.barraBusqueda.setQuery(misPreferencias.getString("juego", "MHWorld"), false)
                true
            }

            R.id.BuscarFrecuenciaJuego -> {
                opcionBusqueda=3
                binding.barraBusqueda.setQuery(misPreferencias.getString("dias", "Findes"), false)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    fun volver(view: View) {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
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

    private fun iniciarRecycleViewCazadores() {
        binding.recyclerViewCazadores.layoutManager = managerCazadores
        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(cazadoresList = cazadorProvider.cazadoresList)

        coleccionCazadores
            .get()
            .addOnSuccessListener {
                resultado ->
                    cazadorProvider.actualizarLista(resultado)
                    binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }

    private fun listarCazadoresFiltrando(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            .whereEqualTo("bio", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                        cazadorProvider.actualizarLista(resultado)
                        binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


    private fun listarCazadoresFiltrandoArma(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            .whereEqualTo("arma", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                cazadorProvider.actualizarLista(resultado)
                binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


    private fun listarCazadoresFiltrandoJuegofav(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            .whereEqualTo("juegofav", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                cazadorProvider.actualizarLista(resultado)
                binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


    private fun listarCazadoresFiltrandoDias(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            .whereEqualTo("dias", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                cazadorProvider.actualizarLista(resultado)
                binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


}