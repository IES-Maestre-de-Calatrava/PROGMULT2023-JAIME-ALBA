package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.monsterhunterfinder.databinding.ActivityPerfilAnadirBinding

/**
 * Activity dedicada a la adición de imágenes a la
 * galería que el usuario tiene disponible en su perfil.
 * Deberá hacerse mediante el enlace de la imagen.
 * @author Jaime
 */
class activity_perfil_anadir : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilAnadirBinding
    private val FOTO_INSERTADA: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Se establece un listener para el botón de confirmar;
        // el método volverConFoto() será el que envíe un intent
        // a la activity anterior a la presente, conteniendo
        // en dicho intent el enlace de la imagen que se quiera
        // añadir a la galería
        binding.botonConfirmarAnadir.setOnClickListener {
            volverConFoto()
        }
    }

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityPerfilAnadirBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Función que finaliza la activity actual, devolviendo
     * al usuario a aquella activity desde la que hubiera
     * accedido.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun volver(view: View) {
        finish()
    }

    /**
     * Función que vuelve a la activity que ha llamado a la presente
     * con un intent que incluye datos recogidos de la caja de
     * texto presente en el layout de esta activity.
     * Para recoger dichos datos, será necesario un activityResultLauncher
     * en la activity que ha llamado a la presente.
     * Los datos que se recogen son el enlace de la imagen que
     * será añadida a la galería.
     */
    fun volverConFoto() {
        val intent = Intent()
        intent.putExtra("enlace", binding.textoEnlaceFoto.text.toString())
        setResult(FOTO_INSERTADA, intent)
        finish()
    }
}