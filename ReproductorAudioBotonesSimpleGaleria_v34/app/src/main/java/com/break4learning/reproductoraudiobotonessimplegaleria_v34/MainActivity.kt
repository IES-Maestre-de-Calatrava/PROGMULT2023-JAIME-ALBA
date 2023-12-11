package com.break4learning.reproductoraudiobotonessimplegaleria_v34

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.break4learning.reproductoraudiobotonessimplegaleria_v34.databinding.ActivityMainBinding

/**
 * Clase creada para demostrar la ejecución de un audio SELECCIONADO DE LA GALERÍA controlado por botones
 * Realiza la actualización de una barra de progreso en un hilo
 *
 * Video: https://www.youtube.com/watch?v=a3yLc9J0hGE
 * Imagen obtenida de: https://commons.wikimedia.org/wiki/File:Audio-itunes.svg
 * con licencia: https://creativecommons.org/licenses/by-sa/3.0/deed.en
 *
 * Ayuda para reproducir audios en servicios:
 * https://www.desarrollolibre.net/blog/android/mediaplayer-para-reproducir-audios-en-android
 *
 * Ejercicio: Este código presenta un problema que deberás solucionar. Al girar el dispositivos,
 * el reproductor pierde el control sobre el audio. Los botones no responden a la lógica de la aplicación.
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    /**
     * Binding
     */
    // Para cambiar iconitos: click derecho en drawable > new > image asset
    private lateinit var binding: ActivityMainBinding


    private var mediaPlayer: MediaPlayer? = null            // Clase para reproducir el audio


    // El setting se nos da hecho; hay que cambiar cosas para poder seleccionar
    // cosas de la galería
    // En primer lugar, puesto que la carga es llamando a otra app (la galería) y
    // la galería me va a devolver algo, hay que cambiar la mutableList

    //private var sonidoActual = mutableListOf(R.raw.bailar)
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearObjetosDelXml()

        binding.seekBar.isEnabled = false                   // Deshabilito la barra de reproducción hasta cargar el audio

        //controlSonido(sonidoActual[0])

        // Preparo algo para recoger el activityResultLauncher de antes
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.data != null) {
                val data: Intent = result.data!!
                controlSonido(data.data)
            }
        }

        // Más: añadimos un click listener para el botón nuevo, que llamará a un método que
        // permitirá cargar sonidos de la galería
        binding.loadButton.setOnClickListener {
            SelectSoundFromGallery()
        }

    }

    /**
     * Prepara los eventos para todos los botones de la interfaz
     *
     * @param id Identificador del audio a reproducir
     */
    // Ahora lo que control sonido recibe no es un entero, sino una uri, la uri que es el enlace
    // al elemento de la galería
     private fun controlSonido(id: Uri?){

        binding.playButton.isEnabled = true

        binding.playButton.setOnClickListener{      // Acción al pulsar el botón del play
            iniciarReproduccion(id)
        }

        binding.pauseButton.setOnClickListener{     // Acción al pulsar el botón de pausa
            if (mediaPlayer != null){                      // Si está cargado el mediaPlayer
                if (mediaPlayer!!.isPlaying()){            // Si está ejecutándose lo pausamos
                    mediaPlayer!!.pause()
                } else {                                   // Si no está ejecutándose es porque está en pausa, lo volvemos a iniciar
                    mediaPlayer!!.start()
                }
            }
        }

        binding.stopButton.setOnClickListener{      // Acción al pulsar el botón de Stop
            pararReproductor()
        }


        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{        // Controles de la seekBar
            // Control del avance por parte del usuario
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {                             // Si el cambio ha sido por pulsación del usuario
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    // Método para cargar sonidos de la galería:
    fun SelectSoundFromGallery() {
        // ActionPick (selecciona archivos con explorador local, sólo aparecen archivos de audio,
        // o ActionGetContent (muestra todos los archivos pero sólo deja seleccionar los de audio),
        // cosas que añadir al activityResultLauncher para poder seleccionar archivos de audio
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT

        // Ahora hay que indicarle el tipo de fichero que me permita seleccionar
        intent.type = "audio/*" // Cualquier tipo de audio

        activityResultLauncher.launch(intent)
    }


    /**
     * Iniciar reproduccion
     *
     * @param id Identificador del audio a reproducir
     */
    // Ya no es un int, ahora es una uri
    private fun iniciarReproduccion(id: Uri?){

        if (mediaPlayer==null){                                 // Si MediaPlayer no está inicializado
            mediaPlayer = MediaPlayer.create(this, id)   // Creamos el MediaPlayer para el id del audio a reproducir
            mediaPlayer!!.setOnCompletionListener(this)         // Le ponemos el listener para cuando finalice la reproducción
            inicializarSeekBar()                                // Inicializamos la barra de progreso
            mediaPlayer!!.start()                               // Comenzamos la reproducción
            binding.playButton.isEnabled = false                // Habilitamos y deshabilitamos los botones
            binding.stopButton.isEnabled = true
            binding.pauseButton.isEnabled = true
            binding.seekBar.isEnabled = true
        }
    }

    /**
     * Parar reproductor
     * Para el reproductor y libera los recursos
     */
    private fun pararReproductor(){
        if (mediaPlayer !=null){
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null

            binding.seekBar.isEnabled = false
            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false
        }
    }

    /**
     * Inicializar seek bar
     * Ejecuta en un hilo la actualización de la barra de progreso
     */
    private fun inicializarSeekBar(){
        binding.seekBar.max = mediaPlayer!!.duration    // Establecemos la duración máxima de la seekbar

        //Handler se utiliza para enviar mensajes y objetos a una cola
        //de mensajes de un thread del Looper

        //Un Looper:
        // Looper es una clase utilizada para ejecutar los Messages (Runnables) de
        // una cola.
        // Un thread normal no tiene una cola. Se ejecuta una vez y cuando el método
        // termina, el thread no vuelve a ejecutar otro elemento Message(Runnable)

        //Explicación de Looper:
        // https://stackoverflow.com/questions/7597742/what-is-the-purpose-of-looper-and-how-to-use-it
        // http://prasanta-paul.blogspot.com/2013/09/android-looper-and-toast-from.html

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(
            object: Runnable { //Parámetro 1: Lo que queremos ejecutar en segundo plano
                override fun run() {
                    try {
                        binding.seekBar.progress = mediaPlayer!!.currentPosition             // Se sitúa la seekbar en la posición del mediaPlayer
                        handler.postDelayed(this, 1000)                          // Volvemos a ejecutar pasado 1 segundo
                    } catch (e: Exception){
                        binding.seekBar.progress = 0
                    }
                }
            }
            , 0) //Parámetro 2: El tiempo de retardo para que se ejecute parámetro 1
    }

    /**
     * Al finalizar la reproducción
     *
     * @param p0 Llama para detener el reproductor y liberar los recursos
     */
    override fun onCompletion(p0: MediaPlayer?) {
        pararReproductor()
    }

    /**
     * Crear objetos del xml
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}