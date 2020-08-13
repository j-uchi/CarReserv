package com.example.carreserv

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.*

class DispCalendar : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()
    var NowOpenRecordDay:String=""

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

    //再アクティブ時動作
    override fun onRestart() {
        super.onRestart()
        if(NowOpenRecordDay!=""){CreateRecordList(NowOpenRecordDay)}
    }


    fun CreateRecordList(cal:String){
        val SV=findViewById<View>(R.id.ScrollView)as ViewGroup
        SV.removeAllViews()
        var count=0;
        for(i in GLOBAL.RECORD.indices){
            if(GLOBAL.RECORD[i].R_STARTDATE==cal){
                getLayoutInflater().inflate(R.layout.recordlist,SV)
                val layout=SV.getChildAt(count)as ConstraintLayout
                count++
                val text=GLOBAL.RECORD[i].R_STARTTIME+"　～　"+GLOBAL.RECORD[i].R_ENDTIME
                layout.setTag(i)
                ((layout.getChildAt(0)as TextView).setText(text))
                //もし過去のデータであれば色を薄いグレーに設定
                if(isBefore(i,false)){
                    ((layout.getChildAt(0)as TextView).setTextColor(Color.parseColor("#D2D2D2")))
                    if(GLOBAL.RECORD[i].R_ID==GLOBAL.userID){
                        ((layout.getChildAt(1)as ImageView).setImageResource(R.drawable.ic_me_before))
                    }
                    else{
                        ((layout.getChildAt(1)as ImageView).setImageResource(R.drawable.ic_people_before))
                    }
                }
                else{
                    ((layout.getChildAt(0)as TextView).setTextColor(Color.parseColor("#808080")))
                    if(GLOBAL.RECORD[i].R_ID==GLOBAL.userID){
                        ((layout.getChildAt(1)as ImageView).setImageResource(R.drawable.ic_me_after))
                    }
                    else{
                        ((layout.getChildAt(1)as ImageView).setImageResource(R.drawable.ic_people_after))
                    }
                }
                layout.setOnClickListener{
                    SelectRecord(it.getTag().toString().toInt())
                }
            }//if end
        }//for end
        NowOpenRecordDay=cal
    }

    fun SelectRecord(num:Int){
        //現在時刻より過去であれば参照ダイアログを表示
        if(isBefore(num,true)){
            CreateDialog_Past(num)
        }
        //未来の予定であれば削除や編集ボタンを含めたダイアログを表示
        else{
            //且つ、自分以外であれば参照ダイアログのみの表示
            if(GLOBAL.RECORD[num].R_ID!=GLOBAL.userID){
                CreateDialog_Past(num)
            }
            else{
                CreateDialog_Future(num)
            }
        }
    }

    fun isBefore(i:Int,startflg:Boolean):Boolean{
        val now=Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)
        if(startflg){
            var S_str = GLOBAL.RECORD[i].R_STARTDATE + GLOBAL.RECORD[i].R_STARTTIME
            var S_date = S_str.split("年", "月", "日", "時", "分")

            val S_calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

            S_calendar.set(Calendar.YEAR, S_date[0].toInt())
            S_calendar.set(Calendar.MONTH, S_date[1].toInt() - 1)
            S_calendar.set(Calendar.DAY_OF_MONTH, S_date[2].toInt())
            S_calendar.set(Calendar.HOUR_OF_DAY, S_date[3].toInt())
            S_calendar.set(Calendar.MINUTE, S_date[4].toInt())

            if(S_calendar.before(now)){
                return true
            }
            else return S_calendar.equals(now)
        }
        else{
            var E_str = GLOBAL.RECORD[i].R_ENDDATE + GLOBAL.RECORD[i].R_ENDTIME
            var E_date = E_str.split("年", "月", "日", "時", "分")

            val E_calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN)

            E_calendar.set(Calendar.YEAR, E_date[0].toInt())
            E_calendar.set(Calendar.MONTH, E_date[1].toInt() - 1)
            E_calendar.set(Calendar.DAY_OF_MONTH, E_date[2].toInt())
            E_calendar.set(Calendar.HOUR_OF_DAY, E_date[3].toInt())
            E_calendar.set(Calendar.MINUTE, E_date[4].toInt())

            if(E_calendar.before(now)){
                return true
            }
            else return E_calendar.equals(now)
        }

    }

    fun CreateDialog_Past(num :Int){
        var fuel:String="（ 未給油 ）"
        if(GLOBAL.RECORD[num].R_REFUEL){
            fuel="（ 給油済 ）"
        }
        AlertDialog.Builder(this)
            .setTitle("利用履歴")
            .setMessage("\n名前 : "+GLOBAL.RECORD[num].R_NAME +"\n\n"+
                    "\n開始 : "+GLOBAL.RECORD[num].R_STARTDATE+"　"+GLOBAL.RECORD[num].R_STARTTIME+"\n"+
                    "返却 : "+GLOBAL.RECORD[num].R_ENDDATE+"　"+GLOBAL.RECORD[num].R_ENDTIME+"\n\n" +
                    "返却場所　:　"+GLOBAL.RECORD[num].R_PARK+"　　　"+fuel+"\n\n\n" +
                    "開始時コメント　:　"+GLOBAL.RECORD[num].R_START_COMMENT+"\n\n\n" +
                    "返却時コメント　:　"+GLOBAL.RECORD[num].R_START_COMMENT)
            //.setPositiveButton("OK"){dialog,which->}
            .create()
            .show()
    }

    fun CreateDialog_Future(num:Int){
        AlertDialog.Builder(this)
            .setTitle("利用予定")
            .setMessage("\n名前 : "+GLOBAL.RECORD[num].R_NAME +"\n\n"+
                    "開始 : "+GLOBAL.RECORD[num].R_STARTDATE+"　"+GLOBAL.RECORD[num].R_STARTTIME+"\n"+
                    "返却 : "+GLOBAL.RECORD[num].R_ENDDATE+"　"+GLOBAL.RECORD[num].R_ENDTIME+"\n\n" +
                    "開始時コメント:"+GLOBAL.RECORD[num].R_START_COMMENT)
            .setPositiveButton("編集",{dialog,which->
                intent= Intent(this,DispEdit::class.java)
                intent.putExtra("num",num)
                startActivity(intent)
            })
            .setNegativeButton("キャンセル", { dialog, which ->})
            .create()
            .show()
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