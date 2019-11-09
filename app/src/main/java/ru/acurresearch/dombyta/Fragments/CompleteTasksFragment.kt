package ru.acurresearch.dombyta.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.acurresearch.dombyta.Activities.MasterConsolActivity
import ru.acurresearch.dombyta.Adapters.CompleteTaskListAdapter
import ru.acurresearch.dombyta.Adapters.NewTaskListAdapter
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.R


class CompleteTasksFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.complete_task_fragment_master_consol, container, false)
        (context as MasterConsolActivity).initTaskCompleteList(view)

        return view
    }


}