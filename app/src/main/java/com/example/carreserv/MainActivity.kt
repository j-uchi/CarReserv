package com.example.carreserv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {
    //起動時動作
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RandomBackGround()
        val fab:View=findViewById(R.id.fab)
        fab.setOnClickListener{view->
            startActivity(Intent(this,DispReserv::class.java))
        }
    }
    //メニューボタン生成
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId == R.id.menu_calendar){
            startActivity(Intent(this,DispCalendar::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    fun RandomBackGround(){
        val now= SimpleDateFormat("HH").format(Date()).toInt()
        val r=Random().nextInt(3)
        when{
            now>=6&&now<16->{
                if(r==0)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.hiru0)
                if(r==1)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.hiru1)
                if(r==2)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.hiru2)
            }
            now>=16&&now<20->{
                if(r==0)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.yu0)
                if(r==1)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.yu1)
                if(r==2)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.yu2)
            }
            now>=20||now<6->{
                if(r==0)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.yoru0)
                if(r==1)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.yoru1)
                if(r==2)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.yoru2)
            }
            else->{
                if(r==0)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.hiru0)
                if(r==1)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.hiru1)
                if(r==2)findViewById<ImageView>(R.id.img_back).setImageResource(R.drawable.hiru2)
            }
        }

    }


}