package com.example.recycleview


// PARA LO DE LA FOTO: me boy a build.gradle.kts (Module: app) y a
// dependencies, e importo la librería
// implementation("com.github.bumptech.glide:glide:4.14.2")
// Como he cambiado algo del Gradle, darle a SyncNow, O HACE CUENTA QUE NO HE HECHO NADA.

// Meto el import de turno en la MainActivity2, el del glide.
// Me voy al ViewHolder.
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recycleview.adapter.ProductosAdapter
import com.example.recycleview.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 02/11/2023, me creo el lateinit
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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


        // 02/11/2023
        binding.buttonInsertar.setOnClickListener {
            // Me creo una activity nueva para las inserciones. La MainActivity2.
            // En el xml de la MainActivity2 pego el archivo de Javier.
            // En el Kotlin del MainActivity2 meto el código que pasa Javier; hay
            // que cambiarle el package y otro par de cosas.

            // Método que abre la activity para añadir
            openDetRegister()
        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
                    if (result.data != null) {
                        val data: Intent = result.data!!

                        // Hacemos una consulta en la lista, buscamos cuál es el
                        // ID más grande y al más grande sumarle 1
                        // No podemos operar con el tamaño de la lista porque,
                        // si elimino y añado registros, pueden repetirse IDs
                        val idProducto: Int


                        // Si estoy pasando un ID en lo que estoy haciendo, significa que
                        // estoy haciendo una modificación, porque el ID ya existe.
                        // Si no, estoy haciendo una inserción, que es cuando doy el ID
                        // como un ID nuevo.

                        // Hay que hacer también lo de pasarle el lambda del actualizar registro,
                        // como un parámetro de función.
                        // En todo aquello en lo que haya metido el delete.
                        if (data.hasExtra("IdProducto")) {
                            idProducto = data.getStringExtra()
                        } else {
                            // En lugar de trabajar con el método que devuelve IDs, trabajamos
                            // con el tamaño para no tengo ni idea de por qué.
                            idProducto = productoProvider.productosList.size
                            // Tras tenerlo, nos vamos al MainActivity2

                            var producto = Producto(
                                idProducto,
                                data.getStringExtra("nombre")!!,
                                data.getStringExtra("descripcion")!!,
                                data.getStringExtra("foto")!!
                            )

                            insertRegister(producto)
                        }
                    }
            }
    }

    // 02/11/2023
    // Método para mandar el intent con datos:
    private fun openDetRegister() {
        val intent = Intent(this, MainActivity2::class.java)
        activityResultLauncher.launch(intent)
    }

    // Método para obtener el ID más grande y sumarle 1,
    // para que a la hora de insertar IDs nunca se repitan:

    // Ya tengo el ProductoProvider, de haberlo usado en el insertRegister,
    // por lo que puedo usarlo para obtener el tamaño de la lista
    // ME HAGO LA FUNCIÓN EN EL PRODUCTOPROVIDER



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

        // 02/11/2023, al adapter le paso un segundo parámetro
        adapterProducto = ProductosAdapter(
            productosList = productoProvider.productosList,
            deleteRegister = {deleteRegister(it)})


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


    // 02/11/2023
    // Método para el borrado de registros
    private fun deleteRegister (posicion: Int) {
        myCollection
            .document((posicion+1).toString())
            .delete()
            .addOnSuccessListener {
                productoProvider.productosList.removeAt(posicion)
                // Informamos al adapter de que ha habido un cambio
                adapterProducto.notifyItemRemoved(posicion)
            }
    }

    // Método para el modificado de registros
    // Me traigo el producto de la Firestore, lo reasigno completo
    // y lo vuelvo a asignar a la posición en la que estaba
    private fun updateRegister(producto: Producto) {
        myCollection
            .document(producto.id.toString())
            .set(
                hashMapOf(
                    "nombre" to producto.nombre,
                    "descripcion" to producto.descripcion,
                    "foto" to producto.foto
                )
            )
            .addOnSuccessListener {
                productoProvider.productosList.set(producto.id, producto)
                adapterProducto.notifyItemChanged(producto.id)
                manager.scrollToPositionWithOffset(producto.id, 10)
            }
    }

    // En la main activity hago los métodos para borrado y update, pero NO VA
    // a ser el main quien los haga, sino el ViewHolder; voy a utilizar una función
    // lambda, que consiste en mandar funciones como parámetros, en lugar de
    // parámetros a secas.
    // Me voy al ViewHolder.
}