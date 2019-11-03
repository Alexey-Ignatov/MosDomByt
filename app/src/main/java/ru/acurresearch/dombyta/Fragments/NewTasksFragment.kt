package ru.acurresearch.dombyta.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_order_search.*
import ru.acurresearch.dombyta.R
import kotlinx.android.synthetic.main.new_task_fragment_master_consol.*
import ru.acurresearch.dombyta.Activities.MasterConsolActivity
import ru.acurresearch.dombyta.Adapters.NewTaskListAdapter
import ru.acurresearch.dombyta.Adapters.OrderSearchListAdapter
import ru.acurresearch.dombyta.App.App
import ru.acurresearch.dombyta.Constants
import ru.acurresearch.dombyta.Order
import ru.acurresearch.dombyta.Task

/**
 * Created by User on 2/28/2017.
 */

class NewTasksFragment() : androidx.fragment.app.Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.new_task_fragment_master_consol, container, false)

        (context as MasterConsolActivity).initNewTaskList(view)
        return view
    }

}