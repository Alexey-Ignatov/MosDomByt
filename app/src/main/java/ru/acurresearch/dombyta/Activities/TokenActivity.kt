package ru.acurresearch.dombyta.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_token.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.acurresearch.dombyta.*
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.R
import ru.evotor.framework.navigation.NavigationApi

class TokenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        App.prefs.cashBoxServerData = CashBoxServerData("0d96c1f1-e454-4339-bf1b-434e628559be", "aa")
        finish()
        //receiveToken()
        send_token_btn.setOnClickListener {
            receiveToken()
        }

    }


    fun receiveToken(){

        fun onSuccess(resp_data: CashBoxServerData){
            App.prefs.cashBoxServerData = resp_data
            finish()
        }

        val call = App.api.getToken(SiteToken("a"))
        call.enqueue(object : Callback<CashBoxServerData> {
            override fun onResponse(call: Call<CashBoxServerData>, response: Response<CashBoxServerData>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    onSuccess(response.body()!!)
                else
                    Toast.makeText(getApplicationContext(),"Неверный токен.", Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<CashBoxServerData>, t: Throwable) {
                Toast.makeText(getApplicationContext(),"ВНИМАНИЕ! Не удалось получить токен. Проверьте подключение к Интернету!", Toast.LENGTH_LONG).show()
            }
        })

    }
}
