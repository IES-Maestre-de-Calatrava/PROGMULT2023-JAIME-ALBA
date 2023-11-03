package com.example.monsterhunterfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.example.monsterhunterfinder.databinding.ActivityPerfilBinding

class activity_perfil : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var activityEditarPerfilResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }

    private fun crearObjetosDelXml() {
        binding=ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imagenEditar.setOnClickListener{
            abrirEditar()
        }

        activityEditarPerfilResultLauncher = registerForActivityResult(StartActivityForResult()) {
            resultado ->
                if (resultado.data!=null) {
                    val datos: Intent = resultado.data!!

                    val nombre = datos.getStringExtra("Nombre")
                    val bio = datos.getStringExtra("Bio")

                    binding.textoNombre.setText(nombre)
                    binding.textoBio.setText(bio)
                }
        }
    }

    fun volver(view: View) {
        finish()
    }

    fun abrirEditar() {
        val intent = Intent(this, activity_editar_perfil::class.java)
        activityEditarPerfilResultLauncher.launch(intent)
    }
}