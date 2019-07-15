package ru.acurresearch.mosdombyt

import android.app.Application


import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)


class Prefs(context: Context) {
    val prefs = context.getSharedPreferences(Constants.SHARED_FILE_PATH,0)
    val emptyCheck = Check( "", Date(),"", listOf() )

    val emptyCheckStr = GsonBuilder().create().toJson(emptyCheck)


    var currCheck: Check
        get(){
            val strRes = prefs.getString("currCheckUUID", emptyCheckStr)
            return Gson().fromJson(strRes, Check::class.java)
        }
        set(value) = prefs.edit().putString("currCheckUUID", GsonBuilder().create().toJson(value) ).apply()


    var currPhone: String
    get() = prefs.getString(Constants.PREFS_CURR_PHONE, "")
    set(value) = prefs.edit().putString(Constants.PREFS_CURR_PHONE,value).apply()


    var currName: String
        get() = prefs.getString(Constants.PREFS_CURR_NAME, "")
        set(value) = prefs.edit().putString(Constants.PREFS_CURR_NAME,value).apply()


}




class App : Application() {
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        api = ApiProvider.provide()

    }

    companion object {
        lateinit var prefs: Prefs
        lateinit var api: Api

    }
}
