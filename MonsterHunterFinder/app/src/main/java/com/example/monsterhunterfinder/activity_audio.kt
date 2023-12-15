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

/**
 * Activity asociada a la reproducción de los distintos audios
 * que van asociados a las reseñas que se dejan en el perfil de
 * un usuario.
 * @author Jaime
 */
class activity_audio : AppCompatActivity(), MediaPlayer.OnCompletionListener {

    private lateinit var binding: ActivityAudioBinding

    // Preparamos una variable String vacía que se igualará
    // a la String contenida en el putExtra para así obtener
    // el nombre del archivo de audio a reproducir
    private lateinit var nombreAudio: String

    // Posición por la que se halla reproduciendo
    var pos = 0

    // Variable usada para verificar si había una reproducción en curso
    companion object {
        var isPlaying = false
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
        nombreAudio = objetoIntent.getStringExtra("Identificador").toString()
        nombreAudio = nombreAudio.substring(0, nombreAudio.length - 4);
        Log.d("AAAAA", nombreAudio)


        if (mediaPlayer == null) {
            // Tengo que hacer un proceso de creación cuando el mediaPlayer sea nulo.
            // Creamos el objeto como instancia de la clase importada
            mediaPlayer = MediaPlayer()

            // Para cargar en el mediaPlayer un archivo de audio:
            try {
                val resourceId = resources.getIdentifier(nombreAudio, "raw", packageName)
                mediaPlayer!!.setDataSource(this, Uri.parse("android.resource://$packageName/$resourceId"))
                // Y le decimos que se prepare para reproducirlo
                mediaPlayer!!.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("AudioError", "Error al preparar el MediaPlayer: ${e.message}")
            }


            mediaPlayer!!.setOnCompletionListener(this)

            // También inicializamos la seekBar; lo vamos a hacer con un hilo
            inicializarSeekBar()
        }


        if (savedInstanceState == null) {
            Log.d("MultimediaLog", "No hay datos que recuperar")

            // Hasta que no se empiece a reproducir el audio, el usuario no podrá tocar la seekbar
            binding.seekBar.isEnabled = false

            binding.botonPlay.isEnabled = true
            binding.botonStop.isEnabled = false
            binding.botonPause.isEnabled = false

            // Reproducción de sonidos:
            // luego habrá que tocarlo para decirle que pase al sigueinte audio
            controlSonido(nombreAudio)
        } else {
            Log.d("MultimediaLog", "En el onCreate con datos a recuperar")

            // Hasta que no se empiece a reproducir el audio, el usuario no podrá tocar la seekbar
            if (isPlaying) {
                binding.seekBar.isEnabled = true
                binding.botonPlay.isEnabled = false
                binding.botonStop.isEnabled = true
                binding.botonPause.isEnabled = true
            }
        }
    }

    /**
     * Método dedicado al control de la reproducción del audio, implementando
     * listeners para los botones de play, pause y stop.
     *
     * @param nombre String que contiene el nombre del audio a reproducir
     */
    private fun controlSonido(nombre: String) {
        // Me creo listeners para cada botón
        binding.botonPlay.setOnClickListener {
            isPlaying = true
            iniciarReproduccion(nombre)
        }

        binding.botonPause.setOnClickListener {
            if (mediaPlayer != null) {
                // Si estaba ejecutando, pausa. Si no, reproduce.
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                } else {
                    mediaPlayer!!.start()
                }
            }
        }

        binding.botonStop.setOnClickListener {
            // Detiene el reproductor, no el audio.
            // Funcionará con el onCompletion, cuando la reproducción se acabe.
            isPlaying = false
            pararReproductor()
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
    }


    /**
     * Método que inicia la reproducción de audio, discriminando entre que
     * no exista un mediaPlayer previamente creado (cuando se haya usado el
     * botón de Stop) y que sí exista.
     *
     * @param nombre String que contiene el nombre del audio a reproducir
     */
    private fun iniciarReproduccion(nombre: String) {
        if (mediaPlayer == null) {

            mediaPlayer = MediaPlayer()

            try {
                val resourceId = resources.getIdentifier(nombre, "raw", packageName)
                mediaPlayer!!.setDataSource(this, Uri.parse("android.resource://$packageName/$resourceId"))
                mediaPlayer!!.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("AudioError", "Error al preparar el MediaPlayer: ${e.message}")
            }

            mediaPlayer!!.setOnCompletionListener(this)

            inicializarSeekBar()
        }
        mediaPlayer!!.start()

        // Y aquí jugamos con habilitar o deshabilitar botones para evitar errores.
        // Si hay reproducción activa, deshabilito el botón play. A los otros dos sí
        // les puedo dar.
        binding.botonPlay.isEnabled = false
        binding.botonStop.isEnabled = true
        binding.botonPause.isEnabled = true
        // Y habilitamos la seekbar
        binding.seekBar.isEnabled = true
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
            binding.seekBar.progress = 0

            mediaPlayer!!.release() // Para liberar recursos
            mediaPlayer = null

            // Y rehago el juego de botones habilitados
            binding.botonPlay.isEnabled = true
            binding.botonStop.isEnabled = false
            binding.botonPause.isEnabled = false
            // Deshabilitamos la seekBar
            binding.seekBar.isEnabled = false
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
        Log.d("MultimediaLog", "En el onPause")

        if (mediaPlayer != null) {
            // Al entrar en el onPause, se guarda la posición actual de
            // la reproducción del mediaPlayer
            pos = mediaPlayer!!.currentPosition
            mediaPlayer!!.pause()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        Log.d("MultimediaLog", "En el onSaveInstanceState")

        // Si el mediaPlayer no era null, se guarda su estado de
        // avance en un bundle
        if (mediaPlayer != null) {
            bundle.putInt("posicion", pos)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Al llegar al onDestroy, el mediaPlayer existente se iguala
        // a null y se liberan recursos
        Log.d("MultimediaLog", "En el onDestroy")
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle!!)
        Log.d("MultimediaLog", "En el onRestoreInstanceState")

        if (bundle != null) {
            // La posición por la que iba reproduciendo el mediaPlayer se
            // recupera cuando se llega a onRestoreInstanceState
            pos = bundle.getInt("posicion")
        }
    }

    override fun onResume(){
        super.onResume()
        Log.d("MultimediaLog", "En el onResume");

        // Si el mediaPlayer no era null, se busca la posición en la que se encontraba
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(pos)

            // Si estaba tocando (como al girar el móvil mientras se reproduce
            // un audio), inicia para reanudar
            if (isPlaying) {
                mediaPlayer!!.start()
            }

            controlSonido(nombreAudio)
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
        pararReproductor()
        isPlaying = false
        finish()
    }
}