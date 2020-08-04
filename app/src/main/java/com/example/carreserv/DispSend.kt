package com.example.carreserv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

class DispSend : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disp_send)
        test()
    }

    fun test(){
        val debuggerline:String?

        val test:Int?

        println("通信開始")
        "https://www.google.co.jp".httpGet().response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    // レスポンスボディを表示
                    println("非同期処理の結果：" + String(response.data))
                }
                is Result.Failure -> {
                    println("通信に失敗しました。")
                }
            }
        }

    }

}