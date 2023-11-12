package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monsterhunterfinder.databinding.ActivityPerfilBinding
import com.bumptech.glide.Glide
import com.example.monsterhunterfinder.adapter.MultimediasAdapter
import com.example.monsterhunterfinder.adapter.ResenasAdapter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity dedicada al perfil del usuario, desde el cual es
 * posible visualizar el nombre de usuario y biografía que se
 * tienen, editarlos, visualizar la galería de fotos personal,
 * añadir fotografías a esta galería y visualizar las opiniones
 * que otros usuarios han dejado sobre ti.
 * @author Jaime
 */
class activity_perfil : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    // Se preparan activityResultLaunchers para que recojan datos de las
    // modificaciones que se hagan a los datos del usuario y de las
    // fotos que se quieran añadir, para añadirlas a la colección dedicada.
    private lateinit var activityEditarPerfilResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var activityPerfilAnadirLauncher: ActivityResultLauncher<Intent>


    private val db = FirebaseFirestore.getInstance()

    // Se recupera la colección "multimedia" de la firestore, que es una
    // galería de imágenes, y se preparan las variables necesarias para
    // la RecyclerView correspondiente
    private val coleccionMultimedia = db.collection("multimedia")
    private lateinit var multimediaProvider: MultimediaProvider
    private lateinit var multimediasAdapter: MultimediasAdapter
    val managerMultimedia = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


    // Se recupera la colección "resenas" de la firestore, que es una
    // lista de opiniones, y se preparan las variables necesarias para
    // la RecyclerView correspondiente
    private val coleccionResenas = db.collection("resenas")
    private lateinit var resenaProvider: ResenaProvider
    private lateinit var resenasAdapter: ResenasAdapter
    val managerResenas = LinearLayoutManager(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Al pulsar en la ImageView de la parte superior izquierda, se
        // inicia el proceso de edición del perfil del usuario
        binding.imagenEditar.setOnClickListener{
            abrirEditar()
        }

        activityEditarPerfilResultLauncher = registerForActivityResult(StartActivityForResult()) {
                resultado ->
            if (resultado.data!=null) {
                val datos: Intent = resultado.data!!

                // Se recogen los datos contenidos en el activityResultLauncher
                // dedicado a la edición del perfil del usuario
                val nombre = datos.getStringExtra("Nombre")
                val bio = datos.getStringExtra("Bio")

                // Y dichos datos se muestran en los campos de texto que les
                // corresponden
                binding.textoNombre.setText(nombre)
                binding.textoBio.setText(bio)
            }
        }

        iniciarRecyclerViewMultimedia()
        iniciarRecyclerViewResenas()


        binding.botonAnadirMultimedia.setOnClickListener{
            lanzarActivityAnadir()
        }


        // El activityResultLauncher dedicado a añadir imágenes a la
        // galería multimedia del usuario vuelve de la activity que
        // se lanza con datos: la id de la imagen añadida
        // (autogenerada) y el enlace de la misma
        activityPerfilAnadirLauncher = registerForActivityResult(StartActivityForResult()) {
            resultado ->
                if (resultado.data != null) {
                    val datos: Intent = resultado.data!!

                    val idMultimedia: Int = multimediaProvider.getId()
                    // Se crea un objeto Multimedia con los datos recogidos
                    // y es insertado en la colección
                    var multimedia = Multimedia(
                        idMultimedia,
                        datos.getStringExtra("enlace")!!)
                    insertarMultimedia(multimedia)
                }
        }

    }


    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    /**
     * Función que abre la activity dedicada a la edición del
     * perfil del usuario, junto con un activityResultLauncher
     * que recogerá datos de dicha activity y los traerá de vuelta.
     */
    fun abrirEditar() {
        val intent = Intent(this, activity_editar_perfil::class.java)
        activityEditarPerfilResultLauncher.launch(intent)
    }


    /**
     * Método que inicializa una RecyclerView con el contenido de la
     * colección dedicada a la multimedia.
     * Por ahora, a esta RecyclerView únicamente es posible añadirle
     * más imágenes.
     */
    private fun iniciarRecyclerViewMultimedia() {
        val decoration = DividerItemDecoration(this, managerMultimedia.orientation)
        binding.recyclerViewMultimedia.layoutManager = managerMultimedia

        multimediaProvider = MultimediaProvider()
        multimediasAdapter = MultimediasAdapter(multimediasList = multimediaProvider.multimediasList)
        coleccionMultimedia
            .get()
            .addOnSuccessListener {
                resultado ->
                    multimediaProvider.actualizarLista(resultado)
                    binding.recyclerViewMultimedia.adapter = multimediasAdapter
                    binding.recyclerViewMultimedia.addItemDecoration(decoration)
            }
    }


    /**
     * Método que inicializa una RecyclerView con el contenido de la
     * colección dedicada a las reseñas recibidas.
     * Una colección de reseñas recibidas asociada a un usuario nunca
     * podrá ser tocada, manipulada o editada por dicho usuario.
     */
    private fun iniciarRecyclerViewResenas() {
        val decoration = DividerItemDecoration(this, managerResenas.orientation)
        binding.recyclerViewResenas.layoutManager = managerResenas

        resenaProvider = ResenaProvider()
        resenasAdapter = ResenasAdapter(resenasList = resenaProvider.resenasList)
        coleccionResenas
            .get()
            .addOnSuccessListener {
                resultado ->
                    resenaProvider.actualizarLista(resultado)
                    binding.recyclerViewResenas.adapter = resenasAdapter
                binding.recyclerViewMultimedia.addItemDecoration(decoration)
            }
    }

    /**
     * Función que abre la activity dedicada a la adición de
     * imágenes a la galería, junto con un activityResultLauncher
     * que recogerá datos de dicha activity y los traerá de vuelta.
     */
    fun lanzarActivityAnadir() {
        val intent = Intent(this, activity_perfil_anadir::class.java)
        activityPerfilAnadirLauncher.launch(intent)
    }


    /**
     * Función que llama a la variable asociada a la colección de la multimedia
     * y le inserta un registro, utilizando para generar los campos del
     * mismo los datos contenidos por una data class de tipo Multimedia que se
     * le pase por parámetro.
     * @param entradaDiario: objeto data class de tipo Multimedia cuyos datos
     * se utilizan para generar los campos del registro.
     */
    private fun insertarMultimedia(multimedia: Multimedia) {
        coleccionMultimedia
            .document(multimedia.id.toString())
            .set(
                hashMapOf(
                    "enlace" to multimedia.enlace
                )
            )
            .addOnSuccessListener {
                multimediaProvider.multimediasList.add(multimedia.id, multimedia)
                multimediasAdapter.notifyItemInserted(multimedia.id)
                managerMultimedia.scrollToPositionWithOffset(multimedia.id, 35)
            }
    }
}