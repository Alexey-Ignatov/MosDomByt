package ru.acurresearch.mosdombyt.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import ru.acurresearch.mosdombyt.Activities.MasterConsolActivity
import ru.acurresearch.mosdombyt.Adapters.InWorkTaskListAdapter
import ru.acurresearch.mosdombyt.Adapters.NewTaskListAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.R




class InWorkTasksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.in_work_fragment_master_consol, container, false)

        initTaskList(view)


        return view
    }
    fun initTaskList(view: View){
        (context as MasterConsolActivity).inWorkTasksItems =ArrayList( App.prefs.allTasks.filter { it.status == Constants.TaskStatus.IN_WORK })

        var task_list = view.findViewById(R.id.in_work_task_list) as RecyclerView
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        task_list.layoutManager = layoutManager
        var adapter = InWorkTaskListAdapter((context as MasterConsolActivity).inWorkTasksItems, context!!)
        task_list.adapter = adapter

    }


}