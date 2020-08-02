package com.example.carreserv

import android.app.Application
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class MyApp: Application(){

    data class DC_RECORD(var R_ID:String,var R_STARTDATE:String,var R_STARTTIME:String,var R_ENDDATE:String,
                      var R_ENDTIME:String,var R_Park:String,var R_ReservComment:String,var R_ParkComment:String,var R_Refuel:Boolean)

    var RECORD=mutableListOf<DC_RECORD>()




    //開始時処理
    override fun onCreate(){
        super.onCreate()
        READFILE()
    }

    companion object{
        private var instance:MyApp?=null
        fun getInstance():MyApp{
            if(instance==null)
                instance=MyApp()
            return instance!!
        }
    }

    fun READFILE(){
        val GLOBAL=MyApp.getInstance()
        try{
            val file= File("$filesDir/", "sql_cache.csv")
            val scan= Scanner(file)
            scan.useDelimiter(",|\n")
            while(scan.hasNext()){
                val ID=scan.next()
                val STARTDATE=scan.next()
                val STARTTIME=scan.next()
                val ENDDATE=scan.next()
                val ENDTIME=scan.next()
                val PARK=scan.next()
                val RESERVCOMMENT=scan.next()
                val PARKCOMMENT=scan.next()
                val REFUEL=scan.next().toBoolean()
                GLOBAL.RECORD.add(DC_RECORD(ID,STARTDATE,STARTTIME,ENDDATE,ENDTIME,PARK,RESERVCOMMENT,PARKCOMMENT,REFUEL))
            }

        }catch(e: FileNotFoundException){
            //ファイルが存在しない場合は、カラのファイルを新規で作成する
            val cacheFile_name="$filesDir"+"/sql_cache.csv"
            val cacheFile= File(cacheFile_name)
            cacheFile.writeText("")
        }
    }


}