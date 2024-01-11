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

    // ActivityResultLauncher común para seleccionar vídeos y fotos
    // del almacenamiento del teléfono, y variables para controlar que
    // estas acciones se hayan realizado
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

        // Se va a utilizar un activityResultLauncher común, tanto para vídeo como para fotos del
        // almacenamiento del teléfono
        // Para evitar conflictos, tanto para vídeos como para fotos, seleccionar de galería
        // desactivará el botón de tomarlos en el momento, y viceversa
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
            if (resultado.data != null) {
                val data: Intent = resultado.data!!

                // Caso de que se haya seleccionado un vídeo
                if (cogeVideo) {
                    actualizarEstadoBoton()

                    mVideoView = binding.videoViewAnadir

                    // El vídeo escogido de la galería se muestra en el videoView,
                    // para que el usuario pueda ver una previsualización de lo que
                    // va a subir a la galería de su perfil
                    mVideoView.setVideoURI(data.data)
                    uriGaleriaVideo = data.data!!
                    hayVideo = true

                // Caso de que se haya seleccionado una foto
                } else if (cogeFoto) {
                    actualizarEstadoBoton()

                    uriGaleriaFoto = data.data!!
                    hayFoto = true
                }

            }
        }
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
     * Método que realiza una subida de vídeo al storage de la Firebase.
     * También llama al método subirFoto, pasándole por parámetro la uri
     * del vídeo ya almacenado en Firebase.
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
     * Método que realiza una subida de foto al storage de la Firebase.
     * También llama al método volverConFoto, pasándole por parámetro la uri
     * de la foto ya almacenada en Firebase y la uri del vídeo que vaya a
     * haber asociado.
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
        intent.action = Intent.ACTION_GET_CONTENT

        intent.type = "video/*"

        activityResultLauncher.launch(intent)
        cogeVideo = true
    }

    /**
     * Método que permite al usuario seleccionar fotos de
     * la galería para añadirlas, asociadas cada una a un
     * vídeo
     */
    private fun SeleccionarFotoDeGaleria() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT

        intent.type = "image/*"

        activityResultLauncher.launch(intent)
        cogeFoto = true
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
     * con un intent que incluye las uris de la foto y del vídeo añadidos,
     * permitiendo emplearlas para realizar el insert de un elemento que
     * emplea ambas uris en la RecyclerView de la galería del usuario.
     */
    fun volverConFoto(uriVideo: Uri, uriFoto: Uri) {
        val intent = Intent()
        intent.putExtra("uri_foto", uriFoto.toString())
        intent.putExtra("uri_video", uriVideo.toString())
        setResult(FOTO_INSERTADA, intent)
        finish()
    }
}