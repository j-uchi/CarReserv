package com.example.carreserv

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_disp_edit.*
import kotlinx.android.synthetic.main.activity_disp_edit.btnEdit
import kotlinx.android.synthetic.main.activity_disp_edit.btnPark
import kotlinx.android.synthetic.main.activity_disp_edit.btn_EndDate
import kotlinx.android.synthetic.main.activity_disp_edit.btn_EndTime
import kotlinx.android.synthetic.main.activity_disp_edit.btn_StartDate
import kotlinx.android.synthetic.main.activity_disp_edit.btn_StartTime
import kotlinx.android.synthetic.main.activity_disp_edit.strComment
import kotlinx.android.synthetic.main.activity_disp_reserv.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class DispEdit : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()
    var mHandler= Handler()
    var SelectNum=0


    //起動時動作
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_StartDate.setOnClickListener{showDatePicker(btn_StartDate,btn_EndDate)}
        btn_StartTime.setOnClickListener{showTimePicker(btn_StartTime)}
        btn_EndDate.setOnClickListener{showDatePicker(btn_EndDate)}
        btn_EndTime.setOnClickListener{showTimePicker(btn_EndTime)}
        btnPark.setOnClickListener{ CreateDialog() }
        btnEdit.setOnClickListener{ UpdateRecord()}
        SelectNum=intent.getIntExtra("num",0)
        setData(SelectNum)
    }
    //メニューボタン生成
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
            R.id.menu_delete->{
                CreateDeleteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun CreateDeleteDialog(){
        AlertDialog.Builder(this) // FragmentではActivityを取得して生成
            .setTitle("この予定を削除しますか?")
            .setPositiveButton("削除") { _, _ ->
                var n=0;
                DeleteRecord(n)
            }
            .setNegativeButton("cancel") { _, _ ->
            }
            .show()
    }

    fun UpdateRecord(){
        val R_RID=GLOBAL.RECORD[SelectNum].R_RID
        val R_ID=GLOBAL.userID
        val R_NAME=GLOBAL.NAME
        val R_START_DATE=btn_StartDate.getText().toString().replace("年","/").replace("月","/").replace("日","")
        val R_START_TIME=btn_StartTime.getText().toString().replace("時",":").replace("分","")
        val R_END_DATE=btn_EndDate.getText().toString().replace("年","/").replace("月","/").replace("日","")
        val R_END_TIME=btn_EndTime.getText().toString().replace("時",":").replace("分","")
        val R_PARK=btnPark.getText().toString()
        val R_COMMENT=""+strComment.getText().toString().replace(",","，")//NULL対策
        GLOBAL.SEND_RECORD= MyApp.DC_RECORD(R_RID,R_ID,R_NAME,R_START_DATE,R_START_TIME,R_END_DATE,R_END_TIME,R_PARK,R_COMMENT,"",null)
        startActivity(Intent(this,DispUpdate::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    fun DeleteRecord(n:Int){
        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("rid", GLOBAL.RECORD[SelectNum].R_RID)
        POSTDATA.put("hash", CreateHash(SimpleDateFormat("yyyyMMddHHmm",Locale.getDefault()).format(Date())))
        "https://myapp.tokyo/carreserv/delete.php".httpPost(POSTDATA.toList()).response { _, response, result ->
            when (result) {
                is Result.Success -> {
                    if(String(response.data).indexOf("SQL ERROR")!=-1){
                        mHandler.post(Runnable
                        {
                            if(n<3) DeleteRecord(n+1)
                            else Toast.makeText(applicationContext, "SQLエラー", Toast.LENGTH_SHORT).show()
                        })
                    }
                    else if(String(response.data).indexOf("HASH ERROR")!=-1){
                        mHandler.post(Runnable
                        {
                            if(n<3) DeleteRecord(n+1)
                            else Toast.makeText(applicationContext, "HASHエラー", Toast.LENGTH_SHORT).show()
                        })
                    }
                    else{
                        mHandler.post(Runnable
                        {
                            GLOBAL.RECORD.removeAt(SelectNum)
                            Toast.makeText(applicationContext, "削除しました", Toast.LENGTH_SHORT).show()
                            finish()
                        })
                    }
                }
                is Result.Failure -> {
                    mHandler.post(Runnable
                    {
                        if(n<3) DeleteRecord(n+1)
                        else Toast.makeText(applicationContext, "接続エラー", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
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
        val List=arrayOf("東比恵","大濠","薬院","学校","屋形原")
        AlertDialog.Builder(this)
            .setTitle("返却場所を選択してください")
            .setItems(List) { _, which-> btnPark.text = List[which] }
            .setPositiveButton("その他") { dialog, which->
                //その他時処理記載\
                val myedit= EditText(this)
                AlertDialog.Builder(this)
                    .setTitle("返却場所を入力してください")
                    .setView(myedit)
                    .setPositiveButton("OK") { dialog, which->
                        btnPark.text=myedit.getText().toString()
                    }.show()
            }
            .show()
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
    fun CreateHash(str:String):String {
        var hash=str+"ROADSTAR"
        return MessageDigest.getInstance("SHA-256")
            .digest(hash.toByteArray())
            .joinToString(separator = "") {
                "%02x".format(it)
            }
    }
}