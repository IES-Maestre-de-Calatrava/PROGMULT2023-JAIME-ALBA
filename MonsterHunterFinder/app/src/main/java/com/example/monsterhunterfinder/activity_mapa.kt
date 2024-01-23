package com.example.monsterhunterfinder

import android.Manifest
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monsterhunterfinder.databinding.ActivityMapBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.content.Context
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.location.Location
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import java.util.LinkedList

/**
 * Activity dedicada al mapa, en la que el usuario puede gestionar
 * reuniones con otra gente, cazas
 */
class activity_mapa : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    // Variable para permisos en tiempo de ejecución
    private val PETICION_PERMISOS_OSM = 0

    // Variable para el map
    private lateinit var map: MapView

    // Variable para mi localización actual
    private lateinit var mLocationOverlay: MyLocationNewOverlay

    private lateinit var locListener: LocationListener
    private lateinit var locManager: LocationManager

    // Variables para posiciones nueva y vieja, para dibujar las líneas
    // y los marcadores de la ruta
    private lateinit var posicion_new: GeoPoint
    private lateinit var posicion_old: GeoPoint
    private lateinit var marker: Marker

    // Variables para el manejo de la brújula
    private var bruj=1
    private lateinit var mCompassOverlay: CompassOverlay

    // Variable para el control del tipo de mapa
    private var tipo=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Para poder acceder a la ubicación del usuario es necesario dar permisos
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


        // Cargar el mapa
        map = binding.mapa
        generarMapa()
        quitarRepeticionYLimitarScroll()

        // Centro el mapa en la localización del usuario
        habilitarMiLocalizacion()
        map.invalidate()

        // Control de la brújula
        mCompassOverlay = CompassOverlay(this, InternalCompassOrientationProvider(this), map)
        binding.botonBrujula.setOnClickListener {
            manejoBrujula()
        }

        // Listener para el cambio de mapa
        binding.botonTipo.setOnClickListener {
            cambiaMapa()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Me llega un array con todos los permisos, y en el grantResults, lo que ha dado el usuario
        // Si he pedido cuatro permisos, en el grantResults me llegan cuatro resultados
        // Sólo me vienen los que el usuario concede.

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
    }

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Método que genera el mapa, centrándolo en la ubicación del
     * usuario, añadiendo controles de zoom y habilitando el multitouch
     */
    private fun generarMapa() {
        // A la instancia del programa le cargo las preferencias
        getInstance().load(this, getSharedPreferences(packageName+"osmdroid", Context.MODE_PRIVATE))
        map.minZoomLevel = 4.0
        map.controller.setZoom(20.0)

        // Para que salgan fijos controles de zoom
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)

        // Reacción del mapa al multitouch control gestual)
        map.setMultiTouchControls(true)
    }

    /**
     * Función que ubica el mapa en la localización del usuario
     */
    private fun habilitarMiLocalizacion() {
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)

        // Habilito tanto ubicación como seguimiento
        mLocationOverlay.enableMyLocation()
        mLocationOverlay.enableFollowLocation()

        // Y se pone esta capa por encima de las existentes
        map.getOverlays().add(mLocationOverlay)
    }

    /**
     * Quitar la repetición del mapa y limitar
     * el scroll que se puede hacer en él
     */
    private fun quitarRepeticionYLimitarScroll() {
        // Quitar la repetición
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

    /**
     * Método que activa y desactiva la brújula.
     *
     * @param view: elemento cuya activación acciona el método
     */
    private fun manejoBrujula() {
        if (bruj==1) {
            // Si se activa el botón de brújula: crear una capa de brújula y metérsela al mapa
            mCompassOverlay.enableCompass()
            map.getOverlays().add(mCompassOverlay)
            bruj--
        } else if (bruj==0) {
            // Si se vuelve a activar: la brújula se deshabilita
            mCompassOverlay.disableCompass()
            map.getOverlays().remove(mCompassOverlay)
            bruj++
        }

        map.invalidate()
    }

    /**
     * Método que cambia el tipo de mapa establecido, alternando entre
     * el por defecto y el de topografía
     */
    private fun cambiaMapa() {
        if (tipo==1) {
            // La primera activación cambia a mapa topográfico
            map.setTileSource(TileSourceFactory.USGS_TOPO)

            tipo++
        } else if (tipo==2) {
            // La segunda, a mapa de ciclismo
            map.setTileSource(TileSourceFactory.HIKEBIKEMAP)

            tipo++
        } else {
            // Y la tercera lo devuelve al mapa por defecto
            map.setTileSource(TileSourceFactory.MAPNIK)

            tipo--
            tipo--

        }

        map.invalidate()
    }
}