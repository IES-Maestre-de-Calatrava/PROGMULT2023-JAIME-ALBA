package com.break4learning.sqliteconroom_v34.bbdd

// Empiezo haciendo varios imports
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

// Lo dejo a medias y me hago una kotlin data class para TaskEntity; me voy a ella


@Entity(
    // Le indico el nombre de la tabla
    tableName = "emple",
    primaryKeys = ["n_depart", "n_emple"],
    foreignKeys = [ // hay que indicarle hacia qué clase apunta la foreign key
        ForeignKey(
            entity = TaskEntity::class, // la tabla de la que la toma
            parentColumns = ["id"], // la columna de esta tabla
            childColumns = ["id_task"], // la columna a la que apunta
            onDelete = ForeignKey.CASCADE // borrado en cascada
        )

    ]
)

// Ahora cambia una cosa; cuando la Primary Key está formada por VARIAS COLUMNAS, SE PONE
// DEBAJO DEL TABLENAME. TAMBIÉN LAS FOREIGN KEYS.
data class EmpleEntity(
    // voy teniendo la correspondencia [atributo en la tabla] -> [atributo en la clase]
    @ColumnInfo(name = "n_depart")
    var n_depart: String = "",

    @ColumnInfo(name = "n_emple")
    var n_emple: String = "",

    @ColumnInfo(name = "nombre")
    var nombre: String? = "", // pongo la interrogación porque PUEDE ADMITIR VALORES NULOS

    @ColumnInfo(name = "id_task")
    var id_task: Int = 0

    // Tras ésto, nos vamos a hacer las clases DAO.
    // Click derecho, new Kotlin Class, Interface.
)
