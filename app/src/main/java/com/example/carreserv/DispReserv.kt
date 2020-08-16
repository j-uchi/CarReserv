package com.example.carreserv

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_disp_reserv.*
import java.text.SimpleDateFormat
import java.util.*

class DispReserv : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_reserv)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_StartDate.setText(getDate())
        btn_StartTime.setText(getTime(0))
        btn_EndDate.setText(getDate())
        btn_EndTime.setText(getTime(3))

        btn_StartDate.setOnClickListener{showDatePicker(btn_StartDate,btn_EndDate)}
        btn_StartTime.setOnClickListener{showTimePicker(btn_StartTime,btn_EndTime)}
        btn_EndDate.setOnClickListener{showDatePicker(btn_EndDate)}
        btn_EndTime.setOnClickListener{showTimePicker(btn_EndTime)}
        btnPark.setOnClickListener{ CreateDialog() }
        btnEdit.setOnClickListener{PushData()}

    }

    fun PushData(){
        val R_RID="0"
        val R_ID=GLOBAL.userID
        val R_NAME=GLOBAL.NAME
        val R_START_DATE=btn_StartDate.getText().toString().replace("年","/").replace("月","/").replace("日","")
        val R_START_TIME=btn_StartTime.getText().toString().replace("時",":").replace("分","")
        val R_END_DATE=btn_EndDate.getText().toString().replace("年","/").replace("月","/").replace("日","")
        val R_END_TIME=btn_EndTime.getText().toString().replace("時",":").replace("分","")
        val R_PARK=btnPark.getText().toString()
        val R_COMMENT=""+strComment.getText().toString()//NULL対策
        GLOBAL.SEND_RECORD= MyApp.DC_RECORD(R_RID,R_ID,R_NAME,R_START_DATE,R_START_TIME,R_END_DATE,R_END_TIME,R_PARK,R_COMMENT,"",null)
        startActivity(Intent(this,DispSend::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }



    fun showDatePicker(r: Button){
        var str: String
        var btn=r.getText().toString()
        var time=btn.split("年","月","日")

        var C_Year=time[0].toInt()
        var C_Month=time[1].toInt()
        var C_Day=time[2].toInt()

        val minDate = Calendar.getInstance()
        minDate.set(C_Year,C_Month,C_Day)


        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() { _, year, month, dayOfMonth->
                str="${year}年${month+1}月${dayOfMonth}日"
                r.setText(str)
            },
            C_Year,
            C_Month-1,
            C_Day)
        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
    }
    fun showDatePicker(r1: Button,r2:Button){
        var str: String
        var btn=r1.getText().toString()
        var time=btn.split("年","月","日")

        var C_Year=time[0].toInt()
        var C_Month=time[1].toInt()
        var C_Day=time[2].toInt()

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() { _, year, month, dayOfMonth->
                str="${year}年${month+1}月${dayOfMonth}日"
                r1.setText(str)
                r2.setText(str)
            },
            C_Year,
            C_Month-1,
            C_Day)
        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
    }

    fun showTimePicker(r: Button){
        var str: String
        var btn=r.getText().toString()
        var time=btn.split("時","分")

        val C_Hour=time[0].toInt()
        val C_Minuts=time[1].toInt()

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener() { _, hour, minutes->
                str="${hour}時${minutes}分"
                r.setText(str)
            },
            C_Hour,
            C_Minuts,
            true)
        timePickerDialog.setCanceledOnTouchOutside(false)
        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
    }
    fun showTimePicker(r1: Button,r2: Button){
        var str: String
        var btn=r1.getText().toString()
        var time=btn.split("時","分")

        val C_Hour=time[0].toInt()
        val C_Minuts=time[1].toInt()

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener() { _, hour, minutes->
                if(hour>=21){
                    str="${hour}時${minutes}分"
                    r1.setText(str)
                    str="23時59分"
                    r2.setText(str)
                }
                else{
                    str="${hour}時${minutes}分"
                    r1.setText(str)
                    str="${hour+3}時${minutes}分"
                    r2.setText(str)
                }
            },
            C_Hour,
            C_Minuts,
            true)
        timePickerDialog.setCanceledOnTouchOutside(false)
        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
    }

    fun CreateDialog(){
        val List=arrayOf("東比恵","大濠","薬院","学校","その他")
        AlertDialog.Builder(this)
            .setTitle("返却場所を選択してください")
            .setItems(List) { _, which-> btnPark.text = List[which] }.show()
    }

    fun getDate():String{
        val date: Date = Date()
        val format= SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        return format.format(date)
    }

    fun getTime(i:Int):String{
        val date= Calendar.getInstance()
        date.time= Date()
        if(date.get(Calendar.HOUR_OF_DAY)+i>=24){
            date.add(Calendar.HOUR,23-date.get(Calendar.HOUR_OF_DAY))
            date.add(Calendar.MINUTE,59-date.get(Calendar.MINUTE))
        }
        else{
            date.add(Calendar.HOUR,i)
        }
        val df= SimpleDateFormat("HH時mm分")
        return df.format(date.time)
    }
    //左上戻るボタンのリスナー設定
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }



}
