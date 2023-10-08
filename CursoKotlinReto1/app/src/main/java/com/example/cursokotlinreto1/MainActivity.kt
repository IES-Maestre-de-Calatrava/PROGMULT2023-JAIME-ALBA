package com.example.cursokotlinreto1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

val TAG:String = ":::::::::::::"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var jaime:Programador = Programador()

        Log.d(TAG,jaime.getProgrammerData().name)
        Log.d(TAG,jaime.getProgrammerData().age.toString())
        Log.d(TAG,jaime.getProgrammerData().language)
        // También puedo declararme una variable que sea de tipo ProgrammerData y
        // mediante el nombre de la variable acceder a los atributos que tenga asignados
    }
}

public class Programador() : ProgramadorInterface {
    override fun getProgrammerData():ProgrammerData {
        var datos:ProgrammerData = ProgrammerData(returnName(), returnAge(), returnLanguage())
        return datos;
    }

    private fun returnName():String {
        var name:String = "Jaime"
        return name
    }

    private fun returnAge():Int {
        var age:Int = 28
        return age
    }

    private fun returnLanguage():String {
        var language:String = "Kotlin"
        return language;
    }
}


interface ProgramadorInterface {
    fun getProgrammerData():ProgrammerData
}

public data class ProgrammerData (
    val name:String,
    val age:Int,
    val language:String
)