package com.example.monsterhunterfinder

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/**
 * Fragment dedicado a la configuración de las preferencias
 * del usuario. Las muestra y gestiona haciendo el inflado
 * del layout que se le indique.
 * @author Jaime
 */
class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Se establecen las preferencias desde el layout xml que se
        // le indique y se muestra la interfaz de usuario
        setPreferencesFromResource(R.xml.preferencias_usuario, rootKey)
    }
}