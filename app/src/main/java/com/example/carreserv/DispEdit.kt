package com.example.carreserv

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_disp_edit.*
import java.util.*

class DispEdit : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()


    //起動時動作
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_StartDate.setOnClickListener{showDatePicker(btn_StartDate,btn_EndDate)}
        btn_StartTime.setOnClickListener{showTimePicker(btn_StartTime,btn_EndTime)}
        btn_EndDate.setOnClickListener{showDatePicker(btn_EndDate)}
        btn_EndTime.setOnClickListener{showTimePicker(btn_EndTime)}
        btnPark.setOnClickListener{ CreateDialog() }
        btnEdit.setOnClickListener{}
        val num:Int=intent.getIntExtra("num",0)
        setData(num)
    }
    //メニューボタン生成
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
            R.id.menu_delete->{

            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun setData(num:Int){
        btn_StartDate.setText(GLOBAL.RECORD[num].R_STARTDATE)
        btn_StartTime.setText(GLOBAL.RECORD[num].R_STARTTIME)
        btn_EndDate.setText(GLOBAL.RECORD[num].R_ENDDATE)
        btn_EndTime.setText(GLOBAL.RECORD[num].R_ENDTIME)
        btnPark.setText(GLOBAL.RECORD[num].R_PARK)
        strComment.setText(GLOBAL.RECORD[num].R_START_COMMENT)
    }
    fun CreateDialog(){
        val List=arrayOf("東比恵","大濠","薬院","学校","その他")
        AlertDialog.Builder(this)
            .setTitle("通知間隔を設定してください")
            .setItems(List,{dialog,which->btnPark.setText(List[which])}).show()
    }
    fun showTimePicker(r1: Button, r2: Button){
        var str=""
        var btn=r1.getText().toString()
        var time=btn.split("時","分")

        val C_Hour=time[0].toInt()
        val C_Minuts=time[1].toInt()

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener() { view, hour, minutes->
                str="${hour}時${minutes}分"
                r1.setText(str)
                str="${hour+3}時${minutes}分"
                r2.setText(str)
            },
            C_Hour,
            C_Minuts,
            true)
        timePickerDialog.setCanceledOnTouchOutside(false)
        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
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
        timePickerDialog.setCanceledOnTouchOutside(false)
        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
    }
    fun showDatePicker(r: Button){
        var str=""
        var btn=r.getText().toString()
        var time=btn.split("年","月","日")

        var C_Year=time[0].toInt()
        var C_Month=time[1].toInt()
        var C_Day=time[2].toInt()

        val minDate = Calendar.getInstance()
        minDate.set(C_Year,C_Month,C_Day)


        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth->
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
        var str=""
        var btn=r1.getText().toString()
        var time=btn.split("年","月","日")

        var C_Year=time[0].toInt()
        var C_Month=time[1].toInt()
        var C_Day=time[2].toInt()

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth->
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
}