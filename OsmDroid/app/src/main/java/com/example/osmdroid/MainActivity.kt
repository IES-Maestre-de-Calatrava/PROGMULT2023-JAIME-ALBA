package com.example.osmdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.osmdroid.databinding.ActivityMainBinding
import org.osmdroid.views.MapView

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.LinkedList

class MainActivity : AppCompatActivity() {
    // EMPEZAR COPIANDO EN EL GRADLE LAS DOS LÍNEAS DE DEPENDENCIAS DE OSMDROID
    // TRAS ESO, LOS PERMISOS QUE HAY EN EL MANIFEST


    private lateinit var binding: ActivityMainBinding

    // Hay que pedir permisos en tiempo de ejecución
    private val PETICION_PERMISOS_OSM = 0

    // Variable para el map
    private lateinit var map: MapView

    // Variable para la localización actual (la mía)
    private lateinit var mLocationOverlay: MyLocationNewOverlay

    private lateinit var locListener: LocationListener
    private lateinit var locManager: LocationManager

    // Variables para posicionesnueva y vieja, para dibujar las líneas
    // y los marcadores de la ruta
    private lateinit var posicion_new: GeoPoint
    private lateinit var posicion_old: GeoPoint
    private lateinit var marker: Marker

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
        binding.buttonOn.isEnabled = true


        // Cargar el mapa
        map = binding.map
        generarMapa()
        añadirAccionesMapa()
        quitarRepeticionYLimitarScroll()

        //habilitarMiLocalizacion()
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
            pararLocalizacion()
        }

        binding.buttonOn.setOnClickListener {
            // Ésto va a habilitar la escucha del GPS. Puedo hacerle sugerencias de funcionamiento:
            // cada 3 segundos recibir posiciones, cada 6... Yo me suscribo a un servicio de
            // recepción de coordenadas, y éstas llegas cuando llegan.

            // Ahora vamos a deshabilitar el seguimiento de ruta y le vamos a pedir que, moviéndonos
            // nosotros, nos vaya pintando la ruta.
            habilitarLocalizacion()
        }
    }

    @SuppressLint("MissingPermission") // Esta línea indica que aseguramos que están los permisos
    fun habilitarLocalizacion () {
        // Habría que volver a comprobar permisos (recomendado)
        binding.buttonOff.isEnabled = true
        binding.buttonOn.isEnabled = false

        // Vale tanto para OsmDroid como para Google Maps
        locManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val loc: Location? = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        locListener = LocationListener{
            location -> pintarRutaLinea(location)
        }

        // Pedir actualizaciones de localización, que me avise cuando haya cambios en esas posiciones
        // Le pido que sea cada 3000 milisegundos
        // Si pongo 1f: es que intente mandarme las posiciones cada metro que avance
        // Y le indico el listener que voy a utilizar para enterarme de cuando lleguen coordenadas nuevas
        // Cada vez que haya una update de localización voy a pintar un icono y una línea uniéndolo con el icono anterior
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0f, locListener)
    }

    /**
     * Pintar ruta
     *
     * @param loc
     */
    private fun pintarRuta(loc: Location) {
        if (loc != null) {
            // El !:: se usa para ver si una variable lateinit se ha inicializado
            if (!::posicion_new.isInitialized) {
                // El mapa trabaja con coordenadas GeoPoint
                posicion_new = GeoPoint(loc.latitude, loc.longitude)
            } else {
                // Si ha sido inicializada, ya tiene dentro unas coordenadas: las paso a una variable
                // para guardarlas y reasigno a las coordenadas nuevas
                posicion_old = posicion_new
                posicion_new = GeoPoint(loc.latitude, loc.longitude)
            }
            // Creo un marcador, y cuando pulse en él, me pone latitud y longitud de ese punto
            var contenido = loc.latitude.toString() + " " + loc.longitude.toString()
            // Se hace distinto a Google Maps
            añadirMarcador(posicion_new, "Punto", contenido)
            // Y un método que me desplaza manualmente hacia la posición
            moverAPosicion(posicion_new, 15.5, 1, 29f, false)
        }
    }

    /**
     * Decirle que sólo pinte un marcador (en lugar de uno cada vez que se actualice la
     * posición) y que vaya pintando una línea con el recorrido que voy haciendo yo
     */
    private fun pintarRutaLinea(loc: Location) {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        var contenido: String
        marker = Marker(map)


        if (loc != null) {
            if (!::posicion_new.isInitialized) {
                posicion_new = GeoPoint(loc.latitude, loc.longitude)
                contenido = loc.latitude.toString() + " " + loc.longitude.toString()
                añadirMarcador(posicion_new, "Punto", contenido)
                // Como es el primer punto y no uno antiguo, simplemente ponemos el marcador en pantalla
            } else {
                // Cuando ya haya más de un marcador
                posicion_old = posicion_new
                posicion_new = GeoPoint(loc.latitude, loc.longitude)
                geoPoints.add(posicion_old)
                geoPoints.add(posicion_new)
                moverMarcador(posicion_new)

            }
            //var contenido = loc.latitude.toString() + " " + loc.longitude.toString()
            //añadirMarcador(posicion_new, "Punto", contenido)
            pintarLinea(geoPoints)
            moverAPosicion(posicion_new, 15.5, 1, 29f, false)
        }
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
        map.getOverlays().add(marker)
        map.invalidate()

        // da lo mismo .overlays que .getOverlays()
    }


    /**
     * Añadir marcador
     *
     * @param posicion_new
     * @param tituloP
     * @param contenidoP
     */
    private fun añadirMarcador(posicion_new: GeoPoint, tituloP: String, contenidoP: String) {
        var marker = Marker(map)
        marker.position = posicion_new
        marker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass)
        marker.title = tituloP
        marker.snippet = contenidoP

        // Para indicarle a dónde quiero que se ancle: centros horizontal y vertical
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        map.invalidate() // Creo la capa, la pongo y la invalido
    }

    private fun moverAPosicion(latlngP: GeoPoint, zoomP: Double, speedP: Long, orientacionP: Float, tiltP: Boolean) {
        map.controller.animateTo(latlngP, zoomP, speedP, orientacionP, tiltP)
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

        // Para habilitar tanto la ubicación como el seguimiento
        mLocationOverlay.enableMyLocation()
        mLocationOverlay.enableFollowLocation()

        // para poner esta capa por encima de las que ya hay:
        map.getOverlays().add(mLocationOverlay)

        // Por ahora no hace nada porque no hay GPS; hay que simular que lo activamos
        // Me voy a los tres puntitos, a location, busco una localización y set location


        // También desde ahí puedo buscar rutas; un punto de inicio, un punto desde el que llegar
        // y que salga en movimiento
    }

    private fun pararLocalizacion() {
        locManager.removeUpdates(locListener)
        binding.buttonOff.isEnabled = false
        binding.buttonOn.isEnabled = true

        //mLocationOverlay.disableMyLocation()
    }

    override fun onStop(){
        super.onStop()
        pararLocalizacion()
    }

    // Estos dos son para controlar la consistencia del mapa
    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume(){
        super.onResume()
        map.onResume()
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



    // 15/01/2023
    // Lo siguiente es controlar las pulsaciones en pantalla
    // Hay de dos tipos: en el mapa y en marcadores
    private fun añadirAccionesMapa() {
        val mapEventsReceiver = object: MapEventsReceiver{
            // Sobreescribir dos métodos: para pulsación corta y para larga
            override fun singleTapConfirmedHelper(loc: GeoPoint?): Boolean {
                if (loc != null) {
                    // Al hacer pulsación corta, pone un marcador con el contenido que queramos, esta
                    // parte es manipulable
                    // PODEMOS SACAR UN CUADRO DE DIÁLOGO, PEDIR UN NOMBRE AL USUARIO Y QUE ÉL LO PONGA
                    // O ABRIR OTRA ACTIVITY
                    var contenido = loc?.latitude.toString() + " " + loc?.longitude.toString()
                    añadirMarcador(loc, "Punto con click", contenido)
                }
                return true
            }

            override fun longPressHelper(loc: GeoPoint?): Boolean {
                if (loc != null) {
                    // Le voy a decir que, en lugar de crear el marcador, me muestre un cuadro de diálogo con la información
                    mostrarDialogo("Coordenadas: ", loc)


                    // Con el long click, va a crear un tipo diferente de marcador; uno en el que, al pulsar, haga algo
                    var contenido = loc?.latitude.toString() + " " + loc?.longitude.toString()
                    map.overlays.add(añadirMarcadorConAccion(loc, "Punto", contenido))
                }
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        // Le vamos a poner una cosa que no hemos hecho en los otros: el índice en el que queremos que esté la capa
        // En este caso, índice 0
        map.overlays.add(0, mapEventsOverlay)
        map.invalidate()
    }

    /**
     * Añadir marcador con accion
     * Va a devolver una capa con un Itemized, un icono que tiene acciones
     *
     * @param posicion_new
     * @param tituloP
     * @param contenidoP
     * @return
     */
    private fun añadirMarcadorConAccion(posicion_new: GeoPoint, tituloP: String, contenidoP: String): ItemizedIconOverlay<OverlayItem> {
        return ItemizedIconOverlay(
            // Por si queremos devolver muchos iconos, luego recorrer el arraylist y ya está
            ArrayList<OverlayItem>().apply{
                val marker = Marker(map)
                marker.position = posicion_new
                marker.title = tituloP
                marker.snippet = contenidoP
                marker.icon = ContextCompat.getDrawable(map.context, android.R.drawable.ic_menu_camera)

                val overlayItem = OverlayItem(tituloP, contenidoP, posicion_new)
                overlayItem.setMarker(marker.icon)
                add(overlayItem)

                // Me falta que este objeto lleve los métodospara interactuar con él
            },
            // Listener para controlar los gestos (pulsaciones)
            // Y en ellos ya hacemos lo que queramos
            object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    // el item lleva mucha info, puede devolver latitud y longitud
                    var geoPoint = GeoPoint(item.point.latitude, item.point.longitude)
                    mostrarDialogo("Has pulsado en el marcador con título: " + item.title, geoPoint)
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    return false
                }

            },
            // Y para terminar le pasamos el contexto
            map.context
        )
    }

    private fun mostrarDialogo(tituloP: String, loc: GeoPoint) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(tituloP)
            .setMessage("Latitud: " + loc.latitude + "; Longitud: " + loc.longitude)
            .setCancelable(true) // Para que el usuario pueda salirse sin tener que elegir algo
            .show()
    }



}