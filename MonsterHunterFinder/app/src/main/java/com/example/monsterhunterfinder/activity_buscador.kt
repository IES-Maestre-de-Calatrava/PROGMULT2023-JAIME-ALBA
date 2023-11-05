package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monsterhunterfinder.adapter.CazadoresAdapter
import com.example.monsterhunterfinder.databinding.ActivityBuscadorBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_buscador : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorBinding

    private val db = FirebaseFirestore.getInstance()
    private val coleccionCazadores = db.collection("cazadores")

    private lateinit var cazadorProvider: CazadorProvider
    private lateinit var cazadoresAdapter: CazadoresAdapter
    val managerCazadores = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        iniciarRecycleViewCazadores()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val decoration = DividerItemDecoration(this, managerCazadores.orientation)
        binding.recyclerViewCazadores.layoutManager = managerCazadores
        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(cazadoresList = cazadorProvider.cazadoresList)

        coleccionCazadores
            .get()
            .addOnSuccessListener {
                resultado ->
                    cazadorProvider.actualizarLista(resultado)
                    binding.recyclerViewCazadores.adapter = cazadoresAdapter
                    binding.recyclerViewCazadores.addItemDecoration(decoration)
            }
    }
}