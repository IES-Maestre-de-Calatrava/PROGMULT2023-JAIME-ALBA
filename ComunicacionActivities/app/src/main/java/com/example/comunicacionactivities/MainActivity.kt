package com.example.comunicacionactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.example.comunicacionactivities.databinding.ActivityMainBinding
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult


/**
 * Demostración de apertura de activities desde otra activity
 *
 * 1) Apertura normal
 *
 * @author Jaime Alba Ruiz
 */

class MainActivity : AppCompatActivity() {


    // Variable para el apartado 3
    lateinit var activityActividadCaso3ResultLauncher: ActivityResultLauncher<Intent>


    /**
     * APARTADO 3: apertura con devolución de datos
     * @param[view] Vista que llama al método
     * Apertura; la activity no hace nada, y la abierta nos devuelve datos
     */
    fun openSomeActivityForResult(view: View){
        val intent = Intent(this, activity_actividad_caso_3::class.java)
        activityActividadCaso3ResultLauncher.launch(intent)
    }




    /**
     *  Binding
     */
    // Código suyo, yo no entiendo nada
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearObjetosDelXml()

        // En el onCreate es donde tengo que recoger
        // los datos que me devuelva la otra activity
        activityActividadCaso3ResultLauncher = registerForActivityResult(StartActivityForResult()) {
            // El StartActivityForResult es lo que se ejecuta cuando hago el launch
            // Es lo que se registra cuando ejecuto la llamada
            // Aquí mismo lo puedo procesar
            result ->
                // Si los datos de verdad devuelven algo
                if (result.data != null) {
                    val data: Intent = result.data !!
                    val nombreDevuelto = data.getStringExtra("Mensaje")
                    // pongo para que se muestre el texto devuelto en el
                    // bloque de texto que corresponde en la MAIN ACTIVITY

                    // TIENE QUE SER UN TEXT VIEW
                    // NO UN TEXTO EDITABLE
                    // ES UN TEXTO QUE SE VE PERO NO SE TOCA
                    // SUBNORMAL
                    binding.textView7.text = nombreDevuelto
                    // falta hacer algo en la activity 3 para que devuelva datos
                }
        }
    }

    /**
     * Crear objetos partiendo del XML.
     */
    private fun crearObjetosDelXml() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * El primer método va de darle al botón llamar y que me
     * redireccione a otra activity.
     *
     * TODOS los métodos a los que se llama desde un botón tienen que
     * cumplir dos cosas: NO pueden ser privados y TIENEN que recibir
     * como parámetro de entrada una view (cualquier objeto en pantalla).
     *
     * En este caso, el parámetro de entrada va a ser el botón.
     */

    /**
     * APARTADO 1
     * Apertura normal: abre una activity concreta
     * @param[view] Objeto View que llama al método
     */
    fun openSomeActivity(view: View) {
        /**
         * Declaro una intención de hacer algo y se lo
         * digo al sistema.
         * Indico la clase Java que voy a abrir (arriba a la
         * izquierda, en Android, puedo cambiar a vista de proyecto
         * y ahí buscar el nombre exacto de la clase: app > src > main > java)
         */
        val intent = Intent(this, activity_actividad_caso_1::class.java)
        /**
         * Le digo que inicie el intento
         */
        startActivity(intent)

        /**
         * Dos formas de llamar a este método desde botones:
         * poner un listener O cambiar una propiedad de los botones,
         * que es onClick
         */

        /**
         * Me abro el xml del activity_main, le doy al botón
         * y miro en sus atributos, añado el atributo onClick
         * y busco el método openSomeActivity, éste de aquí
         */
    }


    /**
     * APARTADO 2
     * Me creo el método al que va a llamar el segundo botón; el método
     * coge el texto y se lo pasa a la siguiente activity, le envía
     * datos.
     *
     * @param[view] Objeto que llama al método
     */

    fun openSomeActivitySendingData(view: View) {
        val intent = Intent(this, activity_actividad_caso_2::class.java)

        /**
         * A la propia intnción le puedo dar datos. En la "cajita"
         * de cosas que llevo la intención meto esa cosa que le
         * quiero pasar.
         *
         * Puedo declarar varios. Le doy nombre a la variable usada
         * y cojo el texto de la variable binding (a lo que la asigno).
         * Tengo que saber cómo se llama mi EditText; lo busco en el
         * XML de la main activity y le pido el texto.
         */
        intent.putExtra("DatosEnviados", binding.editTextText.text.toString())
        intent.putExtra("Minombre", "Javier")

        startActivity(intent)
    }



}