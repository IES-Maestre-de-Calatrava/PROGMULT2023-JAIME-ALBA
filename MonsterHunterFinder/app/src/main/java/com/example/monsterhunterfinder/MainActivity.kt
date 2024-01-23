package com.example.monsterhunterfinder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.monsterhunterfinder.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


/**
 * Activity Main, en la que se da acceso al usuario a
 * su perfil, al diario de caza y al buscador de jugadores.
 * También a la versión completa de la Toolbar, con
 * distintas opciones.
 * @author Jaime
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var soundPool: SoundPool
    var sonidoperfil: Int=0
    var sonidodiario: Int=0
    var sonidobuscar: Int=0
    var sonidomapa: Int=0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Se establece como toolbar la que nos hemos creado
        setSupportActionBar(binding.toolbar.toolbar)
        // Se establece que no se muestre el título de la toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Me preparo los atributos del SoundPool
        var audioAtributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAtributes)
            .build()

        // Cargo los audios en el SoundPool
        sonidoperfil = soundPool.load(this, R.raw.sonidoperfil, 1)
        sonidodiario = soundPool.load(this, R.raw.sonidodiario, 1)
        sonidobuscar = soundPool.load(this, R.raw.sonidobuscar, 1)
        sonidomapa = soundPool.load(this, R.raw.sonidomapa, 1)


    }

    /**
     * Función que permite la reproducción de sonidos al presionar
     * en los botones del menú principal
     */
    fun playSound(view: View) {
        var sonido: Int=0
        when (view.tag) {
            "sonidoperfil" -> sonido=sonidoperfil
            "sonidodiario" -> sonido=sonidodiario
            "sonidobuscar" -> sonido=sonidobuscar
            "sonidomapa" -> sonido=sonidomapa
        }

        soundPool.play(sonido, 1F, 1F, 1, 0, 1F)
    }



    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Aquí se inflan los objetos xml de la toolbar que se use
        menuInflater.inflate(R.menu.toolbar_main_activity, menu)
        return true
    }


    /**
     * Función que lanza un intent para abrir la
     * activity asociada al perfil del usuario.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun abrirPerfil(view: View) {
        playSound(view)

        val intent = Intent(this, activity_perfil::class.java)
        startActivity(intent)
    }

    /**
     * Función que lanza un intent para abrir la
     * activity asociada al diario de caza.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun abrirDiario(view: View) {
        playSound(view)

        val intent = Intent(this, activity_diario_vista::class.java)
        startActivity(intent)
    }

    /**
     * Función que lanza un intent para abrir la
     * activity asociada al mapa para gestión de reuniones
     * y eventos de Monster Hunter Now.
     */
    fun abrirMapa(view: View) {
        playSound(view)

        val intent = Intent(this, activity_mapa::class.java)
        startActivity(intent)
    }

    /**
     * Función que lanza un intent para abrir la
     * activity asociada al buscador de usuarios.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun abrirBuscador(view: View) {
        playSound(view)

        val intent = Intent(this, activity_buscador::class.java)
        startActivity(intent)
    }


    /**
     * Función que lanza un intent para enviar un correo
     * electrónico con el contenido que se pase al intent
     * en forma de putExtras.
     */
    fun compartir() {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.asunto))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.contenido))
                putExtra(Intent.EXTRA_EMAIL, arrayOf("jaimealbaruiz@gmail.com"))
            }
        )
    }

    /**
     * Función que muestra por pantalla un pequeño texto
     * sobre el desarrollador de la app.
     */
    fun lanzarAcercaDe() {
        Toast.makeText(this, R.string.contacto, Toast.LENGTH_LONG).show()
    }

    /**
     * Función que lanza un intent para abrir una
     * activity con la página web contenida en la
     * String que se le indique al parser.
     */
    fun abrirWeb() {
        val lanzarWeb: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mhrise.kiranico.com/es"))
        startActivity(lanzarWeb)
    }

    /**
     * Función que lanza un intent para abrir
     * la activity dedicada a la selección de
     * preferencias del usuario.
     */
    fun abrirPreferencias() {
        val abrirPref: Intent = Intent(this, activity_settings::class.java)
        startActivity(abrirPref)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // En esta función se lanza una acción u otra dependiendo
        // del elemento item de la toolbar que se seleccione
        return when (item.itemId) {
            R.id.AcercaDe -> {
                lanzarAcercaDe()
                true
            }

            R.id.Web -> {
                abrirWeb()
                true
            }

            R.id.CompartirApp -> {
                compartir()
                true
            }

            R.id.Preferencias -> {
                abrirPreferencias()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}