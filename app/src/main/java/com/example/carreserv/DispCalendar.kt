package com.example.carreserv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_disp_calendar.*
import java.text.SimpleDateFormat
import java.util.*

class DispCalendar : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_calendar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //過去の選択可能日を1か月前に設定
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        calendarView.minDate = calendar.timeInMillis

        //リストを表示する
        CreateRecordList(getDateString());

        // 日付変更イベントを追加
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            CreateRecordList(getDateString(year,month,dayOfMonth))
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

    fun CreateRecordList(cal:String){
        val layout=findViewById<View>(R.id.ScrollView)as ViewGroup
        layout.removeAllViews()

        for(i in GLOBAL.RECORD.indices){
            if(GLOBAL.RECORD[i].R_STARTDATE==cal){
                val text=TextView(this)
                val space= Space(this)
                text.text=GLOBAL.RECORD[i].R_STARTTIME+"　～　"+GLOBAL.RECORD[i].R_ENDTIME
                text.gravity= Gravity.CENTER
                text.textSize=30F
                text.setTag(i)
                layout.addView(space,LinearLayout.LayoutParams(50,50))
                layout.addView(text)
                text.setOnClickListener{
                    Toast.makeText(applicationContext, "tap is$i", Toast.LENGTH_SHORT).show()
                    SelectRecord(it.getTag().toString().toInt())
                }
            }//if end
        }//for end
    }

    fun SelectRecord(num:Int){
        val now=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
        var S_str = GLOBAL.RECORD[num].R_STARTDATE + GLOBAL.RECORD[num].R_STARTTIME
        var S_date = S_str.split("年", "月", "日", "時", "分")

        val S_calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

        S_calendar.set(Calendar.YEAR, S_date[0].toInt())
        S_calendar.set(Calendar.MONTH, S_date[1].toInt() - 1)
        S_calendar.set(Calendar.DAY_OF_MONTH, S_date[2].toInt())
        S_calendar.set(Calendar.HOUR_OF_DAY, S_date[3].toInt())
        S_calendar.set(Calendar.MINUTE, S_date[4].toInt())

        //現在時刻より過去であれば参照ダイアログを表示
        if(S_calendar.before(now)||S_calendar.equals(now)){
            CreateDialog_Past(num)
        }
        //未来の予定であれば削除や編集ボタンを含めたダイアログを表示
        else{

        }
    }

    fun CreateDialog_Past(num :Int){
        var fuel:String="（ 未給油 ）"
        if(GLOBAL.RECORD[num].R_REFUEL){
            fuel="（ 給油済 ）"
        }
        AlertDialog.Builder(this)
            .setTitle("利用者 : "+GLOBAL.RECORD[num].R_NAME)
            .setMessage("\n開始 : "+GLOBAL.RECORD[num].R_STARTDATE+"　"+GLOBAL.RECORD[num].R_STARTTIME+"\n"+
                    "返却 : "+GLOBAL.RECORD[num].R_ENDDATE+"　"+GLOBAL.RECORD[num].R_ENDTIME+"\n\n" +
                    "返却場所:"+GLOBAL.RECORD[num].R_PARK+"　　　"+fuel+"\n\n\n" +
                    "開始時コメント:"+GLOBAL.RECORD[num].R_START_COMMENT+"\n\n\n" +
                    "返却時コメント:"+GLOBAL.RECORD[num].R_START_COMMENT)
            //.setPositiveButton("OK"){dialog,which->}
            .create()
            .show()
    }

    fun CreateDialog_Future(){

    }

    fun getDateString(year:Int,month:Int,day:Int):String{
        var calendar=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
        calendar.set(Calendar.YEAR,year)
        calendar.set(Calendar.MONTH,month)
        calendar.set(Calendar.DAY_OF_MONTH,day)
        var day=SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime())
        return day
    }
    fun getDateString():String{
        var day=SimpleDateFormat("yyyy年MM月dd日").format(Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN).getTime())
        return day
    }
}