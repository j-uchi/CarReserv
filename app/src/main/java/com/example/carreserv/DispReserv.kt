package com.example.carreserv

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_reserv)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_StartDate.setText(getDate())
        btn_StartTime.setText(getTime(0))
        btn_EndDate.setText(getDate())
        btn_EndTime.setText(getTime(6))

        btn_StartDate.setOnClickListener{showDatePicker(btn_StartDate)}
        btn_StartTime.setOnClickListener{showTimePicker(btn_StartTime)}
        btn_EndDate.setOnClickListener{showDatePicker(btn_EndDate)}
        btn_EndTime.setOnClickListener{showTimePicker(btn_EndTime)}
        btnPark.setOnClickListener{ CreateDialog() }
        btnReserv.setOnClickListener{PushData()}

    }

    fun PushData(){
        val R_ID=Build.BRAND
        val R_START_DATE=btn_StartDate.getText().toString()
        val R_START_TIME=btn_StartTime.getText().toString()
        val R_END_DATE=btn_EndDate.getText().toString()
        val R_END_TIME=btn_EndTime.getText().toString()
        val R_PARK=btnPark.getText().toString()
        val R_COMMENT=""+strComment.getText().toString()//NULL対策
    }



    fun showDatePicker(r: Button){
        var str=""
        var btn=r.getText().toString()
        var time=btn.split("年","月","日")

        var C_Year=time[0].toInt()
        var C_Month=time[1].toInt()
        var C_Day=time[2].toInt()

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() {view, year, month, dayOfMonth->
                str="${year}年${month+1}月${dayOfMonth}日"
                r.setText(str)
            },
            C_Year,
            C_Month-1,
            C_Day)
        datePickerDialog.show()
    }

    fun showTimePicker(r: Button){
        var str=""
        var btn=r.getText().toString()
        var time=btn.split("時","分")

        val C_Hour=time[0].toInt()
        val C_Minuts=time[1].toInt()

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener() {view, hour, minutes->
                str="${hour}時${minutes}分"
                r.setText(str)
            },
            C_Hour,
            C_Minuts,
            true)
        timePickerDialog.show()
    }

    fun CreateDialog(){
        val List=arrayOf("東比恵","大濠","薬院","学校","その他")
        AlertDialog.Builder(this)
            .setTitle("通知間隔を設定してください")
            .setItems(List,{dialog,which->SetPark(List[which])}).show()
    }

    fun SetPark(str:String){
        btnPark.setText(str)
    }

    fun getDate():String{
        val date: Date = Date()
        val format= SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        return format.format(date)
    }

    fun getTime(i:Int):String{
        val date= Calendar.getInstance()
        date.time= Date()
        date.add(Calendar.HOUR,i)
        val df= SimpleDateFormat("HH時mm分")
        return df.format(date.time)
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