package com.break4learning.sqliteconroom_v34.bbdd

// Pongo los imports:
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    // Empiezo por indicarle las entities que voy a utilizar
    entities = [TaskEntity::class, EmpleEntity::class],
    // Pongo la versión de la base de datos, porque cuando hago una actualización
    // de la aplicación y esta actualización implica una actualización de la base
    // de datos, puedo meter un método que lo haga automáticamente
    version = 1
)
abstract class TaskDatabase: RoomDatabase() {
    // Hay que crear una función por cada DAO que tenga
    abstract fun taskDao(): TaskDao
    abstract fun empleDao(): EmpleDao

    // Ahora viene la part de la creación o utilización de la base de datos
    // un companion es un objeto que vamos a compartir
    companion object {
        // me declaro una variable que va a ser visible por varios threads; si tengo varios
        // threads que estén operando con la bbdd, todos ellos van a poder ver esa variable
        @Volatile
        private var INSTANCE: TaskDatabase? = null // de momento se inicializa a nulo


        // A partir de aquí, ésto es igual para absolutamente todos los proyectos.


        fun getDatabase(context: Context): TaskDatabase{
            // tengo que comprobar que la instancia de base de datos no sea nula, es decir,
            // que está creada; si es nula, tengo que crearla. Tras tenerla o crearla, la devuelvo.
            // Es como darte acceso a la abse de datos.
            if (INSTANCE == null) {
                // el synchronized se pone para bloquear el hilo y para que no pueda estar lanzando
                // eso desde varios hilos
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }


        private fun buildDatabase(context: Context): TaskDatabase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database" // Nombre que le voy a dar a labbdd
                    ).build()
        }

        // Con ésto hecho, nos vamos al main activity.
    }
}