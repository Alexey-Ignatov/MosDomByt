package ru.acurresearch.dombyta.Activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import ru.acurresearch.dombyta.Adapters.SectionsPageAdapter
import kotlinx.android.synthetic.main.activity_master_consol.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.acurresearch.dombyta.*
import ru.acurresearch.dombyta.Adapters.CompleteTaskListAdapter
import ru.acurresearch.dombyta.Adapters.InWorkTaskListAdapter
import ru.acurresearch.dombyta.Adapters.NewTaskListAdapter
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.Fragments.CompleteTasksFragment
import ru.acurresearch.dombyta.Fragments.InWorkTasksFragment
import ru.acurresearch.dombyta.Fragments.NewTasksFragment

//TODO прилепить фетчинг новых товаров к апдейту таворов по эвоторовской базе
//TODO Ускорить, обновляеться все очень медленно
//TODO добавить проверку на пустоту имени и телефона, иначе сервер упадет!!!

class MasterConsolActivity : AppCompatActivity() {

    var newTasksItems = ArrayList<Task>(listOf())
    var inWorkTasksItems = ArrayList<Task>(listOf())
    var completeTasksItems = ArrayList<Task>(listOf())

    //var allTasks = ArrayList<Task>(listOf())

    var adapterNewTask = NewTaskListAdapter(newTasksItems, this)
    var adapterInWorkTask = InWorkTaskListAdapter(inWorkTasksItems, this)
    var adapterCompleteTask = CompleteTaskListAdapter(completeTasksItems, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.acurresearch.dombyta.R.layout.activity_master_consol)
        if (isRegistered().not()){
            startActivity(Intent(this, TokenActivity::class.java))
        }
        fetchAndBuildTasks()
        fetchMasters()




    }
    fun isRegistered(): Boolean{
        return App.prefs.cashBoxServerData != App.prefs.emptyCashBoxServerData
    }
    fun dumpTasksToDisk(){
        var arrListToDump = ArrayList(listOf<Task>())
        arrListToDump.addAll(newTasksItems)
        arrListToDump.addAll(inWorkTasksItems)
        arrListToDump.addAll(completeTasksItems)
        App.prefs.allTasks = arrListToDump
    }

    fun initTaskLists(){
        completeTasksItems = ArrayList( App.prefs.allTasks.filter { it.status == Constants.TaskStatus.COMPLETE })
        inWorkTasksItems =ArrayList( App.prefs.allTasks.filter { it.status == Constants.TaskStatus.IN_WORK })
        newTasksItems =ArrayList( App.prefs.allTasks.filter { it.status == Constants.TaskStatus.NEW })
    }
    fun setAdapters(){
        adapterNewTask = NewTaskListAdapter(newTasksItems, this)
        adapterInWorkTask = InWorkTaskListAdapter(inWorkTasksItems, this)
        adapterCompleteTask = CompleteTaskListAdapter(completeTasksItems, this)
    }
    fun initNewTaskList(view: View){

        var adapterNewTask = NewTaskListAdapter(newTasksItems, this)

        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        var task_list = view.findViewById(R.id.new_task_list) as RecyclerView
        task_list.layoutManager = layoutManager
        task_list.adapter = adapterNewTask
    }

    fun initInWorkTaskList(view: View){


        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        var task_list = view.findViewById(R.id.in_work_task_list) as RecyclerView
        task_list.layoutManager = layoutManager
        task_list.adapter = adapterInWorkTask

    }

    fun initTaskCompleteList(view: View){

        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        var task_list = view.findViewById(R.id.complete_task_list) as RecyclerView
        task_list.layoutManager = layoutManager
        task_list.adapter = adapterCompleteTask
    }


    fun fetchMasters(){
        fun onSuccess(resp_data: List<Master>){
            App.prefs.allMasters =  resp_data
        }

        val call = App.api.fetchMasters(App.prefs.cashBoxServerData.authHeader)
        call.enqueue(object : Callback<List<Master>> {
            override fun onResponse(call: Call<List<Master>>, response: Response<List<Master>>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    onSuccess(response.body()!!)
                else
                    Toast.makeText(this@MasterConsolActivity,"Ошибка на сервере. Мы устраняем проблему. Повторите позже.", Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<List<Master>>, t: Throwable) {
                Toast.makeText(getApplicationContext(),"Ошибка! Проверьте подключение к интернету!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun buildScreen(){
        val sectionsPagerAdapter = SectionsPageAdapter( supportFragmentManager)
        sectionsPagerAdapter.addFragment(NewTasksFragment(), "Новые")
        sectionsPagerAdapter.addFragment(InWorkTasksFragment(), "В работе")
        sectionsPagerAdapter.addFragment(CompleteTasksFragment(), "Готовые")



        view_pager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(ru.acurresearch.dombyta.R.id.tabs)
        tabs.setupWithViewPager(view_pager)

    }

    fun fetchAndBuildTasks(){
        fun onSuccess(resp_data: List<Task>){
            App.prefs.allTasks =  resp_data
            initTaskLists()
            setAdapters()
            buildScreen()
        }

        val call = App.api.fetchTasks(App.prefs.cashBoxServerData.authHeader)
        call.enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    onSuccess(response.body()!!)
                else
                    Toast.makeText(this@MasterConsolActivity,"Ошибка на сервере. Мы устраняем проблему. Повторите позже.", Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Toast.makeText(getApplicationContext(),"Ошибка! Проверьте подключение к интернету!", Toast.LENGTH_SHORT).show()
            }
        })
    }



}