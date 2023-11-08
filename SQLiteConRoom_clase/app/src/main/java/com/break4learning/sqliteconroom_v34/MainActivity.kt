package com.break4learning.sqliteconroom_v34
// Para poder lanzar el inspector: cambiar el minSdk33 en el gradle
// Crear un emulador que se descargue el sdk33
// Al ejecutar, la primera vez que ejecutemos el inspector carga las
// tabls y podemos acceder a la base de datos y a las tablas (verlas)
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.break4learning.sqliteconroom_v34.bbdd.EmpleEntity
import com.break4learning.sqliteconroom_v34.bbdd.TaskDatabase
import com.break4learning.sqliteconroom_v34.bbdd.TaskEntity
import com.break4learning.sqliteconroom_v34.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * clase para trabajar con SQLite a través de Room (utilizando clases de persistencia)
 *
 * Algunos enlaces de ampliación:
 * https://developer.android.com/training/data-storage/room?hl=es-419
 * https://johncodeos.com/how-to-use-room-in-android-using-kotlin/
 * https://github.com/johncodeos-blog/RoomExample
 */
class MainActivity : AppCompatActivity() {


    // Si quiero que me muestre todas las listas, tendré que tener una mutable list.
    // Para el listado de tareas.
    lateinit var tasks: MutableList<TaskEntity>

    // Para una única tarea.
    lateinit var task: TaskEntity

    // Para un empleado
    lateinit var emple: EmpleEntity

    // Si quiero lanzar una consulta que me haga una join entre empleados y tareas
    // y que me traiga el empleado y la tarea que desempeña, al estar en tareas
    // diferentes:
    lateinit var taskMap: Map<TaskEntity, EmpleEntity>


    // Si quiero trabajar con los DAOs de Emple y de Task, me tengo que
    // declarar una serie de constantes para poder manipular dichas clases

    // by lazy significa que hasta que no utilice esa variable en alguna parte
    // del código no ejecute lo que hay entre llaves.
    // de esa manera, no se utiliza el taskDao en ninguna parte del código
    // si no se usa la database
    private val taskDatabase by lazy {
        TaskDatabase.getDatabase(this).taskDao()
    }

    private val empleDatabase by lazy {
        TaskDatabase.getDatabase(this).empleDao()
    }
    // Tras eso, me voy al onCreate.



    /**
     * Binding
     */
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        // Me creo los objetos
        tasks = mutableListOf<TaskEntity>()
        task = TaskEntity()
        emple = EmpleEntity()

        // Y ya puedo empezar a asignar operaciones.
        // Empiezo por detectar cuándo alguien pulsa el botón de insertar una tarea
        binding.buttonInsert.setOnClickListener{
            // El insertar tiene que recibir una TaskEntity.
            // Este método, aún no creado, es el que llama al addTask del TaskDao.
            addTask(TaskEntity(name=binding.editTextTarea.text.toString()))
        }

    }


    private fun addTask(task: TaskEntity) {
        // Para poder lanzar el método en la corrutina
        // No lo hago directamente para tenerlo en código separado y que quede
        // más limpio.
        lifecycleScope.launch {
            taskDatabase.addTask(task)
        }
        // podría ponerse todo junto
    }


    /**
     * Crear objetos del xml
     *
     */
    private fun crearObjetosDelXml(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}