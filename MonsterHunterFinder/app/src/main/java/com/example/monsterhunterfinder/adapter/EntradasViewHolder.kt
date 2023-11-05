package com.example.monsterhunterfinder.adapter

import android.content.DialogInterface
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Entrada
import com.example.monsterhunterfinder.R
import com.example.monsterhunterfinder.activity_diario_ver
import com.example.monsterhunterfinder.databinding.ItemEntradaBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class EntradasViewHolder (view: View): RecyclerView.ViewHolder (view) {
    val binding = ItemEntradaBinding.bind(view)



    fun render(entradaDiario: Entrada,
               borrarRegistro:(Int, Int) -> Unit,
               lanzarActivityModificar:(Int, Entrada) -> Unit) {
        binding.textoListaArma.text = entradaDiario.arma
        binding.textoListaTitulo.text = entradaDiario.titulo



        binding.botonVer.setOnClickListener {
            val contexto = itemView.context
            val intent = Intent(contexto, activity_diario_ver::class.java)

            var identificador: String = entradaDiario.numEntrada.toString()
            intent.putExtra("Identificador", identificador)

            contexto.startActivity(intent)
        }


        binding.botonEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(itemView.context)

            builder.setTitle(R.string.cuidado)
                .setMessage(R.string.seguroBorrado)
                .setCancelable(false)
                .setPositiveButton(
                    R.string.vaciarFiltroSi,
                    DialogInterface.OnClickListener{
                            dialog, id -> borrarRegistro(adapterPosition, entradaDiario.numEntrada)
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

        binding.botonEditar.setOnClickListener {
            lanzarActivityModificar(adapterPosition, entradaDiario)
        }
    }
}