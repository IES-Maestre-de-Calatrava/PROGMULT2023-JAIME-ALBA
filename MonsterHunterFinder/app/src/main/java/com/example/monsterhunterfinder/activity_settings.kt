package com.example.monsterhunterfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Activity dedicada a la configuración de las preferencias del usuario.
 * Aquí se gestiona la interfaz de usuario relativa y utiliza un
 * SettingsFragment para mostrar al usuario las opciones de preferencia
 * que hay disponibles.
 * @author Jaime
 */
class activity_settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Únicamente en el caso de que la activity no tenga una savedInstanceState
        // (no tenga un estado previo guardado), se reemplaza el contenido de la activity
        // con el fragment de preferencias
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }
}