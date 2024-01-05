package com.example.monsterhunterfinder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monsterhunterfinder.adapter.EntradasAdapter
import com.example.monsterhunterfinder.databinding.ActivityDiarioVistaBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity dedicada a la vista principal del diario de caza,
 * desde la cual el usuario puede hacer scroll por el registro
 * de todas sus entradas del diario, visualizar una versión más
 * completa de las mismas, editarlas, eliminarlas, añadir nuevas
 * entradas, filtrar las entradas que aparecen en la lista según
 * el arma que se haya utilizado y limpiar el filtro.
 * @author Jaime
 */
class activity_diario_vista : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioVistaBinding

    // Datos necesarios para crear el canal de notificaciones
    private val ID_CANAL: String = "MHFD"
    private var PETICION_PERMISOS: Int = 111

    // activityResultLauncher preparado para el caso de
    // modificación de registros
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    // Se recupera la colección "cazas" de la firestore;
    // se trata de un diario de caza
    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("cazas")


    // Variables para el RecyclerView
    private lateinit var entradaProvider: EntradaProvider
    private lateinit var entradasAdapter: EntradasAdapter
    val manager = LinearLayoutManager(this)
    private var posicion: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        // Creación del canal de notificaciones
        crearCanalNotificaciones()

        // Se establece como toolbar la que nos hemos creado
        // y se le indica que no muestre el título
        setSupportActionBar(binding.toolbar?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Se registra el filtro para armas para que surja de él
        // un menú contextual
        registerForContextMenu(binding.textoFiltroArma)


        iniciarRecyclerView()

        // Definición de listeners para mostrar una RecyclerView
        // filtrada y para lanzar la activity que nos permite
        // añadir un registro a la colección
        binding.botonFiltrar.setOnClickListener {
            listarFiltrando()
        }


        binding.botonAnadir.setOnClickListener {
            lanzarActivityRegistro()
        }

        // Se prepara un activityResultLauncher para
        // volver desde otra activity conteniendo datos
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
                    if (resultado.data != null) {
                        val datos: Intent = resultado.data!!
                        val numEntrada: Int

                        // Si los datos recuperados tienen id, se estaba modificando
                        // un registro, con lo que se usa la misma id que tenía
                        if (datos.hasExtra("id")) {
                            numEntrada = datos.getStringExtra("id")!!.toInt()
                        } else {
                            // Si no, se estaba añadiendo un registro nuevo, y su id
                            // se obtiene mediante un método
                            numEntrada = entradaProvider.getId()
                        }


                        // Se crea un objeto Entrada con los datos obtenidos
                        // del activityResultLauncher
                        var entradaDiario = Entrada(
                            numEntrada,
                            datos.getStringExtra("titulo")!!,
                            datos.getStringExtra("arma")!!,
                            datos.getStringExtra("resumen")!!
                        )


                        // Se actúa y se lanzan notificaciones distintas dependiendo
                        // de si se estaba modificando o añadiendo un registro
                        if (datos.hasExtra("id")) {
                            actualizarRegistro(posicion, entradaDiario)
                            lanzarNotifModificar()
                        } else {
                            insertarRegistro(entradaDiario)
                            lanzarNotifAnadir()
                        }

                    }
            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Aquí se inflan los objetos xml de la toolbar que se use
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
    }

    /**
     * Función que toma el archivo xml del layout asociado
     * a la activity y lo infla, creando objetos con él.
     */
    private fun crearObjetosDelXml() {
        binding=ActivityDiarioVistaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Función que crea el canal de notificaciones desde el cual
     * se enviarán al usuario las notificaciones relativas a las
     * interacciones con las entradas del diario de caza.
     */
    private fun crearCanalNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Monster Hunter Finder diario"
            val textoDescripcion = "Canal para las notificaciones del diario de Monster Hunter Finder"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT

            // Se crea el canal pasando como info las variables que nos hemos definido
            val canal = NotificationChannel(ID_CANAL, nombre, importancia).apply{
                description = textoDescripcion
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }

    }

    /**
     * Función que, a través de un canal de notificaciones que debe haberse
     * creado con anterioridad, lanza una notificación cuando se añade una
     * entrada al diario de caza.
     * Únicamente en el caso de que el usuario le haya concedido permisos de
     * notificaciones a la aplicación.
     */
    fun lanzarNotifAnadir() {

        // Inicialmente se verifica si se han concedido permisos;
        // si no se han concedido, se solicitan
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(this, R.string.darPermisos.toString(), Toast.LENGTH_LONG).show()
        } else {
            if (ActivityCompat.checkSelfPermission(
                this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ), PETICION_PERMISOS
                )
            }
        }

        // Se lanza un PendingIntent, de tal manera que la notificación
        // seguirá presente aunque se haya cerrado la aplicación, y nos
        // redirigirá a la activity de vista del diario si se toca la notificación
        val contenidoIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, activity_diario_vista::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        // Se crea la notificación con los contenidos que se vayan indicando
        var notificationBuilder = NotificationCompat.Builder(this, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_input_get)
            .setContentTitle(getString(R.string.diarioCazaNotificaciones))
            .setContentText(getString(R.string.entradaAnadidaNotif))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contenidoIntent)

        // Y se lanza dicha notificación
        with(NotificationManagerCompat.from(this)){
            notify(1, notificationBuilder.build())
        }
    }


    /**
     * Función que, a través de un canal de notificaciones que debe haberse
     * creado con anterioridad, lanza una notificación cuando se modifica una
     * entrada del diario de caza.
     * Únicamente en el caso de que el usuario le haya concedido permisos de
     * notificaciones a la aplicación.
     * La notificación que se lanza es distinta de la notificación de añadir,
     * incluyendo ID de la notificación diferente.
     * La documentación intra-código es la misma que la del método lanzarNotifAnadir()
     */
    fun lanzarNotifModificar() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(this, R.string.darPermisos.toString(), Toast.LENGTH_LONG).show()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ), PETICION_PERMISOS
                )
            }
        }

        val contenidoIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, activity_diario_vista::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        var notificationBuilder = NotificationCompat.Builder(this, ID_CANAL)
            .setSmallIcon(android.R.drawable.ic_menu_sort_by_size)
            .setContentTitle(getString(R.string.diarioCazaNotificaciones))
            .setContentText(getString(R.string.entradaModificadaNotif))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contenidoIntent)

        with(NotificationManagerCompat.from(this)){
            notify(2, notificationBuilder.build())
        }

    }

    /**
     * Función que finaliza la activity actual, devolviendo
     * al usuario a aquella activity desde la que hubiera
     * accedido.
     * También detiene la reproducción de audio si la había.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun volver(view: View) {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // En este caso, la única acción que se puede iniciar
            // seleccionando items de la toolbar es la de abrir web
            R.id.Web -> {
                abrirWeb()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Función que lanza un intent para abrir una
     * activity con la página web contenida en la
     * String que se le indique al parser.
     */
    fun abrirWeb() {
        val lanzarWeb: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mhrise.kiranico.com/es"))
        startActivity(lanzarWeb)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        when (v) {
            // En el EditText para el filtro de arma se infla como menú
            // contextual el menú xml indicado
            binding.textoFiltroArma -> menuInflater.inflate(R.menu.menu_armas, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Dependiendo de la opción del menú contextual que se seleccione, el
        // texto del EditText para el filtro de armas se establece a un valor.
        return when (item.itemId) {
            R.id.menuGranEspada -> {
                binding.textoFiltroArma.setText(R.string.granEspada)
                true
            }

            R.id.menuEspadaLarga -> {
                binding.textoFiltroArma.setText(R.string.espadaLarga)
                true
            }

            R.id.menuEspadaYEscudo -> {
                binding.textoFiltroArma.setText(R.string.espadaYEscudo)
                true
            }

            R.id.menuEspadasDobles -> {
                binding.textoFiltroArma.setText(R.string.espadasDobles)
                true
            }

            R.id.menuLanza -> {
                binding.textoFiltroArma.setText(R.string.lanza)
                true
            }

            R.id.menuLanzaPistola -> {
                binding.textoFiltroArma.setText(R.string.lanzaPistola)
                true
            }

            R.id.menuMartillo -> {
                binding.textoFiltroArma.setText(R.string.martillo)
                true
            }

            R.id.menuCornamusa -> {
                binding.textoFiltroArma.setText(R.string.cornamusa)
                true
            }

            R.id.menuHachaEspada -> {
                binding.textoFiltroArma.setText(R.string.hachaEspada)
                true
            }

            R.id.menuHachaCargada -> {
                binding.textoFiltroArma.setText(R.string.hachaCargada)
                true
            }

            R.id.menuGlaiveInsecto -> {
                binding.textoFiltroArma.setText(R.string.glaiveInsecto)
                true
            }

            R.id.menuBallestaLigera -> {
                binding.textoFiltroArma.setText(R.string.ballestaLigera)
                true
            }

            R.id.menuBallestaPesada -> {
                binding.textoFiltroArma.setText(R.string.ballestaPesada)
                true
            }

            R.id.menuArco -> {
                binding.textoFiltroArma.setText(R.string.arco)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    /**
     * Función que vacía el contenido del campo dedicado al
     * filtro para armas y reinicia la RecyclerView.
     */
    fun vaciarCampos() {
        binding.textoFiltroArma.setText("")
        iniciarRecyclerView()
    }

    /**
     * Función que crea un cuadro de diálogo al presionarse
     * la view a la que va asociada, procurando que el usuario
     * se asegure de la acción que desea ejecutar.
     * @param view: vista (botón en este caso) cuya activación
     * inicia la función
     */
    fun mostrarOpciones(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.cuidado)
            .setMessage(R.string.vaciarFiltroPregunta)
            .setCancelable(false) // El usuario no puede salirse clickando fuera
            .setPositiveButton(
                R.string.vaciarFiltroSi,
                DialogInterface.OnClickListener{
                        // Si se elige confirmar, se vacía el campo del filtro para armas
                        dialog, id -> vaciarCampos()
                }
            )
            .setNegativeButton(
                R.string.vaciarFiltroNo,
                DialogInterface.OnClickListener{
                        // Si se elige negar, se cierra el cuadro de diálogo
                        dialog, id -> dialog.cancel()
                }
            )
            .show()
    }


    /**
     * Método que inicializa la RecyclerView con el contenido de la
     * colección cuya variable hayamos inicializado anteriormente.
     */
    private fun iniciarRecyclerView() {
        val decoracion = DividerItemDecoration(this, manager.orientation)

        binding.recyclerEntradas.layoutManager = manager

        // Se pasan como lambda las funciones de borrado y modificado
        entradaProvider = EntradaProvider()
        entradasAdapter = EntradasAdapter(
            listaEntradas = entradaProvider.listaEntradas,
            borrarRegistro = {posicion: Int, id: Int -> borrarRegistro(posicion, id)},
            lanzarActivityModificar = {posicion: Int, entradaDiario: Entrada -> lanzarActivityModificar(posicion, entradaDiario)})

        myCollection
            .get()
            .addOnSuccessListener {
                resultado ->
                    entradaProvider.actualizarLista(resultado)
                    binding.recyclerEntradas.adapter = entradasAdapter
                    binding.recyclerEntradas.addItemDecoration(decoracion)
            }
    }


    /**
     * Método que realiza una función similar a la de inicializar una
     * RecyclerView, con la salvedad de que sólo muestra aquellos registros
     * que coincidan con el filtro que establece el método.
     * Filtra registros según el campo "arma" de la Firestore.
     * @param palabrasBuscar: String que contiene aquel texto con el que
     * se van a buscar coincidencias en el campo "arma" de los registros
     * de la Firestore.
     */
    private fun listarFiltrando() {
        val decoracion = DividerItemDecoration(this, manager.orientation)

        binding.recyclerEntradas.layoutManager = manager

        // Se pasan como lambda las funciones de borrado y modificado para poder
        // hacer dichas funciones aun mientras se está filtrando
        entradaProvider = EntradaProvider()
        entradasAdapter = EntradasAdapter(
            listaEntradas = entradaProvider.listaEntradas,
            borrarRegistro = {posicion: Int, id: Int -> borrarRegistro(posicion, id)},
            lanzarActivityModificar = {posicion: Int, entradaDiario: Entrada -> lanzarActivityModificar(posicion, entradaDiario)})

        myCollection
            // El filtrado busca coincidencias por el campo "arma" de la Firestore
            .whereEqualTo("arma", binding.textoFiltroArma.text.toString())
            .get()
            .addOnSuccessListener {
                    resultado ->
                entradaProvider.actualizarLista(resultado)
                binding.recyclerEntradas.adapter = entradasAdapter
                binding.recyclerEntradas.addItemDecoration(decoracion)
            }
    }


    /**
     * Función que llama a un activityResultLauncher que lanza un intent para
     * abrir la activity necesaria para añadir un registro al diario
     * de caza, de la cual se recogerán datos que vendrán contenidos en
     * el activityResultLauncher llamado.
     */
    private fun lanzarActivityRegistro() {
        val intent = Intent(this, activity_diario_anadir::class.java)
        activityResultLauncher.launch(intent)
    }

    /**
     * Función que llama a la variable asociada a la colección del diario de
     * caza y le inserta un registro, utilizando para generar los campos del
     * mismo los datos contenidos por una data class de tipo Entrada que se
     * le pase por parámetro.
     * @param entradaDiario: objeto data class de tipo Entrada cuyos datos
     * se utilizan para generar los campos del registro.
     */
    private fun insertarRegistro(entradaDiario: Entrada) {
        myCollection
            .document(entradaDiario.numEntrada.toString())
            .set(
                hashMapOf(
                    "titulo" to entradaDiario.titulo,
                    "arma" to entradaDiario.arma,
                    "resumen" to entradaDiario.resumen
                )
            )
            .addOnSuccessListener {
                entradaProvider.listaEntradas.add(entradaDiario.numEntrada, entradaDiario)
                entradasAdapter.notifyItemInserted(entradaDiario.numEntrada)
                manager.scrollToPositionWithOffset(entradaDiario.numEntrada, 35)
            }
    }

    /**
     * Función que llama a la variable asociada a la colección del diario de
     * caza y elimina un registro de la misma, usando la posición y la id
     * del registro a eliminar para identificarlo.
     * La id se emplea con respecto a la colección, y la posición se usa
     * para tratar datos en el provider y en el adapter.
     * @param posicion: posición del registro a eliminar
     * @param id: id del registro a eliminar
     */
    private fun borrarRegistro(posicion: Int, id: Int) {
        myCollection
            .document(id.toString())
            .delete()
            .addOnSuccessListener {
                entradaProvider.listaEntradas.removeAt(posicion)
                entradasAdapter.notifyItemRemoved(posicion)
            }
    }

    /**
     * Función que llama a la variable asociada a la colección del diario de
     * caza y modifica (sobreescribe) un registro de la misma, usando la
     * posición y la id del registro a actualizar para identificarlo.
     * La id se emplea con respecto a la colección, y la posición se usa
     * para tratar datos en el provider y en el adapter.
     * @param posicion: posición del registro a eliminar
     * @param id: id del registro a eliminar
     */
    private fun actualizarRegistro(posicion: Int, entradaDiario: Entrada) {
        myCollection
            .document(entradaDiario.numEntrada.toString())
            .set(
                hashMapOf(
                    "titulo" to entradaDiario.titulo,
                    "arma" to entradaDiario.arma,
                    "resumen" to entradaDiario.resumen
                )
            )
            .addOnSuccessListener {
                entradaProvider.listaEntradas.set(posicion, entradaDiario)
                entradasAdapter.notifyItemChanged(posicion)
                manager.scrollToPositionWithOffset(posicion, 35)
            }
    }

    /**
     * Función que llama a un activityResultLauncher que lanza un intent para
     * abrir la activity necesaria para modificar un registro del diario
     * de caza, de la cual se recogerán datos que vendrán contenidos en
     * el activityResultLauncher llamado.
     *
     * Se diferencia de lanzarActivityRegistro en que se usa un intent con
     * varios putExtra, con datos extraídos de la colección del diario de
     * caza, de manera que la activity a la que se llama mediante esta función
     * recoge dichos datos y los muestra en sus views.
     *
     * La activity utilizada para modificar registros es un reciclaje de la
     * activity utilizada para añadir registros.
     *
     * @param posicion: posición en la colección del registro a modificar
     * @param entradaDiario: objeto Entrada cuyos datos se envían a la activity
     * necesaria para modificar datos, para que los muestre en sus views
     */
    private fun lanzarActivityModificar(posicion: Int, entradaDiario: Entrada) {
        val intent = Intent(this, activity_diario_anadir::class.java)

        this.posicion = posicion

        intent.putExtra("id", entradaDiario.numEntrada.toString())
        intent.putExtra("titulo", entradaDiario.titulo)
        intent.putExtra("arma", entradaDiario.arma)
        intent.putExtra("resumen", entradaDiario.resumen)

        activityResultLauncher.launch(intent)
    }
}