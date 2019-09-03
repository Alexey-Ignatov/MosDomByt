package ru.acurresearch.dombyta.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import ru.acurresearch.dombyta.Activities.MasterConsolActivity
import ru.acurresearch.dombyta.Adapters.InWorkTaskListAdapter
import ru.acurresearch.dombyta.Adapters.NewTaskListAdapter
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R




class InWorkTasksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.in_work_fragment_master_consol, container, false)
        (context as MasterConsolActivity).initInWorkTaskList(view)

        return view
    }

}