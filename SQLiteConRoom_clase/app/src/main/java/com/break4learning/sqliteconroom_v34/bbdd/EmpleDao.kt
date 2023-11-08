package com.break4learning.sqliteconroom_v34.bbdd

// Hago los imports
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


// le pongo lo del dao
@Dao
interface EmpleDao {

    // Una consulta que lo muestre todo:
    @Query("SELECT * FROM emple") // hay que poner lo que le haya puesto en el TABLENAME A LA CLASE
    suspend fun getAllEmple(): MutableList<EmpleEntity>

    // le paso por parámetro a la consulta (debajo) el número de departamento y el
    // número de empleado, que son Strings, y devuelven UNA entidad empleado.
    @Query("SELECT * FROM emple WHERE n_depart= :p_n_depart AND n_emple= :p_n_emple")
    suspend fun getEmpleById(p_n_depart: String, p_n_emple: String): EmpleEntity

    @Insert
    suspend fun addEmple(empleEntity: EmpleEntity): Long

    // Con ésto, tenemos las clases JPA (las entities) y las clases DAO, pero me falta
    // otra clase a la que le tengo que dar información de la base de datos.

    // Ahora tengo que hacerme una clase para decirle cómo se llaman las clases que
    // recogen los datos, cómo se llama la base de datos, que la cree si no está
    // creada, etc.
    // En el package bbdd me creo una Kotlin Class normalita. TaskDatabase.
}