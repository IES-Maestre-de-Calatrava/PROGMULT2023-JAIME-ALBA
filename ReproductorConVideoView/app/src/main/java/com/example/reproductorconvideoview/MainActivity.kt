package com.example.reproductorconvideoview

import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import com.example.reproductorconvideoview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Creado con minSdk de la 33, pero el compile hay que subirlo a la 34
    private lateinit var binding: ActivityMainBinding

    // Nos declaramos un VideoView, y otra clase para ponerle controles
    lateinit var mVideoView: VideoView
    lateinit var mediaController: MediaController // El de widget

    // Para controlar el estado de reproducción
    private var currentPosition: Int = 0
    private var isVideoPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        mVideoView = binding.videoView

        mVideoView.setOnPreparedListener{
            // Se hace un prepareListener para en el video view cargar el vídeo y entonces habilitar
            // t.odo lo que le ponga aquí dentro, NO antes de cargarlo
            mVideoView.start()
            isVideoPlaying = true
            mVideoView.requestFocus() // Le pongo el foco
            ponerControles(mVideoView) // Le pongo los controles
            binding.botonReproducirVideo.isEnabled = false // Cuando comience el vídeo, el botón de
            // reproducir se deshabilita para que no le puedan dar varias veces al play

        }

        mVideoView.setOnCompletionListener {
            // Habilito que cuando el vídeo termine de reproducirse se le pueda dar otra vez al play
            binding.botonReproducirVideo.isEnabled = true
            isVideoPlaying = false
        }
    }

    fun cargarMultimedia(v: View) {
        // Este método se encarga de cargar el vídeo. Cuando lo haga:
        mVideoView.setVideoURI(Uri.parse("android.resource://"+packageName+"/"+R.raw.video)) // Se le pasa la uri de un determinado vídeo
        // Cuando le demos al botoncito, internamente coge el vídeo
    }

    private fun ponerControles(videoView: VideoView) {
        mediaController = MediaController(this)
        // Le decimos dónde queremos poner esos controles
        mediaController.setAnchorView(videoView as View) // Se lo ponemos en la propia vista
        // Si lo quiero poner en el padre que contiene al videoView: videoView.parent as View
        videoView.setMediaController(mediaController)
    }

    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Cosas que hacer:
    // -Cargar vídeos desde la galería
    // -Mantener el control al girar el móvil

    override fun onPause() {
        super.onPause()
        if (mVideoView.isPlaying) {
            currentPosition = mVideoView.currentPosition
            mVideoView.pause()
            isVideoPlaying = true
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt("currentPosition", currentPosition)
        bundle.putBoolean("isVideoPlaying", isVideoPlaying)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        currentPosition = bundle.getInt("currentPosition")
        isVideoPlaying = bundle.getBoolean("isVideoPlaying")
    }

    override fun onResume() {
        super.onResume()
        if (isVideoPlaying) {
            mVideoView.seekTo(currentPosition)
            mVideoView.start()
        }
    }


}