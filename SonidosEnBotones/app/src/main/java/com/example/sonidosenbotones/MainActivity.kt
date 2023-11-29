package com.example.sonidosenbotones

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sonidosenbotones.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Para reproducir sonidos que van dentro de la app, necesito una estructura que los almacene.
    // En Res, me creo una carpeta que SÍ O SÍ se tiene que llamar raw.
    // Click derecho en res > new directory > raw
    // En ella meto los dos audios (arrastro)

    // Tener claro que ésto es para audios cortos

    // Hay una cosa que me permite almacenar info en el xml del botón.
    // En ese apartado, a cada botón le voy a poner un identificador, para
    // que ambos llamen al mismo método pero éste discrimine a cuál
    // estoy llamando. Ésto es lo que se llama EL TAG. Me voy al xml, y a
    // cada botón le pongo un TAG: sonido1 y sonido2.

    // Éste código va a ser para versiones superiores al API 21.

    private lateinit var binding: ActivityMainBinding

    // Clase que me carga todos los audios en memoria; los cargo en memoria y ya los tengo
    // disponibles para usar
    lateinit var soundPool: SoundPool
    var sonido1: Int=0
    var sonido2: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Me preparo los atributos que quiero que tenga ese soundPool
        // Le indico que va a ser de tipo música y que se va a usar en la multimedia
        var audioAtributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        soundPool = SoundPool.Builder()
            // El máximo nº de audios que pueda reproducir simultáneamente
            .setMaxStreams(5)
            // Le doy los atributos que he definido arriba
            .setAudioAttributes(audioAtributes)
            .build()

        // Cargamos los audios en el soundPool. El soundPool, cuando cargo un audio, me devuelve
        // el ID de ese audio
        sonido1 = soundPool.load(this, R.raw.sonido1, 1)
        sonido2 = soundPool.load(this, R.raw.sonido2, 1)
        // Me falta hacer un método que reproduzca esos audios; no puede ser
        // privado, porque por parámetro va a recibir la view
    }

    fun playSound(view: View) {
        // Tengo que averiguar a qué view se ha llamado. Hasta ahora lo hacía con el ID,
        // ahora lo voy a hacer con el tag. Me declaro un sonido y, según lo que tenga el
        // tag, se asigna a uno sonido o a otro.
        var sonido: Int=0

        when (view.tag) {
            "sonido1" -> sonido=sonido1
            "sonido2" -> sonido=sonido2
        }

        // Y ejecuto el comando que reproduce ese audio
        // Parámetros: el sonido que voy a reproducir, volumen de los canales izquierdo
        // y derecho, prioridad, el si se hace en bucle (0 es que no) y cosa no importante
        soundPool.play(sonido, 1F, 1F, 1, 0, 1F)
    }

    private fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}