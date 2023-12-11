package com.break4learning.estadosmediaplayer_v34

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.break4learning.estadosmediaplayer_v34.databinding.ActivityMainBinding
import java.io.IOException

// Los métodos aquí mostrados pueden usarse para guardar información en general

class MainActivity : AppCompatActivity() {
    // Programa que mediante un MediaPlayer guarda los distintos estados
    // por los que pasa nuestra aplicación.
    // También guarda el estado en el que estaba un audio para reproducir por
    // el mismo sitio por el que se había quedado.
    // Los mensajes a transmitir se van a pasar por el logcat; vamos a poder
    // ver los distintos estados.
    /**
     * Binding
     */
    private lateinit var binding: ActivityMainBinding

    // Variables MediaPlayer y para guardar la posición por la que va el audio
    var mediaPlayer: MediaPlayer? = null
    var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Mensaje para indicar que estamos en el onCreate
        Log.d("MultimediaLog", "En el onCreate")

        // Creamos el objeto como instancia de la clase importada
        mediaPlayer = MediaPlayer()

        // Para cargar en el mediaPlayer un archivo de audio:
        try {
            mediaPlayer!!.setDataSource(
                this,
                // Contexto y uri parse de la ruta del archivo de audio
                Uri.parse("android.resource://"+packageName+"/"+R.raw.bailar)
            )
            // Y le decimos que se prepare para reproducirlo
            mediaPlayer!!.prepare() // Este prepare suele dejar bloqueado el hilo principal mientras
            // carga el audio; con audios en local no pasa nada, pero con audios que tenga que descargar
            // tengo que usar un prepare asíncrono para que reproduzca cuando termine de descargar

            // Si uso el MediaPlayer.create(), no puedo crear asíncronos

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // El método que se ejecuta justo tras el onCreate es el onResume; en él le vamos a decir
    // que reproduzca el audio
    override fun onResume(){
        super.onResume()

        // Le vamos a decir que nos diga que se encuentra en el onResume y por qué posición
        // del audio va
        // Antes de darle al start tenemos que asegurarnos de que el mediaPlayer no sea null,
        // porque si lo es produce una excepción
        Log.d("MultimediaLog", "En el onResume");
        Log.d("MultimediaLog", "Valor de pos = $pos");

        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo(pos)
            mediaPlayer!!.start()
        }
    }

    // Para que libere recursos cuando se termine: método onDestroy, método que se ejecuta
    // cuando se destruye una activity
    override fun onDestroy() {
        super.onDestroy()

        // Al girarlo, se destruye, se vuelve a crear y el audio
        // se reproduce desde el principio

        // Si la pongo en segundo plano y luego vuelvo, lo que se
        // ejecuta es el onResume
        Log.d("MultimediaLog", "En el onDestroy")
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }


    // Cuando se detiene la app, se ejecuta el método onPause
    // Se ejecuta tanto al girar el móvil como al poner la app en segundo plano
    override fun onPause() {
        super.onPause()
        Log.d("MultimediaLog", "En el onPause")

        // Cosas que podríamos hacer: que, cuando la app pase a segundo plano,
        // el audio se pause


        // Primero nos guardamos el punto por el que va reproduciendo el audio,
        // y le decimos al mediaPlayer que pause
        if (mediaPlayer != null) {
            pos = mediaPlayer!!.currentPosition
            mediaPlayer!!.pause()
        }

        // Hay un método que se ejecuta en el que voy a guardar la posición por
        // la que va el reproductor (no tengo que decirle dónde lo tiene que guardar,
        // se ejecuta solo
        // Es éste:
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        Log.d("MultimediaLog", "En el onSaveInstanceState")

        // Aquí guardamos la posición por la que va el reproductor
        if (mediaPlayer != null) {
            // Para guardar una variable en el bundle, en el paquete:
            bundle.putInt("posicion", pos)
            Log.d("MultimediaLog", "Valor de pos = $pos");
        }

        // Tras el onPause, pasa por este método y hace lo que nosotros le pongamos
        // Es como un "guardar información"; esta información almacenada se recupera
        // con un método que se ejecuta tras el onResume
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle!!) // Por si el bundle llega vacío

        Log.d("MultimediaLog", "En el onRestoreInstanceState")

        // Comprobamos si el bundle trae datos y si el mediaPlayer NO es null
        if (bundle != null && mediaPlayer != null) {
            pos = bundle.getInt("posicion")
        }
    }

    // Tras mirar el Logcat: el onSaveInstanceState se ejecuta DESPUÉS DEL ONCREATE
    // El onSaveInstanceState se ejecuta ANTES DEL ONDESTROY
    // Se guarda info antes del ondestroy, y tras un nuevo oncreate se restaura
    // Igualmente también se guarda info en el onpause que se restaura en el onresume
    // Más cosas en el PDF de reproducir multimedia, en el classroom de R3 Multimedia


    /**
     * Crear objetos del xml
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}