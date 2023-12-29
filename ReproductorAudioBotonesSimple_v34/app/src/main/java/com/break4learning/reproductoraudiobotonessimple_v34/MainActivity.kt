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

    companion object {
        var isPlaying = false
        var isPaused = false
    }



    // Preparamos el mediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    // A la mutable list le vamos a pasar los sonidos que queremos reproducir
    // (En la rúbrica no hay meter lista de reproducción, por lo que la funcionalidad
    // de pasar de canción se ha ignorado)
    private var sonidoActual = mutableListOf(R.raw.bailar, R.raw.melanie, R.raw.jester)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MultimediaLog", "En el onCreate")
        Log.d("MultimediaLog", "Valor de pos = $pos");
        crearObjetosDelXml()


        if (mediaPlayer == null) {
            // Tengo que hacer un proceso de creación cuando el mediaPlayer sea nulo.
            // Si lo hago sin que sea nulo, me va a dar una excepción.
            mediaPlayer = MediaPlayer.create(this, sonidoActual[cancionActual])
            mediaPlayer!!.setOnCompletionListener(this)

            // También inicializamos la seekBar; lo vamos a hacer con un hilo
            inicializarSeekBar()
        }

        // Siempre que giramos el dispositivo, pasamos por un proceso onPause > onSaveInstanceState >
        // onDestroy > onCreate > onRestoreInstancestate > onResume.

        // Si el savedInstanceState vale null, significa que es el primer onCreate que se hace,
        // el que tiene lugar al abrir l activity por primera vez, y que no se han hecho giros de
        // dispositivo con anterioridad.
        if (savedInstanceState == null) {
            Log.d("MultimediaLog", "No hay datos que recuperar")

            // Estado inicial: nada habilitado salvo el botón de play
            // Hasta que no se empiece a reproducir el audio, el usuario no podrá tocar la seekbar
            binding.seekBar.isEnabled = false

            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false

            binding.retrButton.isEnabled = false
            binding.avanButton.isEnabled = false

            // Reproducción de sonidos:
            controlSonido(sonidoActual[cancionActual])
        } else {
            Log.d("MultimediaLog", "En el onCreate con datos a recuperar")

            // Si es un onCreate distinto del primero y hay una reproducción a
            // medias (se le ha dado al play)
            if (isPlaying) {
                binding.seekBar.isEnabled = true

                binding.playButton.isEnabled = false
                binding.stopButton.isEnabled = true
                binding.pauseButton.isEnabled = true

                binding.retrButton.isEnabled = true
                binding.avanButton.isEnabled = true

            // Si es un onCreate distinto del primero y no hay una reproducción
            // a medias (el último botón tocado fue Stop)
            } else {
                binding.seekBar.isEnabled = false

                binding.playButton.isEnabled = true
                binding.stopButton.isEnabled = false
                binding.pauseButton.isEnabled = false

                binding.retrButton.isEnabled = false
                binding.avanButton.isEnabled = false
            }
        }
    }

    /**
     * Función que adelanta la reproducción un total de 3 segundos, sumando tiempo
     * a la currentPosition del mediaPlayer.
     * El coerceIn se usa para vigilar que la posición final esté dentro de un rango
     * válido del tiempo de ejecución.
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
     * Función que atrasa la reproducción un total de 3 segundos, restando tiempo
     * a la currentPosition del mediaPlayer.
     * El coerceIn se usa para vigilar que la posición final esté dentro de un rango
     * válido del tiempo de ejecución.
     *
     * @param millis: Número entero que equivale al momento actual de la reproducción
     */
    private fun retrocederReproduccion(millis: Int) {
        if (mediaPlayer != null) {
            val newPosition = mediaPlayer!!.currentPosition - millis
            mediaPlayer!!.seekTo(newPosition.coerceIn(0, mediaPlayer!!.duration))
        }
    }

    private fun controlSonido(id: Int) {
        // Me creo listeners para cada botón
        binding.playButton.setOnClickListener {
            isPlaying = true
            iniciarReproduccion(id)
        }


        binding.pauseButton.setOnClickListener {
            if (mediaPlayer != null) {
                // Si estaba ejecutando, pausa. Si no, reproduce.
                if (mediaPlayer!!.isPlaying) {
                    isPaused = true
                    mediaPlayer!!.pause()
                } else {
                    isPaused = false
                    mediaPlayer!!.start()
                }
            }
        }


        binding.stopButton.setOnClickListener {
            // Detiene el reproductor, no el audio.
            // Funcionará con el onCompletion, cuando la reproducción se acabe.
            isPlaying = false
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

        // Listener para el botón de retroceso
        binding.retrButton.setOnClickListener{
            retrocederReproduccion(3000)
        }

        // Listener para el botón de avance
        binding.avanButton.setOnClickListener{
            avanzarReproduccion(3000)
        }
    }

    private fun iniciarReproduccion(id: Int) {
        if (mediaPlayer == null) {
            // Tengo que hacer un proceso de creación cuando el mediaPlayer sea nulo.
            // Si lo hago sin que sea nulo, me va a dar una excepción.

            // Al tocar Play, el mediaPlayer se crea de cero, partiéndose de que
            // anteriormente no existía uno.
            mediaPlayer = MediaPlayer.create(this, id)
            mediaPlayer!!.setOnCompletionListener(this)

            // También inicializamos la seekBar; lo vamos a hacer con un hilo
            inicializarSeekBar()
        }

        // Tras darle a Play, la reproducción se inicia.
        mediaPlayer!!.start()

        // Y aquí jugamos con habilitar o deshabilitar botones para evitar errores.
        // Si hay reproducción activa, deshabilito el botón play. A los otros dos sí
        // les puedo dar.
        binding.playButton.isEnabled = false
        binding.stopButton.isEnabled = true
        binding.pauseButton.isEnabled = true
        // Y habilitamos la seekbar
        binding.seekBar.isEnabled = true
        // Y los botones de avance y retroceso
        binding.retrButton.isEnabled = true
        binding.avanButton.isEnabled = true
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
            binding.seekBar.progress = 0

            // Si da problemas al detenerlo: mediaPlayer.reset()
            mediaPlayer!!.release()
            // Para liberar recursos, el mediaPlayer se elimina siempre que le demos al Stop
            mediaPlayer = null

            // Y rehago el juego de botones habilitados
            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false
            // Deshabilitamos la seekBar
            binding.seekBar.isEnabled = false
            // Y reajustamos los botones de avance y retroceso
            binding.retrButton.isEnabled = false
            binding.avanButton.isEnabled = false
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
    // atrás en los archivos. (No se pide en la rúbrica, innecesario)
    override fun onPause() {
        super.onPause()
        Log.d("MultimediaLog", "En el onPause")

        if (mediaPlayer != null) {
            // Cuando estamos en el onPause, si había una reproducción en proceso, el valor
            // de la misma se asigna a la variable pos
            // La reproducción se pausa
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
            bundle.putInt("cancion_actual", cancionActual)

            // En el onSaveInstanceState, creamos un "paquete" que más tarde se recuperará.
            // En él meto la posición por la que se hallaba la reproducción y la canción que
            // había reproduciéndose (no se usa).
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // En el onDestroy, el mediaPlayer se iguala a null para liberar recursos
        Log.d("MultimediaLog", "En el onDestroy")
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        // Antes del onRestoreInstanceState, se pasa por el onCreate.
        // En éste, siempre se creará un mediaPlayer nuevo.

        super.onRestoreInstanceState(bundle!!)
        Log.d("MultimediaLog", "En el onRestoreInstanceState")

        if (bundle != null) {
            // Del "paquete" recuperamos el punto por el que se hallaba la reproducción
            // y la canción que había reproduciéndose (no se usa).

            pos = bundle.getInt("posicion")
            cancionActual = bundle.getInt("cancion_actual")
            Log.d("MultimediaLog", "Valor de pos = $pos");
        }
        Log.d("MultimediaLog", "Valor de pos = $pos");
    }

    override fun onResume(){
        super.onResume()

        // El onResume se ejecuta tras el onCreate.

        Log.d("MultimediaLog", "En el onResume");
        Log.d("MultimediaLog", "Valor de pos = $pos");
        Log.d("MultimediaLog", "Valor de canción = $cancionActual");

        if (mediaPlayer != null) {
            Log.d("MultimediaLog", "ANTES DEL SEEK, valor de pos = $pos");

            // Usamos la variable que habíamos empleado para guardar la posición de la
            // reproducción para llevar la reproducción directamente hasta ese punto.
            mediaPlayer!!.seekTo(pos)

            // Si había una reproducción en proceso y no se había pausado el reproductor,
            // la reproducción se inicia. Si no, se busca la posición por la que iba la
            // reproducción, pero ésta no se inicia.
            if (isPlaying && !isPaused) {
                mediaPlayer!!.start()
            }

            // Y metemos el control de sonidos vía botones.
            controlSonido(sonidoActual[cancionActual])
        }
    }
}