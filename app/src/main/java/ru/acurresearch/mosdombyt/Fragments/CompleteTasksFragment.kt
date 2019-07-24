package ru.acurresearch.mosdombyt.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.acurresearch.mosdombyt.Activities.MasterConsolActivity
import ru.acurresearch.mosdombyt.Adapters.CompleteTaskListAdapter
import ru.acurresearch.mosdombyt.Adapters.NewTaskListAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.R


class CompleteTasksFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.complete_task_fragment_master_consol, container, false)

        initTaskList(view)







        return view
    }
    fun initTaskList(view: View){
        (context as MasterConsolActivity).completeTasksItems =ArrayList( App.prefs.allTasks.filter { it.status == Constants.TaskStatus.COMPLETE })

        var task_list = view.findViewById(R.id.complete_task_list) as RecyclerView
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        task_list.layoutManager = layoutManager
        var adapter = CompleteTaskListAdapter((context as MasterConsolActivity).completeTasksItems, context!!)
        task_list.adapter = adapter
    }
    companion object {
        private val TAG = "Tab1Fragment"
    }
}