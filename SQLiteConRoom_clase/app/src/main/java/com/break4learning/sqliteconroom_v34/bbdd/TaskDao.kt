package com.break4learning.sqliteconroom_v34.bbdd

// Hago los imports necesarios (los da él)
import androidx.room.*

// le digo que es claseo DAO
@Dao
interface TaskDao {
    /**
     * Obtener todas las tareas
     */


    // cambia la cosa; los métodos ya no van por fun y luego lo que le pido, sino AL REVÉS
    // primero va lo que le pido
    @Query("SELECT * FROM task_entity") // usa la clase que le hemos indicado, porque a la
    // clase le hemos indicado que va asociada a la tabla

    // y ahora es cuando declaro el método; devuelve una mutable list
    // llena de objetos task entity
    suspend fun getAllTasks(): MutableList<TaskEntity>
    // para poder lanzarlo en un thread que va en la corrutina, hace falta el suspend

    // lo necesario para consultar una tarea de la que le pasamos una ID:
    // se la pasamos por parámetro. Lo marca en rojo porque no sabe de dónde cogerlo.
    @Query("SELECT * FROM task_entity where id = :p_id")
    // le pasamos nosotros el p_id
    suspend fun getTaskById(p_id: Long): TaskEntity // es TaskEntity porque devuelve sólo uno

    // Ejemplo de cómo sería un insert: ya no es una query (consulta),
    // ahora es un insert a secas
    // tengo que decirle qué tiene que hacer en caso de que YA HAYA UN REGISTRO CON ESA ID
    // en nuestro caso, le decimos que lo reemplace. Puedo ponerle NONE para que no haga nada.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(taskEntity: TaskEntity): Long // devuelve un int, que es el ID de la tarea
    // que acaba de isnertar.

    // Para un update:
    @Update
    // le paso por parámetro el objeto TaskEntity que quiero que actualice
    suspend fun updateTask(taskEntity: TaskEntity): Int

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity): Int

    // Con todo ello, en lugar de con la tabla, trabajo con la clase TaskEntity.
    // Tras ello, me hago el EmpleDao.
}