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
        btnSetName.setOnClickListener{SetName()}
    }


    fun SetName(){
        val str=strName.getText().toString()
        GLOBAL.NAME=str
        val cacheFile_name="$filesDir/"+"setting.csv"
        val cacheFile= File(cacheFile_name)
        cacheFile.writeText(str)
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