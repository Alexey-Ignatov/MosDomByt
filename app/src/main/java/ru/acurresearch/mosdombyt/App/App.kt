package ru.acurresearch.mosdombyt.App

import android.app.Application


import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.acurresearch.mosdombyt.*
import java.util.*
import kotlin.collections.ArrayList

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)


class Prefs(context: Context) {
    val prefs = context.getSharedPreferences(Constants.SHARED_FILE_PATH,0)
    val emptyCheck = Check("", Date(), "", listOf())

    val emptyCheckStr = GsonBuilder().create().toJson(emptyCheck)
    val emptyStrListStr = GsonBuilder().create().toJson(listOf<String>())
    val emptyProductItemCustom = GsonBuilder().create().toJson(ServiceItemCustom("", "",0.0, 1000000))


    var currCheck: Check
        get(){
            val strRes = prefs.getString("currCheckUUID", emptyCheckStr)
            return Gson().fromJson(strRes, Check::class.java)
        }
        set(value) = prefs.edit().putString("currCheckUUID", GsonBuilder().create().toJson(value) ).apply()


    var currPhone: String
        get() = prefs.getString(Constants.PREFS_CURR_PHONE, "")
        set(value) = prefs.edit().putString(Constants.PREFS_CURR_PHONE,value).apply()

    var lastOrder: Order
        get(){
            val resStr = prefs.getString(Constants.PREFS_LAST_ORDER, Order.empty().toJson())
            return  Order.fromJson(resStr)
        }
        set(value) {
            prefs.edit().putString(Constants.PREFS_LAST_ORDER, value.toJson()).apply()
        }


    var currName: String
        get() = prefs.getString(Constants.PREFS_CURR_NAME, "")
        set(value) = prefs.edit().putString(Constants.PREFS_CURR_NAME,value).apply()


    var selectedPositions: List<OrderPostition>
        get(){
            val resStr = prefs.getString(Constants.PREFS_SELECTED_POSITIONS, emptyStrListStr)
            return  Gson().fromJson<List<OrderPostition>>(resStr)
        }
        set(value){
            prefs.edit().putString(Constants.PREFS_SELECTED_POSITIONS, GsonBuilder().create().toJson(value)).apply()
        }

    var allAllowedProducts: List<ServiceItemCustom>
        get(){
            val resStr = prefs.getString(Constants.PREFS_ALL_ALLOWED_PRODUCTS, emptyStrListStr)
            return  Gson().fromJson<List<ServiceItemCustom>>(resStr)
        }
        set(value){
            prefs.edit().putString(Constants.PREFS_ALL_ALLOWED_PRODUCTS, GsonBuilder().create().toJson(value)).apply()
        }

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
