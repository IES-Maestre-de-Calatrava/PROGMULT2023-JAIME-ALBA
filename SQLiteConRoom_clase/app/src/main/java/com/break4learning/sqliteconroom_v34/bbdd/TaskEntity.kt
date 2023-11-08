package com.break4learning.sqliteconroom_v34.bbdd

// Me copio los imports menos el de foreign key

import androidx.room.ColumnInfo
import androidx.room.Entity

// Y meto otro
import androidx.room.PrimaryKey


// Indico que es una Entidad y el nombre de la tabla que va a tener en la base de datos
@Entity(
    tableName = "task_entity"
)
data class TaskEntity(
    // Declaro los atributos que va a tener; empiezo por la primary key
    // le digo que la genere automáticamente
    @PrimaryKey(autoGenerate = true)
    // pongo el nombre del campo en la tabla; ese nombre del campo en la tabla
    // tiene que ir enlazado con un atributo de la clase; se lo indico
    @ColumnInfo(name = "id")
    var id:Int = 0, //atributo asociado al campo anterior, para que sepa dónde meter los
    // datos cuando trabaje con ellos

    @ColumnInfo(name = "name")
    var name: String = "",  // por lo general me interesa llamar igual los atributos
    // y los campos de la tabla, para no liarme
    @ColumnInfo(name = "isDone")
    var isDone: Boolean = false

    // Tras eso, nos vamos a la de EmpleEntity
)
