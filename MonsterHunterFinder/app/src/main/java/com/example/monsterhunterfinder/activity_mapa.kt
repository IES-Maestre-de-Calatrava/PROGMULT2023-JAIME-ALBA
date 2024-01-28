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
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.location.Location
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore

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

    // Variable para alternar el pintado o no de la ruta
    private var ruta=false

    // Variable para alternar entre activar o no la localización del usuario
    private var localizacion=true

    // Variables para la recuperación de la lista de marcadores
    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("marcadores")

    // Variable para volver con datos de la activity que se abre
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


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
        añadirAccionesMapa()


        // Recuperar todos los marcadores almacenados
        recuperarMarcadores()


        // La activity se abre con la localización del usuario activada
        // Una pulsación en el botón la desactiva, y otra la reactiva
        habilitarMiLocalizacion()
        map.invalidate()
        binding.botonMiLocalizacion.setOnClickListener{
            if (localizacion) {
                pararLocalizacion()
                binding.botonRutaLocation.visibility=View.INVISIBLE
                localizacion = false
            } else {
                habilitarMiLocalizacion()
                map.invalidate()
                binding.botonRutaLocation.visibility=View.VISIBLE
                localizacion = true
            }
        }


        // Habilitar o deshabilitar el pintado de la ruta del usuario
        // El listener de localización está en escucha constante; si la
        // variable ruta es true, pinta, y si no, no pinta
        binding.botonRutaLocation.setOnClickListener {
            habilitarPintadoRuta()
            ruta = !ruta
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


        // El activityResultLauncher vuelve con datos de la activity de añadir
        // y crea y añade una entrada para la colección de marcadores, pintando
        // también un icono para el marcador registrado
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                if (result.data != null) {
                    val datos: Intent = result.data!!
                    myCollection
                        .document()
                        .set(
                            hashMapOf(
                                "latitud" to datos.getStringExtra("latitud"),
                                "longitud" to datos.getStringExtra("longitud"),
                                "tipo" to datos.getStringExtra("tipo"),
                                "titulo" to datos.getStringExtra("titulo"),
                                "descripcion" to datos.getStringExtra("descripcion")
                            )
                        )
                        .addOnSuccessListener {
                            map.overlays.add(
                                añadirMarcadorConAccion(
                                    datos.getStringExtra("latitud"),
                                    datos.getStringExtra("longitud"),
                                    datos.getStringExtra("tipo"),
                                    datos.getStringExtra("titulo"),
                                    datos.getStringExtra("descripcion")))
                        }
                }
        }
    }

    /**
     * Función que añade al mapa un marcador interactuable y que ajusta
     * las funciones en caso de interacción; una pulsación corta abre una
     * activity con los datos del marcador, y una larga lo borra
     *
     * @param latitud latitud del marcador
     * @param longitud longitud del marcador
     * @param tipo tipo del marcador; el icono varía según el tipo
     */
    private fun añadirMarcadorConAccion(latitud: String?, longitud: String?, tipo: String?, titulo: String?, descripcion: String?): ItemizedIconOverlay<OverlayItem> {
        return ItemizedIconOverlay(

            // Por si queremos devolver muchos iconos, luego recorrer el arraylist y ya está
            ArrayList<OverlayItem>().apply{
                val marker = Marker(map)
                val lat = latitud!!.toDoubleOrNull()
                val lon = longitud!!.toDoubleOrNull()
                Log.d("Probando", "latitud: $latitud, longitud: $longitud, tipo: $tipo")

                marker.position = GeoPoint(lat!!, lon!!)
                if (tipo!!.equals("punto")) {
                    marker.icon = ContextCompat.getDrawable(map.context, R.drawable.baseline_speaker_notes_24)
                } else if (tipo!!.equals("reunion")) {
                    marker.icon = ContextCompat.getDrawable(map.context, R.drawable.baseline_people_alt_24)
                }

                val overlayItem = OverlayItem("titulo", "descripcion", marker.position)
                overlayItem.setMarker(marker.icon)
                add(overlayItem)
            },

            // Listener para controlar los gestos (pulsaciones cortas o largas)
            object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
                // Una pulsación corta abre una activity con la información del marcador
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    val intent = Intent(map.context, activity_mapa_ver::class.java)
                    intent.putExtra("tipo", tipo)
                    intent.putExtra("titulo", titulo)
                    intent.putExtra("descripcion", descripcion)
                    startActivity(intent)
                    return true
                }

                // Una pulsación larga borra el marcador
                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    borrarMarcador(item)
                    return false
                }

            },
            map.context
        )
    }

    /**
     * Método que borra el marcador interactuable sobre el que
     * se realiza una pulsación larga.
     */
    private fun borrarMarcador(overlayItem: OverlayItem) {
        for (a in map.overlays) {
            if (a is ItemizedIconOverlay<*>) {
                val item = a.getItem(0)
                if (item==overlayItem) {
                    map.overlays.remove(a)
                    map.invalidate()
                }
            }
        }

        myCollection
            .whereEqualTo("latitud", overlayItem.point.latitude.toString())
            .whereEqualTo("longitud", overlayItem.point.longitude.toString())
            .get()
            .addOnSuccessListener {
                res ->
                    for (doc in res) {
                        myCollection.document(doc.id).delete()
                    }
            }
    }

    /**
     * Función que recupera la colección de marcadores existentes en Firebase
     * y los pinta en el mapa
     */
    private fun recuperarMarcadores() {
        myCollection.get()
            .addOnSuccessListener {
                result ->
                    for (document in result) {
                        val latitud = document.getString("latitud")
                        val longitud = document.getString("longitud")
                        val tipo = document.getString("tipo")
                        val titulo = document.getString("titulo")
                        val descripcion = document.getString("descripcion")
                        map.overlays.add(añadirMarcadorConAccion(latitud, longitud, tipo, titulo, descripcion))
                    }
            }
    }

    /**
     * Método que, en función del valor de una variable booleana, habilita
     * el pintado de la ruta que sigue el usuario
     */
    @SuppressLint("MissingPermission")
    fun habilitarPintadoRuta () {

        locManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        locListener = LocationListener{
                location ->
                        if(ruta){
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
            moverAPosicion(posicion_new, 18.0, 1, 29f, false)
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
        map.controller.setZoom(18.0)

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

    /**
     * Método que detiene la escucha de las actualizaciones
     * de la posición del usuario
     */
    private fun pararLocalizacion() {
        mLocationOverlay.disableMyLocation()
    }

    override fun onStop(){
        super.onStop()
        pararLocalizacion()
    }

    // Para controlar la consistencia del mapa
    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume(){
        super.onResume()
        map.onResume()
    }

    /**
     * Método que ejecuta las acciones pertinentes cuando el usuario realiza
     * una pulsación sobre el mapa, haciéndose una distinción en función de si
     * la pulsación ha sido corta (punto de interés) o larga (reunión de juego).
     */
    private fun añadirAccionesMapa() {
        val mapEventsReceiver = object: MapEventsReceiver{
            override fun singleTapConfirmedHelper(loc: GeoPoint?): Boolean {
                if (loc != null) {
                    // Identifico la latitud, la longitud y el tipo de pulsación que ha sido
                    var latitud = loc?.latitude.toString()
                    var longitud = loc?.longitude.toString()
                    var tipo = "punto"

                    // Creo un intent para pasarlo a la siguiente activity
                    val intent = Intent(map.context, activity_mapa_anadir::class.java)
                    intent.putExtra("latitud", latitud)
                    intent.putExtra("longitud", longitud)
                    intent.putExtra("tipo", tipo)

                    // Y lanzo la activity con el intent y su contenido
                    //activityResultLauncher.launch(intent)
                    activityResultLauncher.launch(intent)
                }
                return true
            }

            override fun longPressHelper(loc: GeoPoint?): Boolean {
                if (loc != null) {
                    // Identifico la latitud, la longitud y el tipo de pulsación que ha sido
                    var latitud = loc?.latitude.toString()
                    var longitud = loc?.longitude.toString()
                    var tipo = "reunion"

                    // Creo un intent para pasarlo a la siguiente activity
                    val intent = Intent(map.context, activity_mapa_anadir::class.java)
                    intent.putExtra("latitud", latitud)
                    intent.putExtra("longitud", longitud)
                    intent.putExtra("tipo", tipo)

                    // Y lanzo la activity con el intent y su contenido
                    //activityResultLauncher.launch(intent)
                    activityResultLauncher.launch(intent)
                }
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(0, mapEventsOverlay)
        map.invalidate()
    }
}