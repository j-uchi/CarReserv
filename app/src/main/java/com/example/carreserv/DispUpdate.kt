package com.example.carreserv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DispUpdate : AppCompatActivity() {
    val GLOBAL=MyApp.getInstance()
    var mHandler= Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_update)
        SEND_DATA()
    }

    fun SEND_DATA(){

        val POSTDATA = HashMap<String, String>()
        POSTDATA.put("rid", GLOBAL.SEND_RECORD.R_RID)
        POSTDATA.put("id", GLOBAL.SEND_RECORD.R_ID)
        POSTDATA.put("name", GLOBAL.SEND_RECORD.R_NAME)
        POSTDATA.put("s_date", GLOBAL.SEND_RECORD.R_STARTDATE)
        POSTDATA.put("s_time", GLOBAL.SEND_RECORD.R_STARTTIME)
        POSTDATA.put("e_date", GLOBAL.SEND_RECORD.R_ENDDATE)
        POSTDATA.put("e_time", GLOBAL.SEND_RECORD.R_ENDTIME)
        POSTDATA.put("park", GLOBAL.SEND_RECORD.R_PARK)
        POSTDATA.put("s_comment", GLOBAL.SEND_RECORD.R_START_COMMENT)
        POSTDATA.put("e_comment", GLOBAL.SEND_RECORD.R_END_COMMENT)
        POSTDATA.put("refuel", GLOBAL.SEND_RECORD.R_REFUEL.toString())
        POSTDATA.put("hash", CreateHash(GLOBAL.SEND_RECORD.R_ID,GLOBAL.SEND_RECORD.R_STARTDATE,GLOBAL.SEND_RECORD.R_STARTTIME,GLOBAL.SEND_RECORD.R_ENDDATE,GLOBAL.SEND_RECORD.R_ENDTIME))

        "https://myapp.tokyo/carreserv/change.php".httpPost(POSTDATA.toList()).response { _, response, result ->
            when (result) {
                is Result.Success -> {
                    if(String(response.data).indexOf("Query OK")!=-1){
                        mHandler.post(Runnable
                        {
                            SEND_Notification(0,"予約変更",GLOBAL.SEND_RECORD.R_NAME+"さんが "+GLOBAL.SEND_RECORD.R_STARTDATE+"　"+GLOBAL.SEND_RECORD.R_STARTTIME+" 開始の予約を変更しました")
                            finish()
                        })
                    }
                    else{
                        mHandler.post(Runnable
                        {
                            Toast.makeText(applicationContext, "SQLエラー", Toast.LENGTH_SHORT).show()
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

    fun SEND_Notification(n:Int,title:String,body:String){
        val POSTDATA = HashMap<String, String>()

        POSTDATA.put("title", title)
        POSTDATA.put("body", body)
        POSTDATA.put("hash", CreateHash(SimpleDateFormat("yyyyMMddHHmm",Locale.getDefault()).format(Date())))

        "https://myapp.tokyo/carreserv/notification.php".httpPost(POSTDATA.toList()).response { _, response, result ->
            when (result) {
                is Result.Success -> {
                    mHandler.post(Runnable
                    {
                        REFLESH()
                        Toast.makeText(applicationContext, "登録しました", Toast.LENGTH_SHORT).show()
                        finish()
                    })
                }
                is Result.Failure -> {
                    mHandler.post(Runnable
                    {
                        if(n<3) SEND_Notification(n+1,title,body)
                        else {
                            Toast.makeText(applicationContext, "接続エラー", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
                }
            }
        }
    }

    fun REFLESH(){
        val POSTDATA = java.util.HashMap<String, String>()
        POSTDATA.put("hash", CreateHash(SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(Date())))
        "https://myapp.tokyo/carreserv/get.php".httpPost(POSTDATA.toList()).response { _, response, result ->
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
                            MainActivity().SETRECORD(String(response.data))
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

    fun CreateHash(ID:String,S_D:String,S_T:String,E_D:String,E_T:String):String {
        var str: String = ID + S_D + S_T + E_D + E_T + "ROADSTAR"
        return MessageDigest.getInstance("SHA-256")
            .digest(str.toByteArray())
            .joinToString(separator = "") {
                "%02x".format(it)
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

}