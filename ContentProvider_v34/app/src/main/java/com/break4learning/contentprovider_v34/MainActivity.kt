package com.break4learning.contentprovider_v34

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.view.View
import androidx.core.app.ActivityCompat
import com.break4learning.contentprovider_v34.databinding.ActivityMainBinding
import java.util.Date

/**
 * Main activity
 *
 * Esta clase muestra cómo trabajar con un ContentProvider. En concreto con el log de llamadas
 *
 * @author Javier García-Retamero Redondo
 *
 */

// Primero configuramos el apartado de permisos. Nos vamos al Manifest y metemos los permisos
// de READ_CONTACTS, WRITE_CALL_LOG y READ_CALL_LOG (ya están). Tras eso, nos volvemos al main.
class MainActivity : AppCompatActivity() {

    /**
     * Binding
     */
    private lateinit var binding: ActivityMainBinding

    // Variable que se usa más tarde, en la solicitud de permisos
    private val PETICION_PERMISOS_CONTACTOS=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Comprobamos si tenemos los permisos; comprobamos si los permisos a usar (alguno) están denegadon
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(
                // Le pasamos el contexto actual y un array de los permisos que vamos a solicitar
                this,
                arrayOf(
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
                ),
                PETICION_PERMISOS_CONTACTOS
            )
        }
    }

    // Si vamos a usar permisos, es necesario sobreescribir un método; es el método que se
    // ejecuta la comprobación de si el usuario ha concedido o no esos permisos.
    // Por ejemplo, habilitar o deshabilitar botones según permisos.
    // Al solicitar unos permisos distintos de PETICION_PERMISOS_CONTACTOS, va a seguir
    // entrando por este método, por lo que tendré que tener un código y evaluar para
    // cada tipo de permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PETICION_PERMISOS_CONTACTOS) {
            if (grantResults.size == 3 && // Si el usuario ha concedido los 3 permisos
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                // En caso de que se hayan conceidido los permisos, se ponen botones a visible
                binding.BtnLlamadas.visibility = View.VISIBLE
                binding.BtnEscribir.visibility = View.VISIBLE
            } else {
                // En el caso de que aguno de los permisos se haya denegado
                binding.BtnLlamadas.visibility = View.INVISIBLE
                binding.BtnEscribir.visibility = View.INVISIBLE
                // Vamos al layout y vemos el onClick que tienen los botones cargar y llamadas.
                // En el Gradle ponerle el minSdk a la 33.
                // Ejecutamos y vemos si nos pide los permisos.
                // Solamente eso por ahora.
                // Ahora sí, nos hacemos la función llamar en el Main.
            }
        }
    }

    fun llamadas(view: View) {
        val projection = arrayOf( // Tres columnas
            // La primera columna SIEMPRE tiene que ser una ID, ésto va a depender del
            // content provider que utilicemos
            CallLog.Calls._ID,
            // Si es de entrada o de salida
            CallLog.Calls.TYPE,
            CallLog.Calls.NUMBER
        )

        val llamadasUri = CallLog.Calls.CONTENT_URI // Depurarlo yo y ver cuál es mi uri
        // Como nos va a devolver toda la lista de llamadas, nos tenemos que crear un cursor
        val cur: Cursor
        // Para llamar al ContentProvider hay que utilizar un Resolver
        val cr = contentResolver

        // Por seguridad, está bien volver a preguntar por los permisos, por si en la parte
        // principal se nos ha olvidado hacer algo; voy a necesitar las de READ_CONTACTS y
        // READ_CALL_LOG

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            // Si está denegado, podemos volver a solicitar permisos o hacer un return y salir.
            // Le vuelvo a solicitar los tres pa tardar menos.
            ActivityCompat.requestPermissions(
                // Le pasamos el contexto actual y un array de los permisos que vamos a solicitar
                this,
                arrayOf(
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
                ),
                PETICION_PERMISOS_CONTACTOS
            )*/
            // Hay que llamara unos métodos u otros dependiendo de lo que vayamos a hacer. Para hacer consultas
            // con el ContentResolver:
            cur = cr.query( // Todo ésto es una consulta; hay que asignarla al cursor
                llamadasUri, // El primero es la uri, la dirección del content provider
                projection,  // Los campos que queremos que se traiga, el FROM
                null,         // Condiciones que queremos que cumpla la query
                null,
                null        // Condiciones para ordenarla de alguna forma
            )!! // Puede que el cursor no devuelva nada
            // Ahora hay que recorrer todo el cursor e ir recuperando elementos; en el
            // textView del layout vamos a mostrar toda la lista de llamadas que encuentre
            // el cursor.

            // Por si lo uso varias veces, primero lo limpio.
            binding.TxtResultados.text=""

            // Y empiezo a recorrer el cursor. El cursor va a traer una lista de cosas, y le tengo
            // que ir diciendo que me devuelva según posición. La primera es ID, la segunda es tipo...
            // Así siempre.
            // Pero si el día de mañana Android decide cambiar de sitio las columnas, nos revienta todo.
            // Para evitarlo, primero le pregunto en qué posición está tal cosa (tipo, id, etc), me
            // devuelve esa posición y la utilizo.
            if (cur.moveToFirst()) { // Si hay primer elemento
                var tipo: Int // llamada entrada o salida
                var id: Int
                var tipoLlamada = ""
                var telefono: String

                val colId = cur.getColumnIndex(CallLog.Calls._ID) // Me devuelve el índice en el que está la columna ID
                val colTipo = cur.getColumnIndex(CallLog.Calls.TYPE)
                val colTelefono = cur.getColumnIndex(CallLog.Calls.NUMBER)



                // El moveToFirst comprueba que hay un primer elemento. Si ejecuta, es que hay un
                // primer elemento. Hecha la comprobación, empezamos a recorrer el cursor con un
                // do-while.
                do {
                    id = cur.getInt(colId)
                    tipo = cur.getInt(colTipo)
                    telefono = cur.getString(colTelefono)

                    // El tipo puede ser un tipo de dato que no me interesa; lo convierto.
                    when (tipo) {
                        CallLog.Calls.INCOMING_TYPE -> tipoLlamada = "Entrada"
                        CallLog.Calls.OUTGOING_TYPE -> tipoLlamada = "Salida"
                        CallLog.Calls.MISSED_TYPE -> tipoLlamada = "Perdida"
                    }

                    // Nos falta decir que lo muestre en el textView. Hacemos un append de línea a
                    // línea.
                    binding.TxtResultados.append("$id - $tipoLlamada - $telefono\n")


                } while (cur.moveToNext())
                // Y cerramos el cursor para no dejar la memoria reservada
                cur.close()
            //}

        }
    }


    /**
     * Crear objetos del xml
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }



    // Este método es un copiapega de llamadas, pero cargando solamente una fila
    // En la projection le voy a pedir que me devuelva una cosa más, la duración de la llamada
    // Hay que cambiar la Uri, porque cada uri que utilicemos TIENE QUE SER DIFERENTE
    fun cargarFila(view: View) {
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.TYPE,
            CallLog.Calls.NUMBER,
            // Le añadimos la duración
            CallLog.Calls.DURATION
        )


        // A cada Uri se le asigna un ID; para que no dé cosas raras, se usa el método éste
        // y el encode, para encodearlo en la propia uri
        val llamadasUri = Uri.withAppendedPath(CallLog.Calls.CONTENT_URI, Uri.encode(binding.editTextID.text.toString()))

        val cur: Cursor

        val cr = contentResolver

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(

                this,
                arrayOf(
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
                ),
                PETICION_PERMISOS_CONTACTOS
            )

            cur = cr.query(
                llamadasUri,
                projection,
                null,
                null,
                null
            )!!

            if (cur.moveToFirst()) {
                var tipo: Int
                var id: Int
                var tipoLlamada = ""
                var telefono: String
                // Variable para la duración
                var duration: Int

                val colId = cur.getColumnIndex(CallLog.Calls._ID)
                val colTipo = cur.getColumnIndex(CallLog.Calls.TYPE)
                val colTelefono = cur.getColumnIndex(CallLog.Calls.NUMBER)
                // Se pilla la variable
                val colDuration = cur.getColumnIndex(CallLog.Calls.DURATION)

                do {
                    id = cur.getInt(colId)
                    tipo = cur.getInt(colTipo)
                    telefono = cur.getString(colTelefono)
                    duration = cur.getInt(colDuration) // En teoría lo devuelve en segundos

                    when (tipo) {
                        CallLog.Calls.INCOMING_TYPE -> tipoLlamada = "Entrada"
                        CallLog.Calls.OUTGOING_TYPE -> tipoLlamada = "Salida"
                        CallLog.Calls.MISSED_TYPE -> tipoLlamada = "Perdida"
                    }

                    binding.editTextNumero.setText(telefono)
                    binding.editTextDuracion.setText(duration.toString())


                } while (cur.moveToNext())

                cur.close()
            }

        }
    }

    // Función para editar el log de llamadas. Para, por ejemplo, falsear el log.
    // Básicamente añade llamadas al log
    fun escribirFilaEjemplo(view: View) {
        // Objeto que contiene los valores que le vamos a enviar
        val valores = ContentValues()
        // Salvo que el usuario lo cambie, las llamadas van a ser de incoming type
        var entradaSalida: Int = CallLog.Calls.INCOMING_TYPE

        val cr = contentResolver


        // Cojo del sistema la fecha y hora de la llamada
        valores.put(CallLog.Calls.DATE, Date().time)
        valores.put(CallLog.Calls.NUMBER, binding.editTextNumero.text.toString())
        valores.put(CallLog.Calls.DURATION, binding.editTextDuracion.text.toString())


        // Me falta comprobar si es entrada o salida
        if (binding.radioButtonSalida.isChecked) {
            entradaSalida = CallLog.Calls.OUTGOING_TYPE
        }

        // Ajustamos el tipo de llamada a lo que había marcado en el radioButton
        valores.put(CallLog.Calls.TYPE, entradaSalida)

        // La uri va a ser siempre la misma en este caso, lo que va a cambiar es el
        // método para llamarla
        val llamadasUri = CallLog.Calls.CONTENT_URI
        val nuevoElemento = cr.insert(llamadasUri, valores)
    }


    // Si quiero un programita que me vaya eliminando del logs las llamadas
    // para que no me las puedan controlar: le pido que me elimine filas
    // No elimina TODAS las filas; elimina todas las filas con un número concreto
    fun eliminarFilasEjemplo(view: View) {
        // Comprobar permisos. Me lo salto.

        // Borra todas las filas que contengan el número de teléfono que se indique aquí.
        val where = "'"+binding.editTextNumero.text.toString()+"'"
        // También se puede eliminar una llamada que tenga un ID concreto.

        val cr = contentResolver
        val llamadasUri = CallLog.Calls.CONTENT_URI
        val elementosEliminados = cr.delete(llamadasUri, "number=$where", null)
    }


    // Para eliminar una única fila
    fun eliminarFila(view: View) {
        val llamadasUri = CallLog.Calls.CONTENT_URI
        val where = "${CallLog.Calls._ID}=?"
        // Lo que viene en la interrogación se lo pasamos por un array con tantos
        // elementos como interrogaciones haya.
        val selectionArgs = arrayOf(binding.editTextID.text.toString())
        val cr = contentResolver

        // Se eliminan únicamente los argumentos que se hagan seleccionado
        cr.delete(llamadasUri, where, selectionArgs)
    }
}