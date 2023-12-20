package com.break4learning.gestionfotos_v34

// Añadir el import del manifest
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.break4learning.gestionfotos_v34.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    // En el manifest están las líneas necesarias para los permisos de usuario y las queries.
    // Hay también una línea dedicada a un provider, que se mete en el manifest y accede a
    // las imágenes de la galería.
    // Para el provider se usa también un recurso dcim_camera. Dentro de la carpeta xml hay
    // un archivo dcim_camera, que es al que se accede.
    /**
     * Binding
     */
    // KDoc: File > Settings > KDoc-er Kotlin Doc Generator
    private lateinit var binding: ActivityMainBinding

    // 0.- Petición de permisos para la cámara
    val PETICION_PERMISO_CAMARA = 321

    // 1.- Carga imagen de la galería
    lateinit var activityResultLauncherCargarImagenDeGaleria: ActivityResultLauncher<Intent>

    // 2.- Hacer foto y obtener miniatura
    // Hay que ser conscientes de que de esta manera no tenemos la imagen con la resolución
    // de la cámara, sino una miniatura
    lateinit var activityResultLauncherHacerFotoThumbnail: ActivityResultLauncher<Intent>

    // 3.- Hacer foto completa y añadir a la galería
    private val CAPTURA_IMAGEN_GUARDAR_GALERIA = 3
    private var fotoPath = ""
    lateinit var activityResultLauncherCapturaImagenYGuardarEnGaleria: ActivityResultLauncher<Intent>

    // 4.- Hacer foto y redimensionar a escala
    private val CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA = 4
    lateinit var activityResultLauncherRedimensionarImagen: ActivityResultLauncher<Intent>

    // 5.- Hacer foto y redimensionar a tamaño de ImageView
    private val CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA2 = 5
    lateinit var activityResultLauncherRedimensionarImagen2: ActivityResultLauncher<Intent>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearObjetosDelXml()

        // 0.- Petición de permisos para la cámara
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {

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


        // 1.- Carga imagen de la galería
        binding.btnSeleccionarDeGaleria.setOnClickListener {
            cargarImagen()
        }
        // Hemos cargado la imagen, y tras eso tenemos que CAPTURAR lo que me
        // haya devuelto la galería
        activityResultLauncherCargarImagenDeGaleria =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
                if (result.data != null) {
                    val data: Intent = result.data!!

                    if (result.resultCode == RESULT_OK) {
                        val uri = data!!.data
                        binding.imageView.setImageURI(uri)
                    }
                }
            }


        // 2.- Hacer foto y obtener miniatura (cambio al listener)
        binding.btnImg.setOnClickListener {
            hacerFotoThumbnail()
        }
        // Hemos cargado la imagen, y tras eso tenemos que CAPTURAR lo que me
        // haya devuelto la galería
        activityResultLauncherHacerFotoThumbnail =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result ->
                if (result.data != null) {
                    val data: Intent = result.data!!

                    if (result.resultCode == RESULT_OK) {
                        val extras = data.extras
                        val imageBitmap = extras!!["data"] as Bitmap
                        binding.imageView.setImageBitmap(imageBitmap)
                    }
                }
            }


        // 3.- Hacer foto completa y añadir a la galería
        binding.btnGuardarEnGaleria.setOnClickListener {
            // Comprobamos que haya apps capaces de atender la llamada para fotos
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                dispatchTakePictureIntent(true, CAPTURA_IMAGEN_GUARDAR_GALERIA)
            }
        }

        activityResultLauncherCapturaImagenYGuardarEnGaleria =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result ->
                if (result.data != null) {
                    val data: Intent = result.data!!

                    if (result.resultCode == RESULT_OK) {
                        binding.imageView.setImageURI(Uri.fromFile(File(fotoPath)))

                        // Aunque hayamos metido el archivo en galería, el programa no se entera
                        // Hay que decirle que refresque para que se pueda ver
                        refreshGallery()
                    }
                }
            }


        // 4.- Hacer foto y redimensionar a escala
        binding.btnRedi.setOnClickListener {
            // Comprobamos que haya apps capaces de atender la llamada para fotos
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                dispatchTakePictureIntent(true, CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA)
            }
        }

        activityResultLauncherRedimensionarImagen =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result ->
                if (result.data != null) {
                    val data: Intent = result.data!!

                    if (result.resultCode == RESULT_OK) {
                        // En lugar de al Set Uri, llamamos a un método que se va a encargar
                        // de hacer el redimensionado
                        setPicRedim()
                        refreshGallery()
                    }
                }
            }


        // 5.- Hacer foto y redimensionar a tamaño de la ImageView
        binding.btnRedi2.setOnClickListener {
            // Comprobamos que haya apps capaces de atender la llamada para fotos
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                dispatchTakePictureIntent(true, CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA2)
            }
        }

        activityResultLauncherRedimensionarImagen2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result ->
                if (result.data != null) {
                    val data: Intent = result.data!!

                    if (result.resultCode == RESULT_OK) {
                        // En lugar de al Set Uri, llamamos a un método que se va a encargar
                        // de hacer el redimensionado
                        setPicRedim2()
                        refreshGallery()
                    }
                }
            }

    }

    // 1.- Carga imagen de la galería
    /**
     * Cargar imagen de galería
     * 1.- Carga imagen de la galería
     */
    private fun cargarImagen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (intent.resolveActivity(packageManager) != null) {
            activityResultLauncherCargarImagenDeGaleria.launch(intent)
        }
    }

    // 2.- Hacer foto y obtener miniatura
    /**
     * Hacer foto thumbnail
     */
    private fun hacerFotoThumbnail() {
        // Comprobar si hay apps capaces de atender llamadas relacionadas con
        // hacer fotos
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            val intent = Intent()
            intent.action = MediaStore.ACTION_IMAGE_CAPTURE

            if (intent.resolveActivity(packageManager) != null) {
                activityResultLauncherHacerFotoThumbnail.launch(intent)
            }
        }
    }

    // 3.- Hacer foto completa y añadir a la galería

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
                val photoURI = FileProvider.getUriForFile(this, "com.break4learning.gestionfotos_v34", photoFile)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                if (intent.resolveActivity(packageManager) != null) {
                    when (code) {
                        // Según el código, se llama a un activityResultLauncher o a otro
                        CAPTURA_IMAGEN_GUARDAR_GALERIA -> activityResultLauncherCapturaImagenYGuardarEnGaleria.launch(intent)
                        CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA -> activityResultLauncherRedimensionarImagen.launch(intent)
                        CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA2 -> activityResultLauncherRedimensionarImagen2.launch(intent)
                    }
                }
            }
        }
    }

    /**
     * Redimensionar una imagen
     */
    private fun setPicRedim() {
        val bmOptions = BitmapFactory.Options()

        // Le indicamos el factor de reducción
        // 0 es sin reducción, a mayor número mayor reducción
        val scaleFactor = 100

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        val bitmap = BitmapFactory.decodeFile(fotoPath, bmOptions)

        binding.imageView.setImageBitmap(bitmap)
    }

    /**
     * Redimensiona una imagen al tamaño del ImageView que la
     * va a contener
     */
    private fun setPicRedim2() {
        // Lo primero es obtener las dimensiones del ImageView
        val targetW: Int = binding.imageView.width
        val targetH: Int = binding.imageView.height

        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fotoPath, bmOptions)

        // Primero saco las dimensiones del ImageView y luego genero la foto adaptada
        // a esas dimensiones; ya luego si quiero reescalarla la reescalo, pero primero
        // necesito tener cargada una foto con la que poder trabajar

        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Fórmula matemática para ver qué reducción tengo que aplicar en función
        // de las dimensiones del ImageView con el que estoy trabajando

        // De entre los dos factores de reducción que salen me quedo con el más
        // pequeño para que la imagen resultante pese menos

        // Si cojo el factor de reducción más grande, en su lado se va a ver bien,
        // pero en el otro lado se va a ver mal
        val scaleFactor = Math.min(photoW/targetW, photoH/targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        Log.d("DIMENSIONES", "Escala = $scaleFactor")
        val bitmap = BitmapFactory.decodeFile(fotoPath, bmOptions)
        binding.imageView.setImageBitmap(bitmap)
    }


    // Devuelve un archivo que va a ser donde se almacene la foto
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
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/Camera/"
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


    private fun refreshGallery() {
        val f = File(fotoPath)
        MediaScannerConnection.scanFile(this, arrayOf(f.toString()), null, null)
    }




    /**
     * Crear objetos del xml
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PETICION_PERMISO_CAMARA) {
            if (grantResults.size == 3 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                ///Hacer lo que queráis
            } else {
                ///Hacer lo que queráis
            }
        }
    }

    // Nos ha dejado Javier en el control de versiones un método getPath para recuperar uris
    // de SQLite
    // Ahora el RecyclerView tiene que poder subir fotos con un redimensionado correcto
}