package com.example.monsterhunterfinder

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.monsterhunterfinder.databinding.ActivityPerfilAnadirBinding
import com.google.firebase.storage.FirebaseStorage

/**
 * Activity dedicada a la adición de imágenes a la
 * galería que el usuario tiene disponible en su perfil,
 * así como a los vídeos que tendrán asociados cada una
 * de ellas.
 *
 * @author Jaime
 */
class activity_perfil_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilAnadirBinding
    private val FOTO_INSERTADA: Int = 1

    // ActivityResultLauncher para los vídeos
    lateinit var activityResultLauncherVideo: ActivityResultLauncher<Intent>
    // ActivityResultLauncher para las fotos
    lateinit var activityResultLauncherFoto: ActivityResultLauncher<Intent>

    // PARA PRUEBAS
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var cogeVideo = false
    var cogeFoto = false


    // Variable para el videoView
    lateinit var mVideoView: VideoView

    // Uri del vídeo escogido (de la galería)
    lateinit var uriGaleriaVideo: Uri

    // Uri de la imagen escogida (de la galería)
    lateinit var uriGaleriaFoto: Uri

    // Declaración del acceso al almacenamiento de Firebase
    private val storage = FirebaseStorage.getInstance()
    private val videoStorage = storage.reference
    // Variables para controlar si hay vídeo y foto
    var hayVideo = false
    var hayFoto = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Inicialmente, el botón de confirmar es invisible.
        // Se hará visible solamente cuando se hayan seleccionado un
        // vídeo y una foto, de manera que únicamente se pueda confirmar
        // cuando existan ambas cosas
        binding.botonConfirmarAnadir.visibility = View.INVISIBLE

        // Se establece un listener para el botón de confirmar;
        // El método subirVideo() realizará una subida al storage
        // de la Firebase del vídeo elegido, y la llamada interior a
        // subirFoto() y volverConFoto() enviará un intent
        // a la activity anterior a la presente, conteniendo
        // las uris del vídeo y la foto subidos.
        binding.botonConfirmarAnadir.setOnClickListener {
            subirVideo()
        }

        // Listener para el botón de añadir vídeo desde la galería
        binding.botonGaleriaVideo.setOnClickListener {
            SeleccionarVideoDeGaleria()
            binding.botonGrabarVideo.visibility = View.INVISIBLE
        }

        // Listener para el botón de añadir foto desde la galería
        binding.botonGaleriaFoto.setOnClickListener {
            SeleccionarFotoDeGaleria()
            binding.botonHacerFoto.visibility = View.INVISIBLE
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
            if (resultado.data != null) {
                val data: Intent = resultado.data!!

                if (cogeVideo) {

                    mVideoView = binding.videoViewAnadir

                    // Al videoView se le asigna el resultado obtenido de la galería
                    mVideoView.setVideoURI(data.data)
                    uriGaleriaVideo = data.data!!
                    hayVideo = true
                } else if (cogeFoto) {

                    uriGaleriaFoto = data.data!!
                    hayFoto = true
                }

            }
        }

        /*
        // Registramos el contenido con el que vuelva el activityResultLauncher del vídeo
        activityResultLauncherVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultado ->
                if (resultado.data != null) {
                    val data: Intent = resultado.data!!

                    // El vídeo escogido de la galería se muestra en el videoView,
                    // para que el usuario pueda ver una previsualización de lo que
                    // va a subir
                    mVideoView = binding.videoViewAnadir

                    // Al videoView se le asigna el resultado obtenido de la galería
                    mVideoView.setVideoURI(data.data)
                    uriGaleriaVideo = data.data!!
                    hayVideo = true
                }
        }

        // Registramos el contenido con el que vuelva el activityResultLauncher del vídeo
        activityResultLauncherFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultado ->
            if (resultado.data != null) {
                val data: Intent = resultado.data!!
                uriGaleriaFoto = data.data!!
                hayFoto = true
            }
        }*/

    }

    /**
     * Método que se usará para habilitar o deshabilitar el botón de confirmar;
     * únicamente se habilitará cuando se hayan seleccionado un vídeo y una foto
     */
    private fun actualizarEstadoBoton() {
        if (cogeVideo && cogeFoto) {
            binding.botonConfirmarAnadir.visibility = View.VISIBLE
        } else {
            binding.botonConfirmarAnadir.visibility = View.INVISIBLE
        }
    }


    /**
     * Método que realiza una subida de vídeo a la storage de la Firebase
     * y manda el resultado obtenido al método que se encargará de volver
     * a la activity anterior empleando tanto la foto seleccionada como el
     * vídeo elegido.
     */
    private fun subirVideo() {
        val videoName = "${System.currentTimeMillis()}.mp4"
        val videoRef = videoStorage.child("video/$videoName")

        val uploadTask = videoRef.putFile(uriGaleriaVideo)
        uploadTask.addOnSuccessListener {
            videoRef.downloadUrl.addOnSuccessListener {
                resultado ->
                    subirFoto(resultado)
            }
        }
    }

    /**
     * Método que realiza una subida de foto a la storage de la Firebase
     */
    private fun subirFoto(uri: Uri) {
        val fotoName = "${System.currentTimeMillis()}.jpg"
        val fotoRef = videoStorage.child("foto/$fotoName")

        val uploadTask = fotoRef.putFile(uriGaleriaFoto)
        uploadTask.addOnSuccessListener {
            fotoRef.downloadUrl.addOnSuccessListener {
                    resultado ->
                        volverConFoto(uri, resultado)
            }
        }
    }

    /**
     * Método que permite al usuario seleccionar vídeos de
     * la galería para añadirlos, asociados cada uno a una
     * foto
     */
    private fun SeleccionarVideoDeGaleria() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK

        intent.type = "video/*"

        activityResultLauncher.launch(intent)
        cogeVideo = true
        actualizarEstadoBoton()
    }

    /**
     * Método que permite al usuario seleccionar fotos de
     * la galería para añadirlas, asociadas cada una a un
     * vídeo
     */
    private fun SeleccionarFotoDeGaleria() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK

        intent.type = "image/*"

        activityResultLauncher.launch(intent)
        cogeFoto = true
        actualizarEstadoBoton()
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
    fun volverConFoto(uriVideo: Uri, uriFoto: Uri) {
        val intent = Intent()
        intent.putExtra("uri_foto", uriFoto.toString())
        intent.putExtra("uri_video", uriVideo.toString())
        setResult(FOTO_INSERTADA, intent)
        finish()
    }
}