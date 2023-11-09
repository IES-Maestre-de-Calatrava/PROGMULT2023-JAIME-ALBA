package com.example.monsterhunterfinder

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.monsterhunterfinder.databinding.ActivityEditarPerfilBinding

class activity_editar_perfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityEditarPerfilBinding.inflate(layoutInflater)
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
     * Función que crea y envía un intent con varios putExtra, los
     * cuales recogen el contenido de los campos de texto (las
     * views editText) de la activity, para que sea recogido y
     * tratado en la activity del perfil de usuario.
     */
    fun confirmarCambios() {
        val intent = Intent()
        intent.putExtra("Nombre", binding.textoNombreEditar.text.toString())
        intent.putExtra("Bio", binding.textoBioEditar.text.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * Función que crea un cuadro de diálogo al presionarse
     * la view a la que va asociada, procurando que el usuario
     * se asegure de la acción que desea ejecutar.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun mostrarConfirmar(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.cuidado)
            .setMessage(R.string.confirmarPregunta)
            .setCancelable(false) // El usuario no puede salirse clickando fuera
            .setPositiveButton(
                R.string.vaciarFiltroSi,
                DialogInterface.OnClickListener{
                        dialog, id -> confirmarCambios()
                }
            )
            .setNegativeButton(
                R.string.vaciarFiltroNo,
                DialogInterface.OnClickListener{
                        dialog, id -> dialog.cancel()
                }
            )
            .show()
    }
}