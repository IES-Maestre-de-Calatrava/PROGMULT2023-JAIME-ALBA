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

    private fun crearObjetosDelXml() {
        binding=ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun volver(view: View) {
        finish()
    }

    fun confirmarCambios() {
        val intent = Intent()
        intent.putExtra("Nombre", binding.textoNombreEditar.text.toString())
        intent.putExtra("Bio", binding.textoBioEditar.text.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

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