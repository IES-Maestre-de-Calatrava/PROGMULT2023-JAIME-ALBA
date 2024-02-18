package com.example.monsterhunterfinder

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.example.monsterhunterfinder.databinding.ActivityAudioBinding
import java.io.IOException
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity asociada a la reproducción de los distintos audios
 * que van asociados a las reseñas que se dejan en el perfil de
 * un usuario.
 * @author Jaime
 */
class activity_audio : AppCompatActivity(), MediaPlayer.OnCompletionListener {

    private lateinit var binding: ActivityAudioBinding

    // Se recoge la instancia de la Firebase y el documento empleado
    // para almacenar el contenido de las reseñas para poder acceder
    // a los audios
    private val db = FirebaseFirestore.getInstance()
    private val coleccionResenas = db.collection("resenas")

    // Variable para la uri obtenida de la Firebase
    private lateinit var uriFire1: String
    private lateinit var uriFire2: String

    // Preparamos una variable String vacía que se igualará
    // a la String contenida en el putExtra para así obtener
    // el identificador del objeto reseña que contiene
    // la uri del audio a reproducir
    private lateinit var identificador: String

    // Variable que guarda la posición por la que se halla reproduciendo
    private var pos: Int = 0

    // Variables para controlar el estado de la reproducción: el si hay
    // una reproducción en curso y el estado de pausa de la misma
    companion object {
        var isPlaying = false
        var isPaused = false
    }

    // Preparamos el mediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()


        // Vamos a recoger el intent lanzado al pulsar en un objeto reseña,
        // y con él, el identificador que hace referencia al audio que debe
        // reproducirse
        val objetoIntent: Intent = intent
        identificador = objetoIntent.getStringExtra("Identificador").toString()
        Log.d("Audio"," El identificador del elemento multimedia vale $identificador")


        // Tengo que realizar un proceso de creación cuando el reproductor de
        // audio sea nulo
        if (mediaPlayer == null) {
            // El reproductor de audio se creará usando como URI aquella que se
            // haya almacenado en la Firestore, asociada al objeto reseña en
            // el que se haya presionado
            coleccionResenas.document(identificador).get().addOnSuccessListener {
                uriFire1 = (Uri.parse(it.get("uri").toString())).toString()
                Log.d("Audio","Vídeo cargado con uri UNO $uriFire1")
                mediaPlayer = MediaPlayer.create(this, Uri.parse(it.get("uri").toString()))
                mediaPlayer!!.setOnCompletionListener(this)

                // También inicializamos la seekBar; lo vamos a hacer con un hilo
                inicializarSeekBar()


                // Código inicialmente contenido en el onResume
                if (mediaPlayer != null) {
                    // Usamos la variable que habíamos empleado para guardar la posición de la
                    // reproducción para llevar la reproducción directamente hasta ese punto.
                    Log.d("Audio","Se hace el seekTo a la posición $pos")
                    mediaPlayer!!.seekTo(pos)

                    // Si había una reproducción en proceso y no se había pausado el reproductor,
                    // la reproducción se inicia. Si no, se busca la posición por la que iba la
                    // reproducción, pero ésta no se inicia.
                    if (isPlaying && !isPaused) {
                        mediaPlayer!!.start()
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
            Log.d("Audio", "En el onCreate, sin datos a recuperar")

            // Estado inicial: nada habilitado salvo el botón de play
            // Hasta que no se empiece a reproducir el audio, el usuario no podrá tocar la seekbar
            binding.seekBar.isEnabled = false

            binding.botonPlayAudio.isEnabled = true
            binding.botonStopAudio.isEnabled = false
            binding.botonPauseAudio.isEnabled = false
            binding.botonRetrAudio.isEnabled = false
            binding.botonAvanAudio.isEnabled = false

        } else {
            Log.d("Audio", "En el onCreate con datos a recuperar")

            // Si es un onCreate distinto del primero y hay una reproducción a
            // medias (se le ha dado al Play)
            if (isPlaying) {
                binding.seekBar.isEnabled = true

                binding.botonPlayAudio.isEnabled = false
                binding.botonStopAudio.isEnabled = true
                binding.botonPauseAudio.isEnabled = true

                binding.botonRetrAudio.isEnabled = true
                binding.botonAvanAudio.isEnabled = true

            // Si es un onCreate distinto del primero y no hay una reproducción
            // a medias (nunca se pulsó Play o el último botón tocado fue Stop)
            } else {
                binding.seekBar.isEnabled = false

                binding.botonPlayAudio.isEnabled = true
                binding.botonStopAudio.isEnabled = false
                binding.botonPauseAudio.isEnabled = false

                binding.botonRetrAudio.isEnabled = false
                binding.botonAvanAudio.isEnabled = false
            }
        }

        // Control de la reproducción de sonidos:
        controlSonido()
    }

    /**
     * Función que adelanta la reproducción, sumando tiempo
     * a la currentPosition del mediaPlayer.
     * El coerceIn se usa para vigilar que la posición final
     * esté dentro de un rango válido del tiempo de ejecución.
     *
     * @param millis: Número entero que equivale al momento actual de la reproducción
     */
    private fun avanzarReproduccion(millis: Int) {
        if (mediaPlayer != null) {
            val newPosition = mediaPlayer!!.currentPosition + millis
            mediaPlayer!!.seekTo(newPosition.coerceIn(0, mediaPlayer!!.duration))
        }
    }

    /**
     * Función que atrasa la reproducción, restando tiempo
     * a la currentPosition del mediaPlayer.
     * El coerceIn se usa para vigilar que la posición final
     * esté dentro de un rango válido del tiempo de ejecución.
     *
     * @param millis: Número entero que equivale al momento actual de la reproducción
     */
    private fun retrocederReproduccion(millis: Int) {
        if (mediaPlayer != null) {
            val newPosition = mediaPlayer!!.currentPosition - millis
            mediaPlayer!!.seekTo(newPosition.coerceIn(0, mediaPlayer!!.duration))
        }
    }

    /**
     * Método dedicado al control de la reproducción del audio, implementando
     * listeners para los botones de play, pause, stop, retroceso y avance.
     *
     * @param nombre String que contiene el nombre del audio a reproducir
     */
    private fun controlSonido() {
        // Me creo listeners para cada botón
        binding.botonPlayAudio.setOnClickListener {
            pararReproductor()

            iniciarReproduccion()
        }

        binding.botonStopAudio.setOnClickListener {
            // Detiene el reproductor en su totalidad, igualándolo a null.
            pararReproductor()
        }

        binding.botonPauseAudio.setOnClickListener {
            if (mediaPlayer != null) {
                // Si estaba reproduciendo, pausa. Si no, reproduce.
                if (mediaPlayer!!.isPlaying) {
                    isPaused = true
                    mediaPlayer!!.pause()
                } else {
                    isPaused = false
                    mediaPlayer!!.start()
                }
            }
        }


        // Voy a controlar también que el usuario toque la barra para que la canción avance
        // hasta el punto requerido
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Se pasa el progreso requerido en la barra y el mediaPlayer
                    // se sitúa en donde corresponda
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        // Listener para el botón de retroceso
        binding.botonRetrAudio.setOnClickListener{
            retrocederReproduccion(3000)
        }

        // Listener para el botón de avance
        binding.botonAvanAudio.setOnClickListener{
            avanzarReproduccion(3000)
        }
    }


    /**
     * Método que verifica si el reproductor de audio con el que se trabaja
     * es nulo, lo crea en caso afirmativo y, en la propia creación, carga
     * desde el almacenamiento de Firebase el audio que va asociado a la
     * reseña en la que se ha pulsado en la RecyclerView del perfil de usuario.
     */
    private fun iniciarReproduccion() {
        if (mediaPlayer == null) {

            // El reproductor de audio se creará usando como URI aquella que se
            // haya almacenado en la Firestore, asociada al objeto reseña en
            // el que se haya presionado
            coleccionResenas.document(identificador).get().addOnSuccessListener {
                uriFire2 = (Uri.parse(it.get("uri").toString())).toString()
                Log.d("Audio","Vídeo cargado con uri DOS $uriFire2")
                mediaPlayer = MediaPlayer.create(this, Uri.parse(it.get("uri").toString()))
                mediaPlayer!!.setOnCompletionListener(this)


                isPlaying = true
                inicializarSeekBar()

                // Tras el proceso de creación del mediaPlayer, la reproducción se inicia.
                mediaPlayer!!.start()

                // Y aquí jugamos con habilitar o deshabilitar botones para evitar errores.
                // Si hay reproducción activa, deshabilito el botón play. A los otros sí
                // se les puede dar. También se habilita la seekBar.
                binding.seekBar.isEnabled = true

                binding.botonPlayAudio.isEnabled = false
                binding.botonStopAudio.isEnabled = true
                binding.botonPauseAudio.isEnabled = true

                binding.botonRetrAudio.isEnabled = true
                binding.botonAvanAudio.isEnabled = true
            }
        }
    }


    /**
     * Método que inicializa la seekBar empleada para indicar el progreso
     * de la reproducción un audio, mediante el uso de un hilo.
     */
    private fun inicializarSeekBar() {
        // Antes de nada le pongo tamaño a la seekBar
        binding.seekBar.max = mediaPlayer!!.duration

        val handler = Handler(Looper.getMainLooper())

        // Le paso lo que quiero que ejecute en ese hilo
        // Dos parámetros: lo que quiero que ejecute en segundo plano Y cuándo quiero que
        // empiece a ejecutarse este hilo
        handler.postDelayed(
            object: Runnable {
                override fun run() {
                    try {
                        // La seekBar va a ir avanzando cada segundo
                        binding.seekBar.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(this, 1000) // Reejecutar el run cada mil milisegundos
                    } catch (e: Exception) {

                    }
                }
            }
            , 0)
    }


    /**
     * Método empleado para detener el mediaPlayer cuando se presiona el
     * botón de Stop o cuando la reproducción termina. Se liberan recursos
     * y el mediaPlayer se iguala a null.
     */
    private fun pararReproductor() {
        // Comprobamos que mediaPlayer está funcionando
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            pos = 0
            binding.seekBar.progress = 0

            mediaPlayer!!.release() // Para liberar recursos
            mediaPlayer = null

            // Y rehago el juego de habilitaciones de seekBar y botones
            binding.seekBar.isEnabled = false

            binding.botonPlayAudio.isEnabled = true
            binding.botonStopAudio.isEnabled = false
            binding.botonPauseAudio.isEnabled = false

            binding.botonRetrAudio.isEnabled = false
            binding.botonAvanAudio.isEnabled = false

            isPlaying = false
            isPaused = false
        }
    }


    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding= ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        // Este método se ejecuta cuando termina de reproducirse el audio.
        // En el método pararReproductor() vamos a hacer todo lo relativo a la liberación de recursos.
        pararReproductor()
    }


    override fun onPause() {
        super.onPause()
        Log.d("Audio", "En el onPause")

        if (mediaPlayer != null) {
            // Cuando estamos en el onPause, si había una reproducción en proceso
            // (mediaPlayer creado), el valor de la misma se asigna a la variable pos
            // La reproducción se pausa
            pos = mediaPlayer!!.currentPosition
            Log.d("Audio", "Valor de pos = $pos");
            mediaPlayer!!.pause()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        Log.d("Audio", "En el onSaveInstanceState")

        if (mediaPlayer != null) {
            bundle.putInt("posicion", pos)
            Log.d("Audio", "Valor de pos en el bundle = $pos");

            // En el onSaveInstanceState, creamos un "paquete" que más tarde se recuperará.
            // En él meto la posición por la que se hallaba la reproducción.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Audio", "En el onDestroy")

        // En el onDestroy, el mediaPlayer se iguala a null para liberar recursos
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        // Antes del onRestoreInstanceState, se pasa por el onCreate.
        // En éste, siempre se creará un mediaPlayer nuevo.

        super.onRestoreInstanceState(bundle!!)
        Log.d("Audio", "En el onRestoreInstanceState")

        if (bundle != null) {
            // Del "paquete" recuperamos el punto por el que se hallaba la reproducción.
            pos = bundle.getInt("posicion")
            Log.d("Audio", "Valor de pos tras revisar el bundle = $pos");
        } else {
            Log.d("Audio", "Valor de pos si no hay bundle = $pos");
        }
    }

    override fun onResume(){
        super.onResume()
        Log.d("Audio", "En el onResume");
        Log.d("Audio", "Valor de pos en el onResume = $pos");
        // El onResume se ejecuta tras el onCreate.
    }

    /**
     * Función que finaliza la activity actual, devolviendo
     * al usuario a aquella activity desde la que hubiera
     * accedido.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun volver(view: View) {
        pararReproductor()
        isPlaying = false
        finish()
    }
}