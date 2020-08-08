package com.example.carreserv

import android.app.Application
import java.io.File
import java.io.FileNotFoundException
import java.io.Serializable
import java.util.*

class MyApp: Application(){


    data class DC_RECORD(var R_ID:String,var R_NAME:String,var R_STARTDATE:String,var R_STARTTIME:String,var R_ENDDATE:String,
                      var R_ENDTIME:String,var R_PARK:String,var R_START_COMMENT:String,var R_END_COMMENT:String,var R_REFUEL:Boolean)

    var RECORD=mutableListOf<DC_RECORD>()

    var SEND_RECORD:DC_RECORD=DC_RECORD("","","","","","","","","",false)

    var NAME:String=""

    var RESPONSE_STATE=-1

    var RESPONSE_STR:String=""


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
            val file= File("$filesDir/", "setting.csv")
            val scan= Scanner(file)
            scan.useDelimiter(",|\n")
            GLOBAL.NAME=scan.next()
        }catch(e: FileNotFoundException){
            //ファイルが存在しない場合は、カラのファイルを新規で作成する
            val cacheFile_name="$filesDir/"+"setting.csv"
            val cacheFile= File(cacheFile_name)
            cacheFile.writeText("guest")
            GLOBAL.NAME="guest"
        }
    }


}