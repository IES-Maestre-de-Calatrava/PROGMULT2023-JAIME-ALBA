package com.example.osmdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.osmdroid.databinding.ActivityMainBinding
import org.osmdroid.views.MapView

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Context

import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Hay que pedir permisos en tiempo de ejecución
    private val PETICION_PERMISOS_OSM = 0

    // Variable para el map
    private lateinit var map: MapView

    // Variable para la localización actual
    private lateinit var mLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Proceso de comprobar permisos. Importar el manifest.
        // Pedimos cuatro permisos.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)== PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)== PackageManager.PERMISSION_DENIED) {

            // Si alguno de los cuatro permisos está denegado, pedirlo.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
                ),
                PETICION_PERMISOS_OSM // Constante para indicar los permisos que estoy pidiendo
            )
        }

        accionesParaBotones()

        // Cargar el mapa
        map = binding.map
        generarMapa()
        quitarRepeticionYLimitarScroll()
    }

    // File > Settings > buscar Plugins > en Marketplace buscar kdoc > Kdoc-er
    /**
     * On request permissions result
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
        // Depurar y ver qué llega en el grantResults y en el permissions.
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Me llega un array con todos los permisos, y en el grantResults, lo que ha dado el usuario
        // Si he pedido cuatro permisos, en el grantResults me llegan cuatro resultados
        // Sólo me vienen los que el usuario concede. Si pido cuatro y me llegan tres, problemilla.

        // Independientemente de los permisos que me lleguen, me va a valer.
        // Me declaro un arrayList
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }

        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PETICION_PERMISOS_OSM
            )
        }

        // Tarea mía: conseguir que, según los permisos que haya dado, se deshabiliten botones
    }

    private fun accionesParaBotones() {
        binding.buttonOff.setOnClickListener {

        }

        binding.buttonOn.setOnClickListener {

        }
    }

    /**
     * Quitar repeticion y limitar scroll
     * Para que no repita el mapa 800 veces
     *
     */
    private fun quitarRepeticionYLimitarScroll() {
        map.isHorizontalMapRepetitionEnabled = false
        map.isVerticalMapRepetitionEnabled = false

        // Limitar el scroll
        map.setScrollableAreaLimitLatitude(
            MapView.getTileSystem().maxLatitude,
            MapView.getTileSystem().minLatitude,
            0
        )

        map.setScrollableAreaLimitLongitude(
            MapView.getTileSystem().minLongitude,
            MapView.getTileSystem().maxLongitude,
            0
        )
    }

    private fun generarMapa() {
        // A la instancia del programa le cargo las preferencias (lo necesita, o se ve pixelado)
        getInstance().load(this, getSharedPreferences(packageName+"osmdroid", Context.MODE_PRIVATE))

        // Queda feo quitarle todo el zoom; puedo especificar cuál es el zoom mínimo que se puede mover
        map.minZoomLevel = 4.0

        // Y que cuando entre se ponga en un zoom determinado
        map.controller.setZoom(12.0)

        // Le indico que el centro del mapa me lo ubique en un determinado punto (para no spawnear en mitad del mar)
        var startPoint = GeoPoint(35.6764, 139.6500)
        map.controller.setCenter(startPoint)
        // Si le doy a los tres puntitos, a location y busco un lugar, puedo buscar sus coordenadas
        // Puedo darle a SAVE POINT para guardar esa ubicación

        // Para que los botones de zoom salgan fijos
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)

        // SI QUIERO HACER MULTITOUCH, DEJO PRESIONADO EL CONTROL
        // Para que el mapa reaccione al multitouch:
        map.setMultiTouchControls(true)

        // Cambiar el tipo de mapa (a mí me toca mirar en la documentación los distintos mapas que se pueden cargar)
        //map.setTileSource(TileSourceFactory.OPEN_SEAMAP) // El por defecto es MAPNIK

        // Para meter la brújula: crear una capa que es la brújula y metérsela al mapa
        var mCompassOverlay =
            CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        mCompassOverlay.enableCompass()
        map.getOverlays().add(mCompassOverlay)
    }

    private fun habilitarMiLocalizacion() {
        // Normalmente se le deja una posición por defecto para que el mapa vaya a un lado en caso
        // de que no haya habilitados internet o GPS, suele hacerse con la ubicación actual
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
    }

    private fun crearObjetosDelXml() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // En primer lugar, permisos. Nos vamos al Manifest. Las dos líneas de usos.
    // Preguntar a la IA la diferencia entre los permisos de FINE_LOCATION y COARSE_LOCATION.
    // FINE es más precisa, usa GPS, pero consume más recursos (batería).
    // COARSE averigua la localización según conexión a puntos de telefonía, es menos precisa, pero consume menos recursos.

    // Con FINE voy leyendo cosas del GPS, pero puedo decirle cada cuánto tiempo quiero que me lea la posición.
    // Por ejemplo, si voy en bici (no avanzo demasiado deprisa) y no me interesa estar actualizando la posición constantemente.

    // Necesitamos más permisos. Uno para acceso a internet.
    // Y un permiso más, para ver el estado de la red.
    // Pillar el XML de la carpeta compartida. Da un error porque hay que meter algo en las dependencias.

    // Poner en Google OsmDroid. Sale su GitHub y aparece la Wiki. Puedo pegar código Java, al pegarlo lo convierte a Kotlin.
    // OsmDroid se va a usar sólo para cargar mapas, para obtener posiciones vamos a usar librerías de Android.
    // Vamos a usar la versión 6.1.10
    // Nos vamos al Gradle e importamos las librerías. Son las dos líneas que hay por separado.

    // Si le damos play, salta error. Hay que meter un atributo en el manifest (la línea de tools que hay por separado)

    // No es viable salir a la calle con el emulador para probar el mapa, así que usamos lo del emulador.
    // Para poder ver el mapa: sobre el emulador hay tres puntitos en el menú, darle click
    // Si aparece vacío, sin mapa: tools > sdkmanager, ir marcando todo lo que tenga una update disponible para que lo descargue y lo instale
    // Y luego en el sdktools
}