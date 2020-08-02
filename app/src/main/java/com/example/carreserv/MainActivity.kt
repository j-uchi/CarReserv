package com.example.carreserv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    //起動時動作
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RandomBackGround()
        setStatus()
        val fab:View=findViewById(R.id.fab)
        fab.setOnClickListener{view->
            startActivity(Intent(this,DispReserv::class.java))
        }
    }
    override fun onRestart() {
        super.onRestart()
        RandomBackGround()
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
        if(item?.itemId == R.id.menu_Setting){
            startActivity(Intent(this,DispSetting::class.java))
        }
        return super.onOptionsItemSelected(item)
    }


    fun setStatus(){
        //未来に登録されたデータがあれば次回利用時間を表示
        if(GLOBAL.RECORD.size>0){

            for(i in 0..GLOBAL.RECORD.size-1){
                var S_str=GLOBAL.RECORD[i].R_STARTDATE+GLOBAL.RECORD[i].R_STARTTIME
                var S_date=S_str.split("年","月","日","時","分")
                var E_str=GLOBAL.RECORD[i].R_ENDDATE+GLOBAL.RECORD[i].R_ENDTIME
                var E_date=E_str.split("年","月","日","時","分")
                val S_calendar:Calendar=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
                val E_calendar:Calendar=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

                S_calendar.set(Calendar.YEAR,S_date[0].toInt())
                S_calendar.set(Calendar.MONTH,S_date[1].toInt()-1)
                S_calendar.set(Calendar.DAY_OF_MONTH,S_date[2].toInt())
                S_calendar.set(Calendar.HOUR_OF_DAY,S_date[3].toInt())
                S_calendar.set(Calendar.MINUTE,S_date[4].toInt())

                E_calendar.set(Calendar.YEAR,E_date[0].toInt())
                E_calendar.set(Calendar.MONTH,E_date[1].toInt()-1)
                E_calendar.set(Calendar.DAY_OF_MONTH,E_date[2].toInt())
                E_calendar.set(Calendar.HOUR_OF_DAY,E_date[3].toInt())
                E_calendar.set(Calendar.MINUTE,E_date[4].toInt())

                //開始時刻が今より未来であれば次回利用を表示
                val now=Calendar.getInstance()

                if(E_calendar.before(now)){
                    strNext.setText("利用予約はありません")
                    findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
                }
                else if(S_calendar.before(now)){
                    strNext.setText("返却予定："+GLOBAL.RECORD[i].R_ENDDATE+" "+GLOBAL.RECORD[i].R_ENDTIME)
                    strStatus.setText("使用中")
                    findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.use)
                    break;
                }
                else{
                    strNext.setText("次回利用："+GLOBAL.RECORD[i].R_STARTDATE+" "+GLOBAL.RECORD[i].R_STARTTIME)
                    strStatus.setText("未使用")
                    findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
                    break;
                }
            }

            while(GLOBAL.RECORD.size>0){
                var S_str=GLOBAL.RECORD[0].R_STARTDATE+GLOBAL.RECORD[0].R_STARTTIME
                var S_date=S_str.split("年","月","日","時","分")
                var E_str=GLOBAL.RECORD[0].R_ENDDATE+GLOBAL.RECORD[0].R_ENDTIME
                var E_date=E_str.split("年","月","日","時","分")
                val S_calendar:Calendar=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
                val E_calendar:Calendar=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

                S_calendar.set(Calendar.YEAR,S_date[0].toInt())
                S_calendar.set(Calendar.MONTH,S_date[1].toInt()-1)
                S_calendar.set(Calendar.DAY_OF_MONTH,S_date[2].toInt())
                S_calendar.set(Calendar.HOUR_OF_DAY,S_date[3].toInt())
                S_calendar.set(Calendar.MINUTE,S_date[4].toInt())

                E_calendar.set(Calendar.YEAR,E_date[0].toInt())
                E_calendar.set(Calendar.MONTH,E_date[1].toInt()-1)
                E_calendar.set(Calendar.DAY_OF_MONTH,E_date[2].toInt())
                E_calendar.set(Calendar.HOUR_OF_DAY,E_date[3].toInt())
                E_calendar.set(Calendar.MINUTE,E_date[4].toInt())

                //開始時刻が今より未来であれば次回利用を表示
                val now=Calendar.getInstance()

                if(E_calendar.before(now)){
                    GLOBAL.RECORD.removeAt(0)
                }
                else if(S_calendar.before(now)){
                    strNext.setText("返却予定："+GLOBAL.RECORD[0].R_ENDDATE+" "+GLOBAL.RECORD[0].R_ENDTIME)
                    strStatus.setText("使用中")
                    findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.use)
                    break;
                }
                else{
                    strNext.setText("次回利用："+GLOBAL.RECORD[0].R_STARTDATE+" "+GLOBAL.RECORD[0].R_STARTTIME)
                    strStatus.setText("未使用")
                    findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
                    break;
                }
            }


        }
        else{
            strNext.setText("利用予約はありません")
            findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
        }
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