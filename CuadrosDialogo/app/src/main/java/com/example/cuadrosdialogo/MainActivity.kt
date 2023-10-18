package com.example.cuadrosdialogo

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.cuadrosdialogo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }



    fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    // Cuadro de diálogo que limpie el contenido de los EditText que
    // se hayan escrito. Función que limpie los campos de los
    // EditText.
    private fun limpiarPantalla() {
        binding.editTextNombre.setText("")
        binding.editTextEmail.setText("")
        binding.editTextTelf.setText("")
    }

    // Ahora hago el método al que SÍ va a llamar el botón
    fun mostrarDialogo(view: View) {
        // Vamos a utilizar un builder para la clase AlertDialog
        // Hago el import dela de appcompat (salen dos)
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Limpiar campos")
            .setMessage("¿Seguro que quieres limpiar los campos?")
            // Para que el usuario no pueda salirse de ese cuadro
            // de diálogo sin responder sí o no
            .setCancelable(false)

            // Ahora decimos qué ocurre según se use
            // el botón positivo o negativo

            // Le indico qué muestra el botón positivo y qué
            // acción va a ejecutar
            .setPositiveButton(
                "Si",
                DialogInterface.OnClickListener{
                        dialog, id -> limpiarPantalla()
                }

            )
            .setNegativeButton(
                "No",
                DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                }
            )
            .show()


    }
}