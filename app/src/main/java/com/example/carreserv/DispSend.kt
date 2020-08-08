package com.example.carreserv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_disp_send.*
import java.security.MessageDigest

class DispSend : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_send)
        SEND_DATA()
    }

    fun SEND_DATA(){

        val POSTDATA = HashMap<String, String>()

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


        "https://myapp.tokyo/carreserv/register.php".httpPost(POSTDATA.toList()).response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    val str=String(response.data)
                    if(str.indexOf("Query OK")!=-1){
                        GLOBAL.RESPONSE_STATE=1
                        finish()
                    }
                    else{
                        GLOBAL.RESPONSE_STATE=2
                        finish()
                    }
                }
                is Result.Failure -> {
                    GLOBAL.RESPONSE_STATE=3
                    finish()
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

}