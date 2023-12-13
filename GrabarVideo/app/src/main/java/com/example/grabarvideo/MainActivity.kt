package com.example.grabarvideo

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.example.grabarvideo.databinding.ActivityMainBinding
// La cosa:
import android.Manifest
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Hay que pedir permisos para la cámara, por ser hardware. Nos vamos al manifest.
    // Tras eso, nos declaramos el videoView.
    var videoView: VideoView? = null

    // Y una constante
    val PETICION_PERMISO_GRABACION = 0
    var mediaController: MediaController? = null

    // Como hay que llamar a una aplicación, tenemos que llamar a un activityResultLauncher
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Lo primero, comprobar los permisos
        // Meter en los imports la cosa
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {

            // Si están denegados, los volvemos a pedir
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                // Como tercer parámetro se pasa la constante con la que identificamos los permisos solicitados
                PETICION_PERMISO_GRABACION
            )
        }

        // Tras la comprobación de permisos, coger el resultado o bien de la galería o bien de la grabación
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.data != null) {
                val data: Intent = result.data!!

                // Si el activityResultLauncher tiene datos, se asigna la Uri al videoView
                if (result.resultCode == RESULT_OK) {
                    binding.videoView.setVideoURI(data!!.data)
                }
            }
        }

        binding.videoView.setOnPreparedListener{
            // Si el vídeo está preparado, lo arranco y solicito el foco
            binding.videoView!!.start()
            binding.videoView.requestFocus()
            ponerControles()
        }
    }

    // Hacemos el onRequestPermission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Este método se llama cuando hago cualquier solicitud de permisos
        // Si me está devolviendo el control, tengo que saber por qué solicitud de permisos es
        if (requestCode == PETICION_PERMISO_GRABACION) { // Que sea por los permisos que me he definido yo
            // Si hay un array con dos permisos y ambos han sido aceptados
            if (grantResults.size == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                binding.buttonGrabar.visibility = View.VISIBLE

            } else {
                // Si no se cumple todo ésto, lo desactivamos
                // La app va a seguir funcionando, pero sin ese botón
                binding.buttonGrabar.visibility = View.INVISIBLE
            }
        }
    }

    private fun ponerControles() {
        mediaController = MediaController(this)
        // Lo anclamos al padre
        mediaController!!.setAnchorView(binding.videoView!!.parent as View)
        binding.videoView.setMediaController(mediaController)
    }


    // Aquí empieza la chicha: método para grabar
    fun comenzarGrabacion(view: View) {
        val intent = Intent()
        // Indico la acción del intent
        intent.action = MediaStore.ACTION_VIDEO_CAPTURE

        // Le voy a ir metiendo opciones al intent para el tema de la grabación
        //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0) // Le voy calidad baja
        //intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10) // Tiempo límite de grabación en segundos que quiero establecer

        // Estoy solicitando un intent al sistema si hay una aplicación para grabar vídeos. Si no la hay, nuestra app crashea.
        // Este if comprueba que haya alguna aplicación capaz de trabajar con ese intent que yo le he lanzado a Android.
        // A partir de la versión 30, en el manifest hay que meter la parte de las queries.
        if (intent.resolveActivity(packageManager) != null) {
            activityResultLauncher.launch(intent)
        }

        // Está sin hacer el seleccionar vídeo de la galería, pero está hecho del otro día
    }


    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}