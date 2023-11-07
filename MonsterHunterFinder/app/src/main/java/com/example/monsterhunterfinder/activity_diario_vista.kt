package com.example.monsterhunterfinder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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

class activity_diario_vista : AppCompatActivity() {

    private lateinit var binding: ActivityDiarioVistaBinding

    private val ID_CANAL: String = "MHFD"
    private var PETICION_PERMISOS: Int = 111

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


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
        crearCanalNotificaciones()

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        registerForContextMenu(binding.textoFiltroArma)


        iniciarRecyclerView()

        binding.botonFiltrar.setOnClickListener {
            listarFiltrando()
        }



        binding.botonAnadir.setOnClickListener {
            lanzarActivityRegistro()
        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
                    if (resultado.data != null) {
                        val datos: Intent = resultado.data!!
                        val numEntrada: Int

                        if (datos.hasExtra("id")) {
                            numEntrada = datos.getStringExtra("id")!!.toInt()
                        } else {
                            numEntrada = entradaProvider.getId()
                        }



                        var entradaDiario = Entrada(
                            numEntrada,
                            datos.getStringExtra("titulo")!!,
                            datos.getStringExtra("arma")!!,
                            datos.getStringExtra("resumen")!!
                        )



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
        menuInflater.inflate(R.menu.toolbar_otras_activities, menu)
        return true
    }

    private fun crearObjetosDelXml() {
        binding=ActivityDiarioVistaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun crearCanalNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Monster Hunter Finder diario"
            val textoDescripcion = "Canal para las notificaciones del diario de Monster Hunter Finder"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT

            val canal = NotificationChannel(ID_CANAL, nombre, importancia).apply{
                description = textoDescripcion
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }

    }

    fun lanzarNotifAnadir() {

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
            .setSmallIcon(android.R.drawable.ic_input_get)
            .setContentTitle(getString(R.string.diarioCazaNotificaciones))
            .setContentText(getString(R.string.entradaAnadidaNotif))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contenidoIntent)

        with(NotificationManagerCompat.from(this)){
            notify(1, notificationBuilder.build())
        }
    }

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
            notify(1, notificationBuilder.build())
        }


    }

    fun volver(view: View) {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Web -> {
                abrirWeb()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

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
            binding.textoFiltroArma -> menuInflater.inflate(R.menu.menu_armas, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
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

    fun vaciarCampos() {
        binding.textoFiltroArma.setText("")
        iniciarRecyclerView()
    }

    fun mostrarOpciones(view: View) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.cuidado)
            .setMessage(R.string.vaciarFiltroPregunta)
            .setCancelable(false) // El usuario no puede salirse clickando fuera
            .setPositiveButton(
                R.string.vaciarFiltroSi,
                DialogInterface.OnClickListener{
                        dialog, id -> vaciarCampos()
                }
            )
            .setNegativeButton(
                R.string.vaciarFiltroNo,
                DialogInterface.OnClickListener{
                        dialog, id -> dialog.cancel()
                }
            )
            .show()
    }


    private fun iniciarRecyclerView() {
        val decoracion = DividerItemDecoration(this, manager.orientation)

        binding.recyclerEntradas.layoutManager = manager

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


    private fun listarFiltrando() {
        val decoracion = DividerItemDecoration(this, manager.orientation)

        binding.recyclerEntradas.layoutManager = manager

        entradaProvider = EntradaProvider()
        entradasAdapter = EntradasAdapter(
            listaEntradas = entradaProvider.listaEntradas,
            borrarRegistro = {posicion: Int, id: Int -> borrarRegistro(posicion, id)},
            lanzarActivityModificar = {posicion: Int, entradaDiario: Entrada -> lanzarActivityModificar(posicion, entradaDiario)})

        myCollection
            .whereEqualTo("arma", binding.textoFiltroArma.text.toString())
            .get()
            .addOnSuccessListener {
                    resultado ->
                entradaProvider.actualizarLista(resultado)
                binding.recyclerEntradas.adapter = entradasAdapter
                binding.recyclerEntradas.addItemDecoration(decoracion)
            }
    }


    private fun lanzarActivityRegistro() {
        val intent = Intent(this, activity_diario_anadir::class.java)
        activityResultLauncher.launch(intent)
    }

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

    private fun borrarRegistro(posicion: Int, id: Int) {
        myCollection
            .document(id.toString())
            .delete()
            .addOnSuccessListener {
                entradaProvider.listaEntradas.removeAt(posicion)
                entradasAdapter.notifyItemRemoved(posicion)
            }
    }

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