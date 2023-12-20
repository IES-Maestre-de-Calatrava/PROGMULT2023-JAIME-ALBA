package com.example.monsterhunterfinder

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import com.example.monsterhunterfinder.databinding.ActivityVideoBinding
import com.google.firebase.firestore.FirebaseFirestore

class activity_video : AppCompatActivity() {


    private lateinit var binding: ActivityVideoBinding

    // Se recoge la instancia de la Firebase y el documento empleado
    // para almacenar el contenido multimedia
    private val db = FirebaseFirestore.getInstance()
    private val coleccionVideos = db.collection("multimedia")


    // Preparamos una variable String vacía que se igualará
    // a la String contenida en el putExtra para así obtener
    // el identificador del objeto multimedia que contiene
    // la uri del vídeo a reproducir
    private lateinit var identificador: String

    // Para poner controlador al vídeo
    lateinit var mediaController: MediaController

    // Variable para el videoView
    lateinit var mVideoView: VideoView

    // Variables para controlar el estado de reproducción del vídeo
    private var currentPosition: Int = 0
    private var isVideoPlaying: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        Log.d("TAG", "En el onCreate")

        // Vamos a recoger el intent lanzado al pulsar en un objeto multimedia,
        // y con él, el identificador que hace referencia a la imagen asociada
        // al vídeo que debe reproducirse
        val objetoIntent: Intent = intent
        identificador = objetoIntent.getStringExtra("Identificador").toString()

        // Bindeamos el videoView utilizado para ver el vídeo asociado
        // a la imagen en la que hayamos pulsado
        mVideoView = binding.videoViewVer

        // Verificar si hay un estado guardado antes de iniciar la reproducción, para que el
        // vídeo solamente se cargue una vez, al iniciarse la activity por primera vez
        if (savedInstanceState == null) {
            cargarVideo()
        }
    }

    /**
     * Método que carga, desde el almacenamiento de Firebase, el vídeo
     * que va asociado a la imagen en la que se ha pulsado en la
     * RecyclerView del perfil de usuario.
     */
    private fun cargarVideo() {
        coleccionVideos.document(identificador).get().addOnSuccessListener {
            mVideoView.setVideoURI(Uri.parse(it.get("uri").toString()))

            mVideoView.setOnPreparedListener{
                // Se hace un prepareListener para en el video view cargar el vídeo y entonces habilitar
                // t.odo lo que le ponga aquí dentro, NO antes de cargarlo
                mVideoView.start()
                mVideoView.requestFocus() // Le pongo el foco
                ponerControles(mVideoView) // Le pongo los controles
            }
        }
    }

    /**
     * Método para poner los controles al reproductor de vídeo
     */
    private fun ponerControles(videoView: VideoView) {
        mediaController = MediaController(videoView.context)
        // Le decimos dónde queremos poner esos controles
        mediaController.setAnchorView(videoView as View) // Se lo ponemos en la propia vista
        videoView.setMediaController(mediaController)
    }


    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding= ActivityVideoBinding.inflate(layoutInflater)
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

    override fun onPause() {
        super.onPause()
        Log.d("TAG", "En el onPause")
        if (mVideoView.isPlaying) {
            // Si había vídeo reproduciéndose, se guarda el estado correspondiente
            // para la variable y la posición que había en la reproducción, y se
            // pausa el reproductor
            currentPosition = mVideoView.currentPosition
            Log.d("TAG", "La reproducción iba por $currentPosition")
            mVideoView.pause()
            isVideoPlaying = true
        } else {
            isVideoPlaying = false
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        Log.d("TAG", "En el onSaveInstanceState")
        // En el onSaveInstanceState guardamos la posición
        // por la que iba la reproducción del vídeo
        bundle.putInt("currentPosition", mVideoView.currentPosition)
        bundle.putBoolean("isVideoPlaying", isVideoPlaying)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "En el onDestroy")
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        Log.d("TAG", "En el onRestoreInstanceState")
        // En el onRestoreInstanceState, tras el onCreate, recogemos la
        // posición por la que se encontraba la reproducción y el booleano
        // que indica si había vídeo reproduciéndose
        currentPosition = bundle.getInt("currentPosition")
        isVideoPlaying = bundle.getBoolean("isVideoPlaying")
        Log.d("TAG", "La reproducción iba por $currentPosition")

        mVideoView.seekTo(currentPosition)
        if (bundle.getBoolean("isPlaying")) {
            mVideoView.start()
        }
    }

    override fun onResume() {
        super.onResume()
        // Tras cargar los datos se iguala la posición
        // a la que ya había, y si había en proceso una reproducción,
        // ésta se inicia
        if (isVideoPlaying) {
            mVideoView.seekTo(currentPosition)
            mVideoView.start()
        }
    }
}