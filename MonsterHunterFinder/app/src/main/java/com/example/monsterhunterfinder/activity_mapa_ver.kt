package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monsterhunterfinder.databinding.ActivityMapaAnadirBinding
import com.example.monsterhunterfinder.databinding.ActivityMapaVerBinding

class activity_mapa_ver : AppCompatActivity() {

    private lateinit var binding: ActivityMapaVerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()


        val intent: Intent = intent
        var titulo = intent.getStringExtra("titulo")
        var descripcion = intent.getStringExtra("descripcion")
        var tipo = intent.getStringExtra("tipo")

        // El texto de cabecera cambia según si se está añadiendo punto de interés
        // o punto de reunión
        if (tipo=="punto") {
            binding.textoCabeceraVer.text = getString(R.string.punto)
        } else if (tipo=="reunion") {
            binding.textoCabeceraVer.text = getString(R.string.reunion)
        }

        binding.textoTituloMarcadorVer.setText(titulo)
        binding.textoDescripcionMarcadorVer.setText(descripcion)


        binding.botonVolverMapaVer.setOnClickListener{
            finish()
        }
    }


    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding = ActivityMapaVerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}