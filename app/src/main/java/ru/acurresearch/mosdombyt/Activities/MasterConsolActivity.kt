package ru.acurresearch.mosdombyt.Activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import ru.acurresearch.mosdombyt.Adapters.SectionsPageAdapter
import kotlinx.android.synthetic.main.activity_master_consol.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.acurresearch.mosdombyt.Adapters.NewTaskListAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.Fragments.CompleteTasksFragment
import ru.acurresearch.mosdombyt.Fragments.InWorkTasksFragment
import ru.acurresearch.mosdombyt.Fragments.NewTasksFragment
import ru.acurresearch.mosdombyt.Master
import ru.acurresearch.mosdombyt.ServiceItemCustom
import ru.acurresearch.mosdombyt.Task
//TODO прилепить фетчинг новых товаров к апдейту таворов по эвоторовской базе
//TODO Ускорить, обновляеться все очень медленно
//TODO добавить проверку на пустоту имени и телефона, иначе сервер упадет!!!

class MasterConsolActivity : AppCompatActivity() {

    var newTasksItems = ArrayList<Task>(listOf())
    var inWorkTasksItems = ArrayList<Task>(listOf())
    var completeTasksItems = ArrayList<Task>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.acurresearch.mosdombyt.R.layout.activity_master_consol)

        fetchAndRebuildTasks()
        fetchMasters()

    }
    fun fetchMasters(){
        fun onSuccess(resp_data: List<Master>){
            App.prefs.allMasters =  resp_data
        }

        val call = App.api.fetchMasters()
        call.enqueue(object : Callback<List<Master>> {
            override fun onResponse(call: Call<List<Master>>, response: Response<List<Master>>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    if (response.isSuccessful)
                        onSuccess(response.body()!!)
                    else
                        Log.e("sendPhone", "Sorry, failure on request "+ response.errorBody())
            }
            override fun onFailure(call: Call<List<Master>>, t: Throwable) {
                Log.e("sendPhone", "Sorry, unable to make request", t)
                Toast.makeText(getApplicationContext(),"Sorry, unable to make request" + t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun buildScreen(){


        val sectionsPagerAdapter = SectionsPageAdapter( supportFragmentManager)
        sectionsPagerAdapter.addFragment(NewTasksFragment(), "Новые")
        sectionsPagerAdapter.addFragment(InWorkTasksFragment(), "В работе")
        sectionsPagerAdapter.addFragment(CompleteTasksFragment(), "Готовые")



        view_pager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(ru.acurresearch.mosdombyt.R.id.tabs)
        tabs.setupWithViewPager(view_pager)

    }

    fun fetchAndRebuildTasks(){
        fun onSuccess(resp_data: List<Task>){
            App.prefs.allTasks =  resp_data
            buildScreen()
        }

        val call = App.api.fetchTasks()
        call.enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    if (response.isSuccessful)
                        onSuccess(response.body()!!)
                    else
                        Log.e("sendPhone", "Sorry, failure on request "+ response.errorBody())
            }
            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("sendPhone", "Sorry, unable to make request", t)
                Toast.makeText(getApplicationContext(),"Sorry, unable to make request" + t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }



}