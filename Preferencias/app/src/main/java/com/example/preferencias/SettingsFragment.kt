package com.example.preferencias

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

// Esta clase, fragmento, hereda de PreferenceFragmentCompat, para que extienda de la
// clase dedicada a fragmentos de preferencias
// Al heredar me tiene que decir algo del Gradle, si no pasa copiar la primera línea


// Hacer los imports que pida al poner el ratón en SettingsFragment
class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Va a inflar el XML de preferencias y guardar y cargar automáticamente
        // todas las preferencias que haya insertado el usuario
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Nos falta el XML con las preferencias que el usuario vaya a poder elegir,
        // el root_preferences
        // Se puede poner en modo check, en modo lista, etc, se puede investigar
        // Ahora a construir el XML. Va en la carpeta de XML, no en layouts.
        // Click derch > new XML resource file
    }
}