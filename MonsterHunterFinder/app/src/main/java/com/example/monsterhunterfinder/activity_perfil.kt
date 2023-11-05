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

class activity_perfil : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var activityEditarPerfilResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var activityPerfilAnadirLauncher: ActivityResultLauncher<Intent>


    private val db = FirebaseFirestore.getInstance()

    private val coleccionMultimedia = db.collection("multimedia")
    private lateinit var multimediaProvider: MultimediaProvider
    private lateinit var multimediasAdapter: MultimediasAdapter
    val managerMultimedia = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


    private val coleccionResenas = db.collection("resenas")
    private lateinit var resenaProvider: ResenaProvider
    private lateinit var resenasAdapter: ResenasAdapter
    val managerResenas = LinearLayoutManager(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        binding.imagenEditar.setOnClickListener{
            abrirEditar()
        }

        activityEditarPerfilResultLauncher = registerForActivityResult(StartActivityForResult()) {
                resultado ->
            if (resultado.data!=null) {
                val datos: Intent = resultado.data!!

                val nombre = datos.getStringExtra("Nombre")
                val bio = datos.getStringExtra("Bio")

                binding.textoNombre.setText(nombre)
                binding.textoBio.setText(bio)
            }
        }

        iniciarRecyclerViewMultimedia()
        iniciarRecyclerViewResenas()


        binding.botonAnadirMultimedia.setOnClickListener{
            lanzarActivityAnadir()
        }

        activityPerfilAnadirLauncher = registerForActivityResult(StartActivityForResult()) {
            resultado ->
                if (resultado.data != null) {
                    val datos: Intent = resultado.data!!

                    val idMultimedia: Int = multimediaProvider.getId()
                    var multimedia = Multimedia(
                        idMultimedia,
                        datos.getStringExtra("enlace")!!)
                    insertarMultimedia(multimedia)
                }
        }

    }

    private fun crearObjetosDelXml() {
        binding=ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }

    fun abrirEditar() {
        val intent = Intent(this, activity_editar_perfil::class.java)
        activityEditarPerfilResultLauncher.launch(intent)
    }

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

    fun lanzarActivityAnadir() {
        val intent = Intent(this, activity_perfil_anadir::class.java)
        activityPerfilAnadirLauncher.launch(intent)
    }

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