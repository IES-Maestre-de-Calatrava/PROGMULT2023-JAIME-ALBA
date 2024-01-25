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
    private var tipo=true

    // Variable para alternar entre mi localización y desactivar mi localización+pintar ruta
    // Si es false: darle al botón desactiva mi localización y activa la ruta
    // Si es true: darle al botón desactiva la ruta y activa mi localización
    private var ruta=false

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


        // La activity se abre con la localización del usuario activada
        // Una pulsación la desactiva y comienza un seguimiento de ruta mediante línea
        // Una nueva pulsación activa el estado anterior
        habilitarMiLocalizacion()
        map.invalidate()
        binding.botonRutaLocation.setOnClickListener {
            if (!ruta) {
                habilitarPintadoRuta()

                ruta=true
            } else {
                ruta=false
            }
        }


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

    /**
     * Método que, en función del valor de una variable booleana, habilita
     * el pintado de la ruta que sigue el usuario
     */
    @SuppressLint("MissingPermission")
    fun habilitarPintadoRuta () {

        locManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val loc: Location? = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        locListener = LocationListener{
                location ->
                if (!ruta) {
                         pintarRutaLinea(location)
                }
        }

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0f, locListener)
    }

    /**
     * Método que realiza el pintado de la ruta seguida por el usuario.
     * Se le dice que sólo pinte un marcador (en lugar de uno cada vez que se actualice la
     * posición) y que vaya pintando una línea con el recorrido que voy haciendo yo.
     */
    private fun pintarRutaLinea(loc: Location) {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        marker = Marker(map)

        if (loc != null) {
            if (!::posicion_new.isInitialized) {
                posicion_new = GeoPoint(loc.latitude, loc.longitude)
                añadirMarcador(posicion_new)
                // Como es el primer punto y no uno antiguo, simplemente ponemos el marcador en pantalla
            } else {
                // Cuando ya haya más de un marcador
                posicion_old = posicion_new
                posicion_new = GeoPoint(loc.latitude, loc.longitude)
                geoPoints.add(posicion_old)
                geoPoints.add(posicion_new)
                moverMarcador(posicion_new)

            }
            // Se pinta la línea, se mueve el marcador al final de ella y se reencuadra el mapa
            pintarLinea(geoPoints)
            moverAPosicion(posicion_new, 17.0, 1, 29f, false)
        }
    }

    /**
     * Función que elimina el marcador previo y añade uno nuevo,
     * dando la impresión de que lo desplaza
     * @param posicion_new: geopoint en cuyas coordenadas se pintará el nuevo marcador
     */
    private fun moverMarcador(posicion_new: GeoPoint) {
        // Cuando creo una capa, un overlay, puedo obtener su id y trabajar con él
        // Primero voy a borrar todas las capas de tipo marker
        val t = LinkedList(map.overlays)

        // recorremos todas las capas
        for (o in t) {
            if (o is Marker) {
                map.overlays.remove(o)
            }
        }

        marker.setPosition(posicion_new)

        // Añado el marcador y hago refresh
        map.getOverlays().add(marker)
        map.invalidate()
    }

    /**
     * Método que reencuadra la vista del mapa al punto que
     * se le pase por parámetro
     */
    private fun moverAPosicion(latlngP: GeoPoint, zoomP: Double, speedP: Long, orientacionP: Float, tiltP: Boolean) {
        map.controller.animateTo(latlngP, zoomP, speedP, orientacionP, tiltP)
    }

    /**
     * Método que añade un marcador al mapa (usado en seguimiento de ruta)
     *
     * @param posicion_new
     */
    private fun añadirMarcador(posicion_new: GeoPoint) {
        var marker = Marker(map)
        marker.position = posicion_new
        marker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass)

        // Para indicarle a dónde quiero que se ancle: centros horizontal y vertical
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        map.invalidate() // Creo la capa, la pongo y la invalido para hacer el refresh
    }

    /**
     * Pintar lina
     * El método que dibuja tiene que recibir un array con todos los puntos por
     * los que pasará la línea de la ruta
     *
     * param puntos por los que tiene que pasar la línea
     */
    private fun pintarLinea(geoPoints: ArrayList<GeoPoint>) {
        val line = Polyline()
        line.setPoints(geoPoints)

        map.overlayManager.add(line)
    }

    /**
     * Método que desactiva la localización del usuario
     */
    private fun pararLocalizacion() {
        locManager.removeUpdates(locListener)
        mLocationOverlay.disableMyLocation()
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
        if (tipo) {
            // Una activación cambia a mapa topográfico
            map.setTileSource(TileSourceFactory.OpenTopo)

            tipo=false
        } else {
            // Otra más lo devuelve al mapa por defecto
            map.setTileSource(TileSourceFactory.MAPNIK)

            tipo=true
        }

        map.invalidate()
    }
}