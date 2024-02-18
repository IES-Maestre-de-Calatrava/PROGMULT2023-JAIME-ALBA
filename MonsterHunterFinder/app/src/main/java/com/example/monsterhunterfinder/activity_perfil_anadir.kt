package com.example.monsterhunterfinder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.monsterhunterfinder.databinding.ActivityPerfilAnadirBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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
    var hayVideo = false
    var hayFoto = false

    var cogeVideo = false
    var cogeFoto = false

    // Variable para el videoView
    private var mVideoView: VideoView? = null

    // Uri del vídeo escogido (de la galería)
    lateinit var uriGaleriaVideo: Uri

    // Uri de la imagen escogida (de la galería)
    lateinit var uriGaleriaFoto: Uri

    // Declaración del acceso al almacenamiento de Firebase
    private val storage = FirebaseStorage.getInstance()
    private val videoStorage = storage.reference

    // Variables para los permisos de grabar vídeos y hacer fotos
    private val PETICION_PERMISO_GRABACION = 0
    private val PETICION_PERMISO_CAMARA = 1

    // Variables para tomar una foto, incluirla en la galería y usarla en la aplicación
    private var fromCamera = false
    private lateinit var fotoPath: String
    private val CAPTURA_IMAGEN_GUARDAR_GALERIA = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()


        // Petición de permisos para grabar vídeo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                PETICION_PERMISO_GRABACION
            )
        }


        // Petición de permisos para hacer fotos y añadirlas a la galería
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PETICION_PERMISO_CAMARA
            )
        }


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

        // Listener para el botón de grabar vídeo
        binding.botonGrabarVideo.setOnClickListener{
            // Primero se detiene cualquier reproducción que hubiera en proceso
            pararReproduccion()

            comenzarGrabacion()
            binding.botonGaleriaVideo.visibility = View.INVISIBLE
        }

        // Listener para el botón de añadir vídeo desde la galería
        binding.botonGaleriaVideo.setOnClickListener {
            // Primero se detiene cualquier reproducción que hubiera en proceso
            pararReproduccion()

            SeleccionarVideoDeGaleria()
            binding.botonGrabarVideo.visibility = View.INVISIBLE
        }

        // Listener para el botón de hacer foto y añadirla a la galería
        binding.botonHacerFoto.setOnClickListener{
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                dispatchTakePictureIntent(true, CAPTURA_IMAGEN_GUARDAR_GALERIA)
            }
            binding.botonGaleriaFoto.visibility = View.INVISIBLE
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
                    hayVideo = true
                    cogeVideo = false
                    actualizarEstadoBoton()

                    // El vídeo escogido de la galería se muestra en el videoView,
                    // para que el usuario pueda ver una previsualización de lo que
                    // va a subir a la galería de su perfil
                    uriGaleriaVideo = data.data!!

                    // Método para iniciar la reproducción de vídeo
                    cargarMultimedia()

                // Caso de que se haya seleccionado una foto
                } else if (cogeFoto) {
                    hayFoto = true
                    cogeFoto = false

                    if (fromCamera) {
                        if (resultado.resultCode == RESULT_OK) {
                            uriGaleriaFoto = Uri.fromFile(File(fotoPath))
                            refreshGallery()
                        }
                    } else {
                        uriGaleriaFoto = data.data!!
                    }

                    actualizarEstadoBoton()
                }

            }
        }
    }

    /**
     * Método que, en caso de que el dispositivo disponga de una característica capaz de
     * atender una llamada de la cámara, realiza la llamada para permitir hacer una foto
     * e insertarla en la galería
     */
    private fun dispatchTakePictureIntent(galeria: Boolean, code: Int) {
        // En los parámetros indicamos si queremos o no que lo guarde en la galería
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE

        if (intent.resolveActivity(packageManager) != null) {
            // Llamamos a un método que crea una imagen en la galería
            // Se ejecuta si galería está a true, y no en caso contrario
            val photoFile = createImageFile(galeria)

            // Si el método devuelve una ruta
            if (photoFile != null) {
                fotoPath = photoFile!!.absolutePath

                // Le pasamos el contexto, el nombre del paquete (authorities del manifest) y la ruta al archivo de la galería
                val photoURI = FileProvider.getUriForFile(this, "com.example.monsterhunterfinder", photoFile)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                if (intent.resolveActivity(packageManager) != null) {
                    fromCamera = true
                    cogeFoto = true
                    when (code) {
                        CAPTURA_IMAGEN_GUARDAR_GALERIA -> activityResultLauncher.launch(intent)
                    }
                }
            }
        }
    }

    /**
     * Método que devuelve un archivo, que será el archivo en el que se almacene la foto
     *
     * @param galeria: variable que indica si el archivo se insertará o no en la galería
     */
    private fun createImageFile(galeria: Boolean): File? {
        var image: File? = null
        // Crea nombres de archivos usando la fecha y la hora del sistema (coger los imports de java)
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"

        // Una cosa va a ser el nombre del archivo y otra la carpeta en que lo almacenamos
        // (galería o almacenamiento interno privado de la aplicación)
        var storageDir: File? = null

        storageDir =
            if (galeria) {
                // La parte que nos interesa; que lo coja y lo meta en la galería
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/Camera/"
                )
            } else {
                // No hace nada pero lo dejamos así, el método da problemas
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            }

        // Si el directorio no existe, lo creamos
        if (!storageDir!!.exists()) {
            storageDir.mkdirs()
        }

        // Y creamos el archivo; lo creamos temporal y lo vamosa devolver para que
        // meta algo en él

        // Le pasamos el imageFileName, nombre del fichero
        // Le pasamos la extensión
        // Le pasamos el directorio donde queremos meterlo
        image = File.createTempFile(
            imageFileName, ".jpeg",storageDir
        )

        return image
    }

    /**
     * Método que refresca la galería, para que sean
     * visibles los cambios hechos en su contenido
     */
    private fun refreshGallery() {
        val f = File(fotoPath)
        MediaScannerConnection.scanFile(this, arrayOf(f.toString()), null, null)
    }

    /**
     * Método que inicia la grabación de vídeo para reproducirlo en la aplicación
     * y posteriormente subirlo al storage de la Firebase y asignarlo a un item
     * de la galería del perfil del usuario.
     */
    private fun comenzarGrabacion() {
        val intent = Intent()

        intent.action = MediaStore.ACTION_VIDEO_CAPTURE

        if (intent.resolveActivity(packageManager) != null) {
            activityResultLauncher.launch(intent)
            cogeVideo = true
        }
    }


    /**
     * Método que verifica si el reproductor de vídeo con el que se trabaja
     * es nulo, lo crea en caso afirmativo y, en la propia creación, carga
     * desde el almacenamiento de Firebase el vídeo que va asociado a la
     * imagen en la que se ha pulsado en la RecyclerView del perfil de usuario.
     */
    private fun cargarMultimedia() {

        if (mVideoView == null) {
            // Ligamos el reproductor a la VideoView del layout
            mVideoView = binding.videoViewAnadir
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            // El reproductor de vídeo se creará usando como URI la del vídeo de la galería
            mVideoView!!.setVideoURI(uriGaleriaVideo)
        }

        // Tras el proceso de creación, la reproducción se inicia
        mVideoView!!.setBackgroundColor(Color.TRANSPARENT)
        mVideoView!!.start()
    }


    /**
     * Método empleado para detener la reproducción cuando se presiona el
     * botón de Stop o cuando la reproducción termina. La VideoView se
     * iguala a null para destruir la instancia que se esté utilizando.
     */
    private fun pararReproduccion() {
        if (mVideoView != null) {
            mVideoView!!.pause()

            mVideoView!!.setBackgroundColor(Color.BLACK)
            mVideoView = null
        }
    }


    /**
     * Método que se usará para habilitar o deshabilitar el botón de confirmar;
     * únicamente se habilitará cuando se hayan seleccionado un vídeo y una foto
     */
    private fun actualizarEstadoBoton() {
        if (hayVideo && hayFoto) {
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