package com.example.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificaciones.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // PASO CERO hacer lo del binding: ésto y el crearObjetosDelXml
    private lateinit var binding: ActivityMainBinding

    // PRIMER PASO creamos una variable: identificador del canal
    // que vamos a utilizar para las notificaciones
    private val CHANNEL_ID: String = "123"

    private var PETICION_PERMISOS: Int = 456 // ID para los permisos que se pidan


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // SEGUNDO PASO
        // nada más ejecutarse el onCreate, tiene que crearnos
        // ese canal de notificaciones que nos hemos declarado
        // en la variable un poco más arriba
        createNotificationChannel()
    }


    private fun crearObjetosDelXml() {
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    // TERCER PASO
    // Creamos la función que genera el canal de
    // notificaciones que vamos a utilizar
    private fun createNotificationChannel() {
        // Comprobamos la versión del SDK, porque
        // hasta cierta API no existían los canales
        // de notificaciones

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Al canal tenemos que darle un nombre, un texto descriptivo,
            // y un grado de importancia para las notificacionees
            val name = "Mi aplicación"
            val descriptionText = "Canal para mis notificaciones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // CUARTO PASO: DECLARACIÓN DEL CANAL
            // Creamos un canal pasando esta información que acabamos de describir
            // Le pasamos al channel el channelId, el name
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply{
                description = descriptionText
            }


            // QUINTO PASO
            // Registrar el canal en el sistema: me declaro un
            // notificationManager y lo creo pasándole como NotificationChannel
            // el que me he creado anteriormente
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


    }


    // SEXTO PASO
    // Crear método que permita lanzar las notificaciones.
    // Se lo va a llamar clickando el botón: no puede ser privado, y
    // por parámetro tiene que recibir una View, que será el propio botón
    fun lanzarNotificaciones(view: View) {

        // Queremos que se haga algo cuando se pulse en la notificación; creamos
        // un intent, una intención

        // PendingIntent permite lanzar algo al sistema a alguien, al margen de que
        // la aplicación que ha hecho el lanzamiento ya no esté en ejecución; se
        // queda ahí aunque hayas cerrado la aplicación
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            // FLAG_IMMUTABLE hace que el contenido de PendingIntent no pueda ser modificado
            // Puede haber casos en que lance un PendingIntent varias veces y se pueda
            // modificar; no es este caso.
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        // Ahora hay que decirle a la app que ejecute ese PendingIntent cuando
        // pulsemos en la notificación: línea de .setContentIntent(contentIntent)
        // Con ello hago que, al pulsar en la notificación desde fuera de la app,
        // me devuelva a ella





        // Para lanzar la notificación, es necesario crear un builder de
        // notificaciones asociado a nuestro canal
        // this: el contexto en el cual estamos
        // CHANNEL_ID: el canal creado anteriormente

        when (view) {

            binding.button1 -> {
                var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    // Ésto ya espara toquetear propiedades
                    // Icono de advertencia:
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    // Un título:
                    .setContentTitle("Aviso")
                    // Se recomienda mirar en el API todo lo que se puede hacer con las notificaciones
                    // Un texto:
                    .setContentText("Ésto es un aviso de notificación")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(contentIntent)

                // Mostrar la notificación
                // Pasamos un ID. No el del canal, sino el de la notificación; cada
                // notificación tiene que tener un ID diferente
                with(NotificationManagerCompat.from(this)){
                    notify(1111, notificationBuilder.build())
                }
            }

            binding.button2 -> {
                var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    // Ésto ya espara toquetear propiedades
                    // Icono de advertencia:
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    // Un título:
                    .setContentTitle("Aviso")
                    // Se recomienda mirar en el API todo lo que se puede hacer con las notificaciones
                    // Un texto:
                    .setContentText("FUNCIONA CHAVALES")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(contentIntent)

                // Mostrar la notificación
                // Pasamos un ID. No el del canal, sino el de la notificación; cada
                // notificación tiene que tener un ID diferente
                with(NotificationManagerCompat.from(this)){
                    notify(2222, notificationBuilder.build())
                }
            }
        }

        // PRIMERO HAY QUE PEDIR PERMISOS Y LUEGO LANZAR LA NOTI
        // EN ESTE CÓDIGO ESTÁ AL REVÉS


        // TODO ESTE BLOQUE LO VAMOS A METER DENTRO DE UN IF QUE COMPRUEBE SI EL
        // USUARIO YA DENEGÓ PERMISOS, PARA QUE NO VUELVA A PREGUNTAR MÁS Y EN
        // RESUMEN NO SEA UN PESAO DE LA MIERDA


        // Proceso de los permisos: verifica si ya se le preguntó al usuario por
        // los permisos, los denegó y decidió que no se le volviese a preguntar
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(this, "Tienes que darme permisos en ajustes", Toast.LENGTH_SHORT).show()
        } else {
            // Verificamos si se ha concedido el permiso

            // Hago un check. Si no está concedido el permiso, en
            // ese momento le pido permiso al usuario (estas líneas
            // se han puesto auto al hacer alt+shift+intro)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Se solicitan permisos al usuario en caso de que no los haya dado
                // Solicitamos los permisos
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ), PETICION_PERMISOS)
            }
        }




    }
}