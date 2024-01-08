package com.example.monsterhunterfinder

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.VideoView
import com.example.monsterhunterfinder.databinding.ActivityVideoBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity asociada a la reproducción de los distintos vídeos
 * que van asociados a las imágenes que un usuario sube a su
 * perfil.
 *
 * @author Jaime
 */
class activity_video : AppCompatActivity() {


    private lateinit var binding: ActivityVideoBinding

    // Se recoge la instancia de la Firebase y el documento empleado
    // para almacenar el contenido multimedia
    private val db = FirebaseFirestore.getInstance()
    private val coleccionVideos = db.collection("multimedia")

    // Variable para la uri obtenida de la Firebase
    private lateinit var uriFire: String


    // Preparamos una variable String vacía que se igualará
    // a la String contenida en el putExtra para así obtener
    // el identificador del objeto multimedia que contiene
    // la uri del vídeo a reproducir
    private lateinit var identificador: String

    // Variable para el videoView
    private var mVideoView: VideoView? = null

    // Variables para controlar el estado de reproducción del vídeo
    private var pos: Int = 0
    companion object {
        // Controla que haya una reproducción en proceso
        var isPlaying = false
        // Controla el estado de pausa
        var isPaused = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        Log.d("TAG", "En el onCreate")


        // Vamos a recoger el intent lanzado al pulsar en un objeto multimedia,
        // y con él, el identificador que hace referencia a la imagen asociada
        // al vídeo que debe reproducirse
        val objetoIntent: Intent = intent
        identificador = objetoIntent.getStringExtra("Identificador").toString()
        Log.d("MultimediaLog"," El identificador del elemento multimedia vale $identificador")


        // Tengo que realizar un proceso de creación cuando el reproductor de
        // vídeo sea nulo
        if (mVideoView == null) {
            // Ligamos el reproductor a la VideoView del layout
            mVideoView = binding.videoViewVideo
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            // El reproductor de vídeo se creará usando como URI aquella que se
            // haya almacenado en la Firestore, asociada al objeto multimedia en
            // el que se haya presionado
            coleccionVideos.document(identificador).get().addOnSuccessListener {
                uriFire = (Uri.parse(it.get("uri").toString())).toString()
                mVideoView!!.setVideoURI(Uri.parse(it.get("uri").toString()))
                Log.d("MultimediaLog","Vídeo cargado con uri $uriFire")


                // Código inicialmente contenido en el onResume
                if (mVideoView != null) {

                    // Usamos la variable que habíamos empleado para guardar la posición de la
                    // reproducción para llevar la reproducción directamente hasta ese punto.
                    Log.d("MultimediaLog","Se hace el seekTo a la posición $pos")
                    mVideoView!!.seekTo(pos)

                    // Si había una reproducción en proceso y no se había pausado el reproductor,
                    // la reproducción se inicia. Si no, se busca la posición por la que iba la
                    // reproducción, pero ésta no se inicia.
                    if (isPlaying && !isPaused) {
                        mVideoView!!.start()
                    }
                }
            }
        }

        // Siempre que giramos el dispositivo, pasamos por un proceso onPause > onSaveInstanceState >
        // onDestroy > onCreate > onRestoreInstancestate > onResume.
        // Si el savedInstanceState vale null, significa que es el primer onCreate que se hace,
        // el que tiene lugar al abrir la activity por primera vez, y que no se han hecho giros de
        // dispositivo con anterioridad.
        if (savedInstanceState == null) {
            // Estado inicial: nada habilitado salvo el botón de play
            Log.d("MultimediaLog", "En el onCreate, sin datos a recuperar");
            binding.botonPlayVideo.isEnabled = true
            binding.botonStopVideo.isEnabled = false
            binding.botonPauseVideo.isEnabled = false

            binding.botonRetrVideo.isEnabled = false
            binding.botonAvanVideo.isEnabled = false

        } else {
            Log.d("MultimediaLog", "En el onCreate, con datos a recuperar")

            // Si es un onCreate distinto del primero y hay una reproducción a
            // medias (se le ha dado al Play)
            if (isPlaying) {

                binding.botonPlayVideo.isEnabled = false
                binding.botonStopVideo.isEnabled = true
                binding.botonPauseVideo.isEnabled = true

                binding.botonRetrVideo.isEnabled = true
                binding.botonAvanVideo.isEnabled = true

            // Si es un onCreate distinto del primero y no hay una reproducción
            // a medias (nunca se pulsó Play o el último botón tocado fue Stop)
            } else {

                binding.botonPlayVideo.isEnabled = true
                binding.botonStopVideo.isEnabled = false
                binding.botonPauseVideo.isEnabled = false

                binding.botonRetrVideo.isEnabled = false
                binding.botonAvanVideo.isEnabled = false
            }
        }

        // Control de la reproducción de vídeo mediante botones
        controlVideo()
    }

    /**
     * Método que establece los listeners necesarios y les asigna
     * funcionalidades para poder manipular la reproducción del
     * vídeo mediante los botones existentes.
     */
    private fun controlVideo() {
        binding.botonPlayVideo.setOnClickListener {
            pararReproduccion()

            cargarMultimedia()
        }

        binding.botonStopVideo.setOnClickListener {
            // Detiene el reproductor en su totalidad, igualándolo a null.
            pararReproduccion()
        }

        binding.botonPauseVideo.setOnClickListener {
            if (mVideoView != null) {
                // Si estaba reproduciendo, pausa. Si no, reproduce.
                if (mVideoView!!.isPlaying) {
                    isPaused = true
                    mVideoView!!.pause()
                } else {
                    isPaused = false
                    mVideoView!!.start()
                }
            }
        }

        // Listener para el botón de retroceso
        binding.botonRetrVideo.setOnClickListener {
            retrocederReproduccion(10000)
        }

        // Listener para el botón de avance
        binding.botonAvanVideo.setOnClickListener {
            avanzarReproduccion(10000)
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
            mVideoView = binding.videoViewVideo
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            // El reproductor de vídeo se creará usando como URI aquella que se
            // haya almacenado en la Firestore, asociada al objeto multimedia en
            // el que se haya presionado
            coleccionVideos.document(identificador).get().addOnSuccessListener {
                mVideoView!!.setVideoURI(Uri.parse(it.get("uri").toString()))
            }

            isPlaying = true
        }

        // Tras el proceso de creación, la reproducción se inicia
        mVideoView!!.setBackgroundColor(Color.TRANSPARENT)
        mVideoView!!.start()

        // Y aquí jugamos con habilitar o deshabilitar botones para evitar errores.
        // Si hay reproducción activa, deshabilito el botón play. A los otros sí
        // se les puede dar.
        binding.botonPlayVideo.isEnabled = false
        binding.botonStopVideo.isEnabled = true
        binding.botonPauseVideo.isEnabled = true
        binding.botonRetrVideo.isEnabled = true
        binding.botonAvanVideo.isEnabled = true
    }

    /**
     * Función que atrasa la reproducción, restando tiempo
     * a la currentPosition de la VideoView.
     * El coerceIn se usa para vigilar que la posición final
     * esté dentro de un rango válido del tiempo de ejecución.
     *
     * @param millis: Número entero que equivale al momento actual de la reproducción
     */
    private fun retrocederReproduccion(millis: Int) {
        if (mVideoView != null) {
            val newPosition = mVideoView!!.currentPosition - millis
            mVideoView!!.seekTo(newPosition.coerceIn(0, mVideoView!!.duration))
        }
    }

    /**
     * Función que adelanta la reproducción, sumando tiempo
     * a la currentPosition de la VideoView.
     * El coerceIn se usa para vigilar que la posición final
     * esté dentro de un rango válido del tiempo de ejecución.
     *
     * @param millis: Número entero que equivale al momento actual de la reproducción
     */
    private fun avanzarReproduccion(millis: Int) {
        if (mVideoView != null) {
            val newPosition = mVideoView!!.currentPosition + millis
            mVideoView!!.seekTo(newPosition.coerceIn(0, mVideoView!!.duration))
        }
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
     * Método empleado para detener la reproducción cuando se presiona el
     * botón de Stop o cuando la reproducción termina. La VideoView se
     * iguala a null para destruir la instancia que se esté utilizando.
     */
    private fun pararReproduccion() {
        if (mVideoView != null) {
            mVideoView!!.pause()
            pos = 0
            mVideoView!!.seekTo(pos)

            mVideoView!!.setBackgroundColor(Color.BLACK)
            mVideoView = null

            binding.botonPlayVideo.isEnabled = true
            binding.botonStopVideo.isEnabled = false
            binding.botonPauseVideo.isEnabled = false
            binding.botonRetrVideo.isEnabled = false
            binding.botonAvanVideo.isEnabled = false

            isPlaying = false
            isPaused = false
        }
    }

    /**
     * Función que finaliza la activity actual, devolviendo
     * al usuario a aquella activity desde la que hubiera
     * accedido.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun volver(view: View) {
        pararReproduccion()
        finish()
    }

    override fun onPause() {
        super.onPause()
        Log.d("MultimediaLog", "En el onPause")

        if (mVideoView != null) {
            // Cuando estamos en el onPause, si había una reproducción en proceso,
            // el valor de la misma se asigna a la variable pos
            // La reproducción se pausa
            pos = mVideoView!!.currentPosition
            Log.d("MultimediaLog", "Valor de pos = $pos");
            mVideoView!!.pause()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        Log.d("MultimediaLog", "En el onSaveInstanceState")

        if (mVideoView != null) {
            bundle.putInt("posicion", pos)
            Log.d("MultimediaLog", "Valor de pos en el bundle = $pos");

            // En el onSaveInstanceState, creamos un "paquete" que más tarde se recuperará.
            // En él meto la posición por la que se hallaba la reproducción.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MultimediaLog", "En el onDestroy")

        // En el onDestroy, el reproductor de vídeo se iguala a null
        // para eliminar esa instancia
        if (mVideoView != null) {
            mVideoView = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        // Antes del onRestoreInstanceState, se pasa por el onCreate.
        // En éste, siempre se creará un reproductor de vídeo nuevo.

        super.onRestoreInstanceState(bundle!!)
        Log.d("MultimediaLog", "En el onRestoreInstanceState")

        if (bundle != null) {
            // Del "paquete" recuperamos el punto por el que se hallaba la reproducción.
            pos = bundle.getInt("posicion")
            Log.d("MultimediaLog", "Valor de pos tras revisar el bundle = $pos");
        } else {
            Log.d("MultimediaLog", "Valor de pos si no hay bundle = $pos");
        }
    }

    override fun onResume(){
        super.onResume()
        Log.d("MultimediaLog", "En el onResume");
        Log.d("MultimediaLog", "Valor de pos en el onResume = $pos");
        // El onResume se ejecuta tras el onCreate.

        // La idea inicial era restaurar en este método la posición de las reproducciones
        // que hubieran quedado a medias ante un giro del dispositivo. Sin embargo, a través
        // de los mensajes de traza es posible observar que la carga del vídeo desde Firebase
        // tarda un tiempo, tiempo suficiente para terminar después de que se haya ejecutado
        // el código contenido en el método onResume, dando lugar a un funcionamiento
        // incorrecto en el que el estado de la reproducción no se mantiene porque el
        // vídeo termina de cargar demasiado tarde.

        // Por ello, el código aquí contenido se va a mover al onCreate, al interior de
        // addOnSuccessListener, para que únicamente se ejecute en caso de que el vídeo
        // haya cargado correctamente.
    }

    // funcionamiento por defecto: ante un giro de dispositivo, la posición se guarda, pero
    // la reproducción se pausa y el juego de botones pasa a sólo play

    // si le doy a stop en el portrait: funcionan bien las reproducciones iniciadas en portrait
    // si le doy a stop en el landscape: funcionan bien las reproducciones iniciadas en el landscape
    // no pueden coexistir
}