package com.example.carreserv

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_disp_setting.*
import java.io.File
import java.io.FileNotFoundException

class DispSetting : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        strName.setText(GLOBAL.NAME)
        btnTEST.setOnClickListener{CreateTESTFile()}
        btnSetName.setOnClickListener{SetName()}
    }


    fun SetName(){
        val str=strName.getText().toString()
        GLOBAL.NAME=str
        val cacheFile_name="$filesDir/"+"setting.csv"
        val cacheFile= File(cacheFile_name)
        cacheFile.writeText(str)
    }

    fun CreateTESTFile(){
        val cacheFile_name="$filesDir"+"/sql_cache.csv"
        val settingFile_name="$filesDir"+"setting.csv"

        val SQLcache:String="hello,内,2020年8月2日,0時0分,2020年8月2日,12時0分,東比恵,テストメモ,タイヤワックス塗っといた,false,hash\n"+
                "hello,内,2020年8月2日,6時0分,2020年8月2日,10時0分,大濠,めもめも,めもに,false,hash\n"+
                "hello,内,2020年8月1日,23時0分,2020年8月4日,23時30分,学校,あいうえお,かきくけこ,true,hash\n"

        try{
            val cacheFile= File(cacheFile_name)
            cacheFile.writeText(SQLcache)
        }catch(e: FileNotFoundException){
            println(e)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}