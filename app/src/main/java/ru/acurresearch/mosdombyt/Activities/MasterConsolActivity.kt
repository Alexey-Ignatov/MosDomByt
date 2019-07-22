package ru.acurresearch.mosdombyt.Activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ru.acurresearch.mosdombyt.Adapters.SectionsPageAdapter
import kotlinx.android.synthetic.main.activity_master_consol.*
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.Fragments.CompleteTasksFragment
import ru.acurresearch.mosdombyt.Fragments.InWorkTasksFragment
import ru.acurresearch.mosdombyt.Fragments.NewTasksFragment
import ru.acurresearch.mosdombyt.Master
import ru.acurresearch.mosdombyt.Task

class MasterConsolActivity : AppCompatActivity() {

    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.acurresearch.mosdombyt.R.layout.activity_master_consol)



        //Todo fetch data from server
        App.prefs.allMasters = ArrayList((1..5).map { Master("Имя Человека "+ it.toString(), "Специализация Человека "+ it.toString() ) })
        rebuildScreen()

    }
    fun rebuildScreen(){

        //Todo fetch data from server
        //taskList = ArrayList(App.prefs.lastOrder.toTaskList())
        //вот тут было как бы с сервера

        var tmp_arr = ArrayList(App.prefs.lastOrder.toTaskList())
        //tmp_arr[0].status = Constants.TaskStatus.IN_WORK
        //tmp_arr[1].status = Constants.TaskStatus.COMPLETE
        //tmp_arr[3].status = Constants.TaskStatus.IN_WORK

        App.prefs.allTasks = tmp_arr

        Toast.makeText(this, "Дедлайн c диска MAsterConsol rebuildScreen" + tmp_arr[0].orderPostition.expDate.toString(), Toast.LENGTH_SHORT).show()


        val sectionsPagerAdapter = SectionsPageAdapter( supportFragmentManager)
        sectionsPagerAdapter.addFragment(NewTasksFragment(), "Новые")
        sectionsPagerAdapter.addFragment(InWorkTasksFragment(), "В работе")
        sectionsPagerAdapter.addFragment(CompleteTasksFragment(), "Готовые")



        view_pager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(ru.acurresearch.mosdombyt.R.id.tabs)
        tabs.setupWithViewPager(view_pager)

    }









}