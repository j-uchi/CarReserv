package com.example.carreserv

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()
    var mHandler=Handler()

    //起動時動作
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RandomBackGround()
        REFLESH()
        val fab:View=findViewById(R.id.fab)
        fab.setOnClickListener{view->
            startActivity(Intent(this,DispReserv::class.java))
        }
        btnPark.setOnClickListener{view->
            CreateParkDialog()
        }
        swipe_refresh.setOnRefreshListener{ REFLESH() }
    }
    //再アクティブ時動作
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

    fun CreateParkDialog() {

        val strList = arrayOf("給油済み")
        val checkedItems = booleanArrayOf(false)
        val myedit = EditText(this)
        var dialogComment: String = ""
        var dialogRefuel: Boolean = false

        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
            .setTitle("車を返却します")
            .setMultiChoiceItems(strList, checkedItems, { dialog, which, isChecked ->
                dialogRefuel = isChecked
            })
            .setView(myedit)
            .setPositiveButton("OK", { dialog, which ->
                dialogComment = myedit.getText().toString()
                sendParkRequest(dialogComment,dialogRefuel)
            })
            .setNegativeButton("cancel", { dialog, which ->

            })
            .show()
    }

    fun sendParkRequest(str:String,ref:Boolean){
        val POSTDATA = HashMap<String, String>()

        GLOBAL.RECORD[0].R_END_COMMENT=str
        GLOBAL.RECORD[0].R_REFUEL=ref
        GLOBAL.RECORD[0].R_ENDDATE=getDate()
        GLOBAL.RECORD[0].R_ENDTIME=getTime()

        val buf_s_date=GLOBAL.RECORD[0].R_STARTDATE.replace("年","/").replace("月","/").replace("日","")
        val buf_s_time=GLOBAL.RECORD[0].R_STARTTIME.replace("時",":").replace("分","")
        val buf_e_date=GLOBAL.RECORD[0].R_ENDDATE.replace("年","/").replace("月","/").replace("日","")
        val buf_e_time=GLOBAL.RECORD[0].R_ENDTIME.replace("時",":").replace("分","")

        POSTDATA.put("rid", GLOBAL.RECORD[0].R_RID)
        POSTDATA.put("id", GLOBAL.RECORD[0].R_ID)
        POSTDATA.put("name", GLOBAL.RECORD[0].R_NAME)
        POSTDATA.put("s_date", buf_s_date)
        POSTDATA.put("s_time", buf_s_time)
        POSTDATA.put("e_date", buf_e_date)
        POSTDATA.put("e_time", buf_e_time)
        POSTDATA.put("park", GLOBAL.RECORD[0].R_PARK)
        POSTDATA.put("s_comment", GLOBAL.RECORD[0].R_START_COMMENT)
        POSTDATA.put("e_comment", GLOBAL.RECORD[0].R_END_COMMENT)
        POSTDATA.put("refuel", GLOBAL.RECORD[0].R_REFUEL.toString())
        POSTDATA.put("hash", CreateHash(GLOBAL.RECORD[0].R_ID+buf_s_date+buf_s_time+buf_e_date+buf_e_time))

        "https://myapp.tokyo/carreserv/change.php".httpPost(POSTDATA.toList()).response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    if(String(response.data).indexOf("SQL ERROR")!=-1){
                        mHandler.post(Runnable
                        {
                            Toast.makeText(applicationContext, "SQLエラー", Toast.LENGTH_SHORT).show()
                        })
                    }
                    else if(String(response.data).indexOf("HASH ERROR")!=-1){
                        mHandler.post(Runnable
                        {
                            Toast.makeText(applicationContext, "HASHエラー", Toast.LENGTH_SHORT).show()
                        })
                    }
                    else{
                        mHandler.post(Runnable
                        {
                            Toast.makeText(applicationContext, "返却しました", Toast.LENGTH_SHORT).show()
                            REFLESH()
                        })
                    }
                }
                is Result.Failure -> {
                    print(String(response.data))
                    mHandler.post(Runnable
                    {
                        Toast.makeText(applicationContext, "接続エラー", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }

    }


    //情報再読み込み関数
    fun REFLESH(){
        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("hash", CreateHash(SimpleDateFormat("yyyyMMddHHmm",Locale.getDefault()).format(Date())))
        "https://myapp.tokyo/carreserv/get.php".httpPost(POSTDATA.toList()).response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    if(String(response.data).indexOf("SQL ERROR")!=-1){
                        mHandler.post(Runnable
                        {
                            swipe_refresh.isRefreshing=false
                            Toast.makeText(applicationContext, "SQLエラー", Toast.LENGTH_SHORT).show()
                        })
                    }
                    else if(String(response.data).indexOf("HASH ERROR")!=-1){
                        mHandler.post(Runnable
                        {
                            swipe_refresh.isRefreshing=false
                            Toast.makeText(applicationContext, "HASHエラー", Toast.LENGTH_SHORT).show()
                        })
                    }
                    else{
                        mHandler.post(Runnable
                        {
                            SETRECORD(String(response.data))
                            setStatus()
                            swipe_refresh.isRefreshing=false
                            Toast.makeText(applicationContext, "更新しました", Toast.LENGTH_SHORT).show()
                        })
                    }
                }
                is Result.Failure -> {
                    mHandler.post(Runnable
                    {
                        Toast.makeText(applicationContext, "接続エラー", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }


    fun SETRECORD(str:String){
        GLOBAL.RECORD.clear()
        val scan= Scanner(str)
        scan.useDelimiter(",")
        while(scan.hasNext()) {
            val RID = scan.next()
            val ID = scan.next()
            val NAME = scan.next()
            val STARTDATE = scan.next().replaceFirst("-","年").replaceFirst("-","月")+"日"
            val STARTTIME = scan.next().replaceFirst(":","時").replaceFirst(":00","分")
            val ENDDATE = scan.next().replaceFirst("-","年").replaceFirst("-","月")+"日"
            val ENDTIME = scan.next().replaceFirst(":","時").replaceFirst(":00","分")
            val PARK = scan.next()
            val S_COMMENT = scan.next()
            val E_COMMENT = scan.next()
            val REFUEL = scan.next().toBoolean()
            GLOBAL.RECORD.add(
                MyApp.DC_RECORD(
                    RID,
                    ID,
                    NAME,
                    STARTDATE,
                    STARTTIME,
                    ENDDATE,
                    ENDTIME,
                    PARK,
                    S_COMMENT,
                    E_COMMENT,
                    REFUEL
                )
            )
        }
    }

    fun setStatus(){
        //未来に登録されたデータがあれば次回利用時間を表示
        if(GLOBAL.RECORD.size>0){

            while(GLOBAL.RECORD.size>0) {
                var S_str = GLOBAL.RECORD[0].R_STARTDATE + GLOBAL.RECORD[0].R_STARTTIME
                var S_date = S_str.split("年", "月", "日", "時", "分")
                var E_str = GLOBAL.RECORD[0].R_ENDDATE + GLOBAL.RECORD[0].R_ENDTIME
                var E_date = E_str.split("年", "月", "日", "時", "分")
                val S_calendar: Calendar =
                    Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
                val E_calendar: Calendar =
                    Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

                S_calendar.set(Calendar.YEAR, S_date[0].toInt())
                S_calendar.set(Calendar.MONTH, S_date[1].toInt() - 1)
                S_calendar.set(Calendar.DAY_OF_MONTH, S_date[2].toInt())
                S_calendar.set(Calendar.HOUR_OF_DAY, S_date[3].toInt())
                S_calendar.set(Calendar.MINUTE, S_date[4].toInt())

                E_calendar.set(Calendar.YEAR, E_date[0].toInt())
                E_calendar.set(Calendar.MONTH, E_date[1].toInt() - 1)
                E_calendar.set(Calendar.DAY_OF_MONTH, E_date[2].toInt())
                E_calendar.set(Calendar.HOUR_OF_DAY, E_date[3].toInt())
                E_calendar.set(Calendar.MINUTE, E_date[4].toInt())

                //開始時刻が今より未来であれば次回利用を表示
                val now = Calendar.getInstance()

                if (E_calendar.before(now)) {
                    GLOBAL.RECORD.removeAt(0)
                    mHandler.post(Runnable
                    {
                        strNext.setText("利用予約はありません")
                        strStatus.setText("未使用")
                        findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
                        btnPark.setVisibility(View.INVISIBLE)
                    })
                } else if (S_calendar.before(now)) {
                    mHandler.post(Runnable
                    {
                        strNext.setText("返却予定：" + GLOBAL.RECORD[0].R_ENDDATE + " " + GLOBAL.RECORD[0].R_ENDTIME)
                        strStatus.setText(GLOBAL.RECORD[0].R_NAME+"が使用中")
                        findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.use)
                        btnPark.setVisibility(View.INVISIBLE)
                        if(GLOBAL.RECORD[0].R_ID==getID()){
                            btnPark.setVisibility(View.VISIBLE)
                        }
                    })
                    break;
                } else {
                    mHandler.post(Runnable
                    {
                        strNext.setText("次回利用：" + GLOBAL.RECORD[0].R_STARTDATE + " " + GLOBAL.RECORD[0].R_STARTTIME)
                        strStatus.setText("未使用")
                        findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
                        btnPark.setVisibility(View.INVISIBLE)
                    })
                    break;
                }
            }
        }
        else{
            mHandler.post(Runnable
            {
                strStatus.setText("未使用")
                strNext.setText("利用予約はありません")
                findViewById<ImageView>(R.id.img_car).setImageResource(R.drawable.parking)
                btnPark.setVisibility(View.INVISIBLE)
            })

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

    fun CreateHash(str:String):String {
        var hash=str+"ROADSTAR"
        return MessageDigest.getInstance("SHA-256")
            .digest(hash.toByteArray())
            .joinToString(separator = "") {
                "%02x".format(it)
            }
    }

    fun getID():String{
        var ID:String= Build.ID
        if(ID.length>10){
            ID=ID.substring(0,9)
        }
        return ID
    }

    fun getDate():String{
        val date: Date = Date()
        val format= SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        return format.format(date)
    }

    fun getTime():String{
        val date= Calendar.getInstance()
        date.time= Date()
        val df= SimpleDateFormat("HH時mm分")
        return df.format(date.time)
    }


}