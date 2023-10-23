package com.example.cloudfirestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cloudfirestore.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Ir aquí tras leer lo del cuaderno
    // Para conectar a FireBase es necesario crearse una instancia, para
    // poder trabajar con la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Nos declaramos una colección, que equivale al NODO PRINCIPAL
    // de un XML, el que lo aglutina todo y del que cuelgan todos
    // los elementos. Un nodo gordo EMPRESAS.
    // Vamos a tener, con el ejemplo de empresas, una colección de
    // elementos empresa. En cada una hay asociado un NIF, y cada
    // NIF tiene asociado un nombre y una dirección.
    // <Empresas>
    //      <NIF>
    //          <Nombre>
    //          <Direccion>
    //      </NIF>
    // </Empresas>
    private val myCollection = db.collection("empresas")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // No vamos a usar el onClick. Vamos a trabajar con un listener de
        // clicks que al notar un click detona eventos.
        // Creamos un listener que ejecuta este método al percibir
        // un click,
        binding.buttonGuardar.setOnClickListener {
            guardarRegistro()
        }

        binding.buttonCargar.setOnClickListener {
            cargarDet()
        }

        binding.buttonEliminar.setOnClickListener {
            eliminarRegistro()
        }

        binding.buttonTraerTodos.setOnClickListener {
            listarTodos()
        }

        binding.buttonFiltrarPorCiudad.setOnClickListener {
            listarConFiltro()
        }
    }

    /**
     * Guardar registro
     *
     * Si el registro con el ID suministrado no existe, entonces lo
     * crea. Si ya existe, modifica los datos. El mismo método se
     * utiliza tanto para guardar registros nuevos como para alterar
     * registros ya creados.
     */
    private fun guardarRegistro() {
        // Dentro de la colección, accedo a un determinado documento
        // con un ID. Si tenemos el NIF dentro de los campos de la empresa,
        // se utiliza un ID interno; si no lo creo yo, me lo crea el propio
        // programa. El ID hace de clave primaria; no se puede repetir, e
        // identifica cada registro de manera unívoca.

        // Si no utilizo el NIF, lo dejo con myCollection.document() y me
        // lo genera solito.
        myCollection.document(binding.editTextNif.text.toString()).set(
            hashMapOf(
                // Nombre de la etiqueta y a qué la asocio
                "nombre" to binding.editTextNombre.text.toString(),
                "direccion" to binding.editTextDireccion.text.toString()
                // Tras hacerlo, lo pruebo; meto cosas en los tres campos,
                // le doy a guardar y consulto FireBase, en Firestore Database
                // y en la base de datos de mi app. Debería estar.
            )
        )
        // le voy a decir que me muestre un mensaje cuando haga un
        // guardado exitoso
        resultadoOperacion("Registro guardado correctamente")
    }


    // Ahora, el método para cargar datos.
    private fun cargarDet() {
        // Antes usábamos un .set del HashMap. Ahora, como lo que quiero
        // es obtener, uso un .get
        myCollection.document(binding.editTextNif.text.toString())
            // Al hacer un .get(), lanza a internet una petición que puede tardar
            // más o menos; no dejo ahí la app cargando, sino que le pongo un
            // listener y ya reaccionará cuando reciba los datos.
            // Hay otro listener que es onCancel.
            .get()
            .addOnSuccessListener{
                // it es un iterador que itera por la database; la
                // copia y se la trae a local para poder trabajar con
                // ella aunque ya no haya conexión
                // if: "si hay datos"
                if (it.exists()) {
                    // Del snapshot (imagen que trae de la base de datos)
                    // nos trae el nombre y la dirección
                    binding.editTextNombre.setText(it.get("nombre").toString())
                    binding.editTextDireccion.setText(it.get("direccion").toString())
                } else {
                    // Si no trae datos, porque no los haya y haya
                    // retornado vacío, muestro un mensaje
                    resultadoOperacion("No existe una empresa con ese NIF")
                }
            }
    }


    // Y ahora, el eliminar registros
    private fun eliminarRegistro() {
        myCollection.document(binding.editTextNif.text.toString())
        // Voy a borrar el NIF que metan ahí
            .delete()
        resultadoOperacion("Registro eliminado correctamente")
    }


    private fun listarTodos() {
        // No quiero un registro completo, me los quiero traer todos
        myCollection
            .get()
        // Me añado un listener
            .addOnSuccessListener {
                // Tengo que comprobar que ha habido un resultado
                resultado ->
                // Por si ejecuto varias veces, limpio el textView
                binding.textViewLista.setText("")
                // Una vz limpio, tengo que recorrer todos los elementos
                // que vengan en ese resultado
                // Por cada elemento obtenido del resultado, le voy a
                // decir que me escriba una línea
                for (documento in resultado) {
                    binding.textViewLista.append(
                    documento.get("nombre").toString()+" - "+
                    documento.get("direccion").toString() + "\n")
                }
            }
    }

    // El siguiente es mostrar todos, pero filtrando registros
    private fun listarConFiltro() {
        myCollection
            // Es igual, pero con un whereEqualTo para filtrar aquellos
            // cuya dirección coincida con aquello que le hayamos metido en
            // el campo direccion
            .whereEqualTo("direccion", binding.editTextDireccion.text.toString())
            .get()
            // Me añado un listener
            .addOnSuccessListener {
                    resultado ->
                binding.textViewLista.setText("")

                for (documento in resultado) {
                    binding.textViewLista.append(
                        documento.get("nombre").toString()+" - "+
                                documento.get("direccion").toString() + "\n")
                }
            }
    }


    private fun resultadoOperacion(mensaje: String) {
        // Le voy a decir al mensaje de guardado exitoso que me
        // limpie los editText
        binding.editTextNif.setText("")
        binding.editTextNombre.setText("")
        binding.editTextDireccion.setText("")
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}