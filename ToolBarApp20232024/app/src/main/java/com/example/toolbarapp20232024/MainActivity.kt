package com.example.toolbarapp20232024

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.toolbarapp20232024.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Le indicamos que la ToolBar pase a ser nuestra ActionBar; deshabilitar
        // la action bar que venga por defecto en temas y poner la nuestra
        setSupportActionBar(binding.appbar.toolbarApp2)

        // La siguiente línea es para que, en ciertos temas, no salga el título de
        // la app en la toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun crearObjetosDelXml() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun abrir(view: View) {
        val intent = Intent(this, activity2::class.java)
        startActivity(intent)
    }

    /**
     * On create options menu
     *
     * Método para sobreescribir dicho método; crea un menú que será el que se utilice
     * en nuestra toolbar asignada a la Action Bar. Crea objetos a partir del xml, hace
     * el "inflado".
     *
     * @param menu
     * @return true si se ha realizado bien
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act1, menu)
        return true
    }

    /**
     * On option item selected
     *
     * Controla las pulsaciones entre los elementos (item) del menú
     *
     * @param item elemento del menú sobre el que se ha pulsado
     * @return true si la operación de pulsación se ha completado correctamente
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // En R está todo lo relativo al proyecto

            R.id.acercaDe -> {
                lanzarAcercaDe()
                true
            }

            R.id.Contactartlfn -> {
                llamarTelefono()
                true
            }

            R.id.Web -> {
                abrirPagina()
                true
            }

            // Ésto es del 16/10/2023
            R.id.ContactarMail -> {
                mandarCorreo()
                true
            }

            R.id.Ubica -> {
                verMapa()
                true
            }

            R.id.CompartirApp -> {
                compartir()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Ésto es del 16/10/2023
    fun mandarCorreo() {
        // Función que permite mandar un correo
        // Intent especial; distinto, con más parámetros, como el asunto o el
        // contenido del correo
        // Para ésta, el ACTION_SEND va a tratar de abrir un sistema de mensajería
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                // El EXTRA_SUBJECT es el asunto
                putExtra(Intent.EXTRA_SUBJECT, "Correo de prueba")
                // El EXTRA_TEXT es el contenido del correo
                putExtra(Intent.EXTRA_TEXT, "Hola, ésto es un mensaje de prueba para la asignatura de ProgMult")
                // Declaramos un array en el que podemos meter varias direcciones
                putExtra(Intent.EXTRA_EMAIL, arrayOf("jaimealbaruiz@gmail.com"))
            }
        )
    }


    fun verMapa() {
        startActivity(
            // Al uri parse se le pasan unas coordenadas
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:39.2741,  -3.5193"))
        )
    }


    fun compartir() {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Mira el sitio que he encontrado http://google.com")
            }
        )
    }





    fun lanzarAcercaDe() {
        // Mensaje que aparece por pantalla y se quita al poquito rato
        Toast.makeText(this, "Aplicación desarrollada por break4learning", Toast.LENGTH_LONG).show()
    }

    /**
     * Se lanza el intent y se busca la app del sistema que hace llamadas. Le paso por String el número.
     */
    fun llamarTelefono() {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel: 686174141")))
    }

    /**
     * Función que abre una página web. Por String le paso la web a buscar.
     */
    fun abrirPagina() {
        // Para ésta, el ACTION_VIEW va a buscar una página que abrir
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://break4learning.weebly.com")))
    }
}