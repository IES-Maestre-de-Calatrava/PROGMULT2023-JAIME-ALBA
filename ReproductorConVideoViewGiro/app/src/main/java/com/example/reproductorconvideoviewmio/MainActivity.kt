package com.example.reproductorconvideoviewmio

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.VideoView
import com.example.reproductorconvideoviewmio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var mVideoView: VideoView? = null

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


        if (mVideoView == null) {
            mVideoView = binding.videoView
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            mVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.video))
        }


        if (savedInstanceState == null) {
            // Si es el primer onCreate, configuramos los botones y la reproducción inicial
            Log.d("MultimediaLog", "En el onCreate, sin datos a recuperar");
            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false

            binding.retrButton.isEnabled = false
            binding.avanButton.isEnabled = false

            controlVideo()
        } else {
            Log.d("MultimediaLog", "En el onCreate, con datos a recuperar")
            if (isPlaying) {

                binding.playButton.isEnabled = false
                binding.stopButton.isEnabled = true
                binding.pauseButton.isEnabled = true

                binding.retrButton.isEnabled = true
                binding.avanButton.isEnabled = true
            } else {

                binding.playButton.isEnabled = true
                binding.stopButton.isEnabled = false
                binding.pauseButton.isEnabled = false

                binding.retrButton.isEnabled = false
                binding.avanButton.isEnabled = false
            }
        }
    }

    private fun controlVideo() {
        binding.playButton.setOnClickListener {
            isPlaying = true
            cargarMultimedia()
        }

        binding.stopButton.setOnClickListener {
            isPlaying = false
            pararReproduccion()
        }

        binding.pauseButton.setOnClickListener {
            if (mVideoView != null) {
                if (mVideoView!!.isPlaying) {
                    isPaused = true
                    mVideoView!!.pause()
                } else {
                    isPaused = false
                    mVideoView!!.start()
                }
            }
        }

        binding.retrButton.setOnClickListener {
            retrocederReproduccion(10000)
        }

        binding.avanButton.setOnClickListener {
            avanzarReproduccion(10000)
        }
    }

    private fun cargarMultimedia() {

        if (mVideoView == null) {
            mVideoView = binding.videoView
            mVideoView!!.setOnCompletionListener { pararReproduccion() }
            mVideoView!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.video))
        }

        mVideoView!!.start()

        binding.playButton.isEnabled = false
        binding.stopButton.isEnabled = true
        binding.pauseButton.isEnabled = true
        binding.retrButton.isEnabled = true
        binding.avanButton.isEnabled = true
    }

    private fun retrocederReproduccion(millis: Int) {
        if (mVideoView != null) {
            val newPosition = mVideoView!!.currentPosition - millis
            mVideoView!!.seekTo(newPosition.coerceIn(0, mVideoView!!.duration))
        }
    }

    private fun avanzarReproduccion(millis: Int) {
        if (mVideoView != null) {
            val newPosition = mVideoView!!.currentPosition + millis
            mVideoView!!.seekTo(newPosition.coerceIn(0, mVideoView!!.duration))
        }
    }

    private fun crearObjetosDelXml() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun pararReproduccion() {
        if (mVideoView != null) {
            mVideoView!!.pause()
            pos = 0
            mVideoView!!.seekTo(pos)

            mVideoView = null

            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.pauseButton.isEnabled = false
            binding.retrButton.isEnabled = false
            binding.avanButton.isEnabled = false
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MultimediaLog", "En el onPause")

        if (mVideoView != null) {
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // En el onDestroy, el mediaPlayer se iguala a null para liberar recursos
        Log.d("MultimediaLog", "En el onDestroy")
        if (mVideoView != null) {
            mVideoView = null
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle!!)
        Log.d("MultimediaLog", "En el onRestoreInstanceState")

        if (bundle != null) {
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

        if (mVideoView != null) {
            Log.d("MultimediaLog", "EN EL ONRESUME ANTES DEL SEEK, valor de pos = $pos");
            Log.d("MultimediaLog", "EN EL ONRESUME DESPUÉS DEL SEEK, valor de pos = $pos");

            if (isPlaying && !isPaused) {
                mVideoView!!.start()
            }

            mVideoView!!.seekTo(pos)

            controlVideo()
        }
    }
}