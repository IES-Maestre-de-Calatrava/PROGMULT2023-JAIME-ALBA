package com.example.recycleview


// PARA LO DE LA FOTO: me boy a build.gradle.kts (Module: app) y a
// dependencies, e importo la librería
// implementation("com.github.bumptech.glide:glide:4.14.2")
// Como he cambiado algo del Gradle, darle a SyncNow, O HACE CUENTA QUE NO HE HECHO NADA.

// Meto el import de turno en la MainActivity2, el del glide.
// Me voy al ViewHolder.
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycleview.adapter.ProductosAdapter
import com.example.recycleview.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // PASO 1: Primero conectarlo a Firebase.
    private val db = FirebaseFirestore.getInstance()
    // En una misma base de datos nos podemos crear varias colecciones,
    // "varios xml". Me creo uno para ésta; productos.
    private val myCollection = db.collection("productos")

    // PASO 2: me creo en la misma carpeta del paquete com.example.recycleview un package
    // Lo voy a llamar com.example.recycleview.adapter
    // Dentro me creo dos clases KOTLIN: ProductosAdapter y ProductosViewHolder

    // El objetivo de las 2 clases es que preparen el entorno gráfico y lo vayan
    // cargando ellas, yo simplemente les doy el layout necesario

    // Me voy a ProductosViewHolder.


    // TRAS HABER VUELTO AQUÍ DESDE PRODUCTOSADAPTER
    // necesario para el RecyclerView
    private lateinit var productoProvider: ProductoProvider
    private lateinit var adapterProducto: ProductosAdapter
    // Le paso el manejador del layout: cómo quiero que pinte las celdas
    val manager = LinearLayoutManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Pongo el método que lo monta todo,
        // y más abajo lo creo
        initRecyclerView()

        binding.buttonInsertar.setOnClickListener {
            // Me creo una activity nueva para las inserciones. La MainActivity2.
            // En el xml de la MainActivity2 pego el archivo de Javier.
            // En el Kotlin del MainActivity2 meto el código que pasa Javier; hay
            // que cambiarle el package y otro par de cosas.

            // Método que abre la activity para añadir
            //openSomeActivitySendingData()

        }
    }

    private fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initRecyclerView() {
        // Puedo poner una pequeña línea separadora entre
        // elementos; se llaman decorator.
        // También es lo suyo poner los elementos de la lista
        // como una CardView, pero eso va aparte. Primero el
        // decorator.
        val decoration = DividerItemDecoration(this, manager.orientation)

        // Le hago un binding al Recycler de productos para
        // decirle cuál es su manejador
        binding.recyclerProductos.layoutManager = manager

        // Creo e inicializo el ProductoProvider y el Adapter
        productoProvider = ProductoProvider()

        // Al adapter le paso la lista con los elementos que
        // quiero que pinte.
        adapterProducto = ProductosAdapter(productosList = productoProvider.productosList)


        // Me falta llamar a Firestore para recuperar la colección.
        myCollection
            .get()
            .addOnSuccessListener {
                resultado ->
                    productoProvider.actualizarLista(resultado)
                    // Al binding le asigno el adapterProducto que he declarado algo
                    // más arriba
                    // En el momento, la lista está vacía; es en este punto cuando
                    // se le indica qué adapter tiene que usar para rellenar
                    binding.recyclerProductos.adapter = adapterProducto
                    // Le añado el decorador que me he declarado antes
                    binding.recyclerProductos.addItemDecoration(decoration)
            }
    }


    // Me creo el método para añadir información a la Firebase
    private fun insertRegister(producto: Producto) {
        myCollection
            // Dentro de la colección crea un documento con el ID que tenga
            .document(producto.id.toString())
            .set(
                hashMapOf(
                    "nombre" to producto.nombre,
                    "descripcion" to producto.descripcion,
                    "foto" to producto.foto // La foto puede dar un error al ir vacía
                )
            )

            // Tras insertar correctamente un registro, tiene que avisarme
            // de ello
            .addOnSuccessListener {
                productoProvider.productosList.add(producto.id, producto)
                // Le notifico al RecyclerView que ha habido un cambio y él lo refresca
                adapterProducto.notifyItemInserted(producto.id)
                // Decirle al RecyclerView que se mueva hasta el sitio donde se ha
                // añadido el registro nuevo: (probar a cambiarel offset)
                manager.scrollToPositionWithOffset(producto.id, 10)

                // VUELVO A LO QUE HAY EN EL ONCREATE, BAJO ESTE MÉTODO
            }
    }
}