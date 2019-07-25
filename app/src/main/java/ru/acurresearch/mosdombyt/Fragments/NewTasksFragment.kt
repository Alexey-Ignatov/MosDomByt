package ru.acurresearch.mosdombyt.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_order_search.*
import ru.acurresearch.mosdombyt.R
import kotlinx.android.synthetic.main.new_task_fragment_master_consol.*
import ru.acurresearch.mosdombyt.Activities.MasterConsolActivity
import ru.acurresearch.mosdombyt.Adapters.NewTaskListAdapter
import ru.acurresearch.mosdombyt.Adapters.OrderSearchListAdapter
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.Order
import ru.acurresearch.mosdombyt.Task

/**
 * Created by User on 2/28/2017.
 */

class NewTasksFragment() : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.new_task_fragment_master_consol, container, false)

        (context as MasterConsolActivity).initNewTaskList(view)
        return view
    }

}