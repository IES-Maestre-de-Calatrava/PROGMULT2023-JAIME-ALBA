package com.example.preferencias

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SettingsActivity : AppCompatActivity() {

    // Aquí no hace falta construir lo del binding.
    // Me copio el XML del layout, cogido de GitHub.

    // El layout contiene una view que es un FrameLayout. Es un marco en el que puedo cargar un
    // fragmento. Si cargo la activity en por ejemplo una tablet, tendría que recolocar todos
    // los botones. En lugar de eso, puedo usar FrameLayouts que luego se recolocan en conjunto,
    // se colocan en bloque en lugar de uno a uno, y se quedan igual. El problema es que trabajar
    // con fragmentos, que es como tener una activity dentro de otra, hace que cambie la mecánica
    // para, por ejemplo, comunicar unos fragmentos con otros.
    // Algo bueno que tienen los Fragments es que puedo pedirle que me cambie todo el contenido,
    // en bloque, e ir cargando distintos menús en el mismo fragment.

    // Nosotros vamos a tener un fragmento específico para las preferencias.
    // Tendré un XML y una clase que cargue en el FrameLayout el contenido del XML.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            // savedInstanceState es el estado de la aplicación Android en un momento concreto.
            // Si es igual a null, significa que es la primera vez que se entra ahí; no ha
            // habido cambios guardados anteriormente.
            supportFragmentManager
                .beginTransaction() // "Vamos a empezar a hacer algo"
                .replace(R.id.settings,SettingsFragment()) // Ponerle el layout que quiero cargar, EL NOMBRE DEL FRAMELAYOUT
                .commit()                                  // le doy la clase encargada de inflar el layout, y commit para que lo haga

            // Ahora me creo una Kotlin Class que se llame SettingsFragment

        }
    }
}