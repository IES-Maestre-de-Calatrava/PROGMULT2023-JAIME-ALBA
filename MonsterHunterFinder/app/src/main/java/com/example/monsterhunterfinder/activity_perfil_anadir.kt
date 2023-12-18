package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.monsterhunterfinder.databinding.ActivityPerfilAnadirBinding
import com.google.firebase.storage.FirebaseStorage

/**
 * Activity dedicada a la adición de imágenes a la
 * galería que el usuario tiene disponible en su perfil.
 * Deberá hacerse mediante el enlace de la imagen.
 * @author Jaime
 */
class activity_perfil_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilAnadirBinding
    private val FOTO_INSERTADA: Int = 1

    // ActivityResultLauncher para volver con vídeos desde la galería
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    // Variable para el videoView
    lateinit var mVideoView: VideoView

    // Uri del vídeo escogido (de la galería)
    lateinit var uriGaleria: Uri

    // Declaración del acceso al almacenamiento de Firebase
    private val storage = FirebaseStorage.getInstance()
    private val videoStorage = storage.reference
    // Variable para controlar si hay vídeo
    var hayVideo = false

    // Para poner controlador al vídeo
    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Inicialmente, el botón de confirmar es invisible.
        // Se hará visible solamente cuando se haya seleccionado un
        // vídeo de la galería Y exista texto en la caja para el
        // enlace de la foto, de manera que únicamente se pueda confirmar
        // cuando existan ambas cosas
        binding.botonConfirmarAnadir.visibility = View.INVISIBLE

        // Se establece un listener para el botón de confirmar;
        // el método volverConFoto() será el que envíe un intent
        // a la activity anterior a la presente, conteniendo
        // en dicho intent el enlace de la imagen que se quiera
        // añadir a la galería
        binding.botonConfirmarAnadir.setOnClickListener {
            subirVideo()
        }

        // Listener para el botón de añadir desde la galería
        binding.botonGaleria.setOnClickListener {
            SeleccionarDeGaleria()
        }

        // Listener para la caja de texto del enlace de la foto
        binding.textoEnlaceFoto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarEstadoBoton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        // Registramos el contenido con el que vuelva el activityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultado ->
                if (resultado.data != null) {
                    val data: Intent = resultado.data!!

                    // El vídeo escogido de la galería se muestra en el videoView,
                    // para que el usuario pueda ver una previsualización de lo que
                    // va a subir
                    mVideoView = binding.videoViewAnadir

                    // Al videoView se le asigna el resultado obtenido de la galería
                    mVideoView.setVideoURI(data.data)
                    uriGaleria = data.data!!
                    hayVideo = true


                    mVideoView.setOnPreparedListener{
                        // Se hace un prepareListener para en el video view cargar el vídeo y entonces habilitar
                        // t.odo lo que le ponga aquí dentro, NO antes de cargarlo
                        mVideoView.start()
                        mVideoView.requestFocus() // Le pongo el foco
                        ponerControles(mVideoView) // Le pongo los controles
                    }

                }
        }
    }

    /**
     * Método que se usará para habilitar o deshabilitar el botón de confirmar;
     * únicamente se habilitará cuando se haya seleccionado un vídeo de la
     * galería y exista texto en la caja de texto para el enlace de la foto
     */
    private fun actualizarEstadoBoton() {
        val textoEnlaceFoto = binding.textoEnlaceFoto.text.toString().trim()

        if (hayVideo && textoEnlaceFoto.isNotEmpty()) {
            binding.botonConfirmarAnadir.visibility = View.VISIBLE
        } else {
            binding.botonConfirmarAnadir.visibility = View.INVISIBLE
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
     * Método que, tras elegir un vídeo de la galería, realiza una subida del mismo
     * a la storage de la Firebase y manda el resultado obtenido al método que se encargará
     * de volver a la activity anterior empleando tanto la foto seleccionada como el
     * vídeo elegido.
     */
    private fun subirVideo() {
        val videoName = "${System.currentTimeMillis()}.mp4"
        val videoRef = videoStorage.child("video/$videoName")

        val uploadTask = videoRef.putFile(uriGaleria)
        uploadTask.addOnSuccessListener {
            videoRef.downloadUrl.addOnSuccessListener {
                resultado ->
                    volverConFoto(resultado)
            }
        }
    }

    /**
     * Método que permite al usuario seleccionar vídeos de
     * la galería para añadirlos, asociados cada uno a una
     * foto
     */
    private fun SeleccionarDeGaleria() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK

        intent.type = "video/*"

        activityResultLauncher.launch(intent)
    }

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityPerfilAnadirBinding.inflate(layoutInflater)
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
     * Función que vuelve a la activity que ha llamado a la presente
     * con un intent que incluye datos recogidos de la caja de
     * texto presente en el layout de esta activity.
     * Para recoger dichos datos, será necesario un activityResultLauncher
     * en la activity que ha llamado a la presente.
     * Los datos que se recogen son el enlace de la imagen que
     * será añadida a la galería.
     */
    fun volverConFoto(uri: Uri) {
        val intent = Intent()
        intent.putExtra("enlace", binding.textoEnlaceFoto.text.toString())
        intent.putExtra("uri_video", uri.toString())
        setResult(FOTO_INSERTADA, intent)
        finish()
    }
}