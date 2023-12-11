package com.break4learning.reproductoraudiobotonessimple_v34

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import com.break4learning.reproductoraudiobotonessimple_v34.databinding.ActivityMainBinding


// Primero extendemos de MediaPlayer; cuando nos pregunte si queremos implementar
// los métodos, le decimos que sí
class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    /**
     * Binding
     */
    private lateinit var binding: ActivityMainBinding
    var pos = 0
    var cancionActual = 0

    // Preparamos el mediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    // A la mutable list le vamos a pasar los sonidos que queremos reproducir
    private var sonidoActual = mutableListOf(R.raw.bailar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MultimediaLog", "En el onCreate")
        if (savedInstanceState == null) {
            Log.d("MultimediaLog", "No hay datos que recuperar")
        }
        Log.d("MultimediaLog", "Valor de pos = $pos");

        crearObjetosDelXml()

        // Hasta que no se empiece a reproducir el audio, el usuario no podrá tocar la seekbar
        binding.seekBar.isEnabled = false


        // Reproducción de sonidos:
        // luego habrá que tocarlo para decirle que pase al sigueinte audio
        if (mediaPlayer == null) {
            controlSonido(sonidoActual[cancionActual])
        }
    }

    private fun controlSonido(id: Int) {
        // Me creo listeners para cada botón
        binding.playButton.setOnClickListener {
            iniciarReproduccion(id)
        }


        binding.pauseButton.setOnClickListener {
            if (mediaPlayer != null) {
                // Si estaba ejecutando, pausa. Si no, reproduce.
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                } else {
                    mediaPlayer!!.start()
                }
            }
        }


        binding.stopButton.setOnClickListener {
            // Detiene el reproductor, no el audio.
            // Funcionará con el onCompletion, cuando la reproducción se acabe.
            pararReproductor()
        }


        // Voy a controlar también que el usuario toque la barra para que la canción avance
        // hasta el punto requerido
        // Darle a que sí importe los tres métodos
        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Se pasa el progreso requerido en la barra y el mediaPlayer
                    // se sitúa en donde corresponda; ESO SIEMPRE Y CUANDO HAGA QUE
                    // EL TAMAÑO DE LA SEEKBAR COINCIDA CON LA DURACIÓN DEL AUDIO
                    mediaPlayer!!.seekTo(progress)
                }
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }


            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    private fun iniciarReproduccion(id: Int) {
        if (mediaPlayer == null) {
            // Tengo que hacer un proceso de creación cuando el mediaPlayer sea nulo.
            // Si lo hago sin que sea nulo, me va a dar una excepción.
            mediaPlayer = MediaPlayer.create(this, id)
            mediaPlayer!!.setOnCompletionListener(this)

            // También inicializamos la seekBar; lo vamos a hacer con un hilo
            inicializarSeekBar()

            mediaPlayer!!.start()

            // Y aquí jugamos con habilitar o deshabilitar botones para evitar errores.
            // Si hay reproducción activa, deshabilito el botón play. A los otros dos sí
            // les puedo dar.
            binding.playButton.isEnabled = false
            binding.stopButton.isEnabled = true
            binding.pauseButton.isEnabled = true
            // Y habilitamos la seekbar
            binding.seekBar.isEnabled = true
        }
    }

    private fun inicializarSeekBar() {
        // Usamos un handler: lo usamos para enviar mensajes y objetos a una cola que puede tener
        // un thread; un hilo de ejecución ejecuta una tarea y se destruye, pero aquí no: le paso una
        // cola de tareas y al acabar una pasa a la siguiente

        // Antes de nada le pongo tamaño a la seekBar
        binding.seekBar.max = mediaPlayer!!.duration

        // Coger el import de android
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

    private fun pararReproductor() {
        // Comprobamos que mediaPlayer está funcionando
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            // Si da problemas al detenerlo: mediaPlayer.reset()
            mediaPlayer!!.release() // Para liberar recursos
            mediaPlayer = null

            // Y rehago el juego de botones habilitados
            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false
            // Deshabilitamos la seekBar
            binding.seekBar.isEnabled = false
        }
    }

    /**
     * Crear objetos del xml
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        // Este método se ejecuta cuando termina de reproducirse el audio.
        // En el método pararReproductor() vamos a hacer todo lo relativo a la liberación de recursos.
        pararReproductor()
    }


    // TAREA: si lo giro, se pierde todo el control sobre el audio. Solucionarlo.
    // Y: pasarle una lista de audios, meterle dos botones y que vaya yendo para adelante y para
    // atrás en los archivos.
    override fun onPause() {
        super.onPause()
        Log.d("MultimediaLog", "En el onPause")

        if (mediaPlayer != null) {
            pos = mediaPlayer!!.currentPosition
            Log.d("MultimediaLog", "Valor de pos = $pos");
            mediaPlayer!!.pause()
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        Log.d("MultimediaLog", "En el onSaveInstanceState")
        if (mediaPlayer != null) {
            bundle.putInt("posicion", pos)
            Log.d("MultimediaLog", "Valor de pos = $pos");
            bundle.putInt("canción actual", cancionActual)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("MultimediaLog", "En el onDestroy")
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle!!)
        Log.d("MultimediaLog", "En el onRestoreInstanceState")

        if (bundle != null && mediaPlayer != null) {
            pos = bundle.getInt("posicion")
            cancionActual = bundle.getInt("canción actual")
        }
        Log.d("MultimediaLog", "Valor de pos = $pos");
    }

    override fun onResume(){
        super.onResume()
        Log.d("MultimediaLog", "En el onResume");
        Log.d("MultimediaLog", "Valor de pos = $pos");
        Log.d("MultimediaLog", "Valor de canción = $cancionActual");

        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(pos)
            mediaPlayer!!.start()
        }
    }
}