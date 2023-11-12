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

/**
 * Activity dedicada al buscador, en la que el usuario puede
 * realizar búsquedas de otros usuarios filtrando de distintas
 * formas.
 * @author Jaime
 */
class activity_buscador : AppCompatActivity() {

    private lateinit var binding: ActivityBuscadorBinding

    // Se recupera la colección "cazadores" de la firestore;
    // se trata de una lista de usuarios ficticia
    private val db = FirebaseFirestore.getInstance()
    private val coleccionCazadores = db.collection("cazadores")

    // Elementos necesarios para la RecyclerView
    private lateinit var cazadorProvider: CazadorProvider
    private lateinit var cazadoresAdapter: CazadoresAdapter
    val managerCazadores = LinearLayoutManager(this)

    var opcionBusqueda: Int = 0

    // Necesario para recuperar las preferencias
    private lateinit var misPreferencias: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Se establece como toolbar la que nos hemos creado
        // y se le indica que no muestre el título
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        iniciarRecycleViewCazadores()

        misPreferencias = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        // Se establece la barra de búsqueda para que de ella surja un menú contextual
        registerForContextMenu(binding.barraBusqueda)


        // Dentro de este tramo de código se establece un listener para la consulta
        // que lance la SearchView a la Firestore; una función para el caso de que
        // se envíe la consulta a hacer y otra para auto-lanzar como consultas cambios
        // que haya en el texto contenido en la SearchView.

        // Si dicho texto está vacío, se usa la Recycler View completa. Si no, dependiendo
        // de la opción de filtrado que haya seleccionado el usuario en el menú
        // contextual, se filtra de una forma distinta. Por filtrar entiéndase iniciar
        // una RecyclerView que contiene únicamente registros con los que se encuentran
        // coincidencias, dependiendo del campo buscado.
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

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityBuscadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when (v){
            // En la SearchView se infla como menú contextual el menú xml indicado
            binding.barraBusqueda -> menuInflater.inflate(R.menu.menu_buscar_favoritos, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        // La variable opcionBusqueda tendrá como valor 1, 2 o 3
        // dependiendo de lo que seleccione el usuario en el menú
        // contextual.
        // Al elegir una opción del menú contextual, aparte de variarse
        // el valor de opcionBusqueda, en la SearchView se establece un
        // texto.
        // Este texto será la preferencia del usuario correspondiente según
        // la opción elegida: arma favorita, juego favorito o frecuencia de juego.
        return when (item.itemId) {
            R.id.BuscarArmaFavorita -> {
                opcionBusqueda=1
                binding.barraBusqueda.setQuery(misPreferencias.getString("arma", getString(R.string.granEspada)), false)
                true
            }

            R.id.BuscarJuegoFavorito -> {
                opcionBusqueda=2
                binding.barraBusqueda.setQuery(misPreferencias.getString("juegofav", "MHWorld"), false)
                true
            }

            R.id.BuscarFrecuenciaJuego -> {
                opcionBusqueda=3
                binding.barraBusqueda.setQuery(misPreferencias.getString("dias", getString(R.string.findes)), false)
                true
            }

            else -> super.onContextItemSelected(item)
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Aquí se inflan los objetos xml de la toolbar que se use
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // En este caso, la única acción que se puede iniciar
        // seleccionando items de la toolbar es la de abrir web
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
     * Método que inicializa la RecyclerView con el contenido de la
     * colección cuya variable hayamos inicializado anteriormente.
     */
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


    /**
     * Método que realiza una función similar a la de inicializar una
     * RecyclerView, con la salvedad de que sólo muestra aquellos registros
     * que coincidan con el filtro que establece el método.
     * Filtra según el campo "bio" de la Firestore.
     * @param palabrasBuscar: String que contiene aquel texto con el que
     * se van a buscar coincidencias en el campo "bio" de los registros
     * de la Firestore.
     */
    private fun listarCazadoresFiltrando(palabrasBuscar: String?) {
        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        // El filtrado (iniciado de RecyclerView con sólo determinados registros)
        // por defecto utiliza el campo "bio", la biografía del usuario
        coleccionCazadores
            .whereEqualTo("bio", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                        cazadorProvider.actualizarLista(resultado)
                        binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


    /**
     * Método que realiza una función similar a la de inicializar una
     * RecyclerView, con la salvedad de que sólo muestra aquellos registros
     * que coincidan con el filtro que establece el método.
     * Filtra según el campo "arma" de la Firestore.
     * @param palabrasBuscar: String que contiene aquel texto con el que
     * se van a buscar coincidencias en el campo "arma" de los registros
     * de la Firestore.
     */
    private fun listarCazadoresFiltrandoArma(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            // Filtrado según campo del arma
            .whereEqualTo("arma", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                cazadorProvider.actualizarLista(resultado)
                binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


    /**
     * Método que realiza una función similar a la de inicializar una
     * RecyclerView, con la salvedad de que sólo muestra aquellos registros
     * que coincidan con el filtro que establece el método.
     * Filtra según el campo "juegofav" de la Firestore.
     * @param palabrasBuscar: String que contiene aquel texto con el que
     * se van a buscar coincidencias en el campo "juegofav" de los registros
     * de la Firestore.
     */
    private fun listarCazadoresFiltrandoJuegofav(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            // Filtrado según campo del juego favorito
            .whereEqualTo("juegofav", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                cazadorProvider.actualizarLista(resultado)
                binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


    /**
     * Método que realiza una función similar a la de inicializar una
     * RecyclerView, con la salvedad de que sólo muestra aquellos registros
     * que coincidan con el filtro que establece el método.
     * Filtra según el campo "dias" de la Firestore.
     * @param palabrasBuscar: String que contiene aquel texto con el que
     * se van a buscar coincidencias en el campo "dias" de los registros
     * de la Firestore.
     */
    private fun listarCazadoresFiltrandoDias(palabrasBuscar: String?) {

        binding.recyclerViewCazadores.layoutManager = managerCazadores

        cazadorProvider = CazadorProvider()
        cazadoresAdapter = CazadoresAdapter(
            cazadoresList = cazadorProvider.cazadoresList
        )

        coleccionCazadores
            // Filtrado según campo de días, la frecuencia de juego
            .whereEqualTo("dias", palabrasBuscar)
            .get()
            .addOnSuccessListener {
                    resultado ->
                cazadorProvider.actualizarLista(resultado)
                binding.recyclerViewCazadores.adapter = cazadoresAdapter
            }
    }


}