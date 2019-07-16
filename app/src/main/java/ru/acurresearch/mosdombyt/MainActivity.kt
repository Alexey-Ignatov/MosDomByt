package ru.acurresearch.mosdombyt

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_x.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.evotor.devices.commons.DeviceServiceConnector
import ru.evotor.framework.navigation.NavigationApi.createIntentForPaybackReceiptEdit
import ru.evotor.framework.navigation.NavigationApi.createIntentForSellReceiptEdit
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.evotor.devices.commons.services.IScalesServiceWrapper
import ru.evotor.devices.commons.exception.DeviceServiceException
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.devices.commons.printer.PrinterDocument
import ru.evotor.devices.commons.services.IPrinterServiceWrapper
import ru.evotor.devices.commons.ConnectionWrapper



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar)

        //val fab: FloatingActionButton = findViewById(R.id.fab)
        //fab.setOnClickListener { view ->
        //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show()
        //}
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //val navView: NavigationView = findViewById(R.id.nav_view)
        //val toggle = ActionBarDrawerToggle(
        //    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        //)
        //drawerLayout.addDrawerListener(toggle)
        //toggle.syncState()

        //navView.setNavigationItemSelectedListener(this)

        //save_phone_btn.setOnClickListener {
        //    App.prefs.currName = ed_txt_name.text.toString()
        //    App.prefs.currPhone = ed_txt_phone.text.toString()
        //    startActivity(Intent(createIntentForSellReceiptEdit()))
        //}
        //skip_tel_btn.setOnClickListener {
        //    startActivity(Intent(createIntentForSellReceiptEdit()))
        //}

        val listItem = listOf("One", "Two","One", "Two","One", "Two","One", "Two","One", "Two","One", "Two","One", "Two","One", "Two","One", "Two","One", "Two","One", "Two")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItem)
        goods_list_view.adapter = adapter
        //val adapter = ArrayAdapter<String>(
        //    this,
        //    android.R.layout.simple_list_item_1, android.R.id.text1, listItem
        //)
        goods_list_view.setAdapter(adapter)


        //val layoutManager = LinearLayoutManager(this)
        //layoutManager.orientation = LinearLayoutManager.VERTICAL
        //order_list_view.layoutManager = layoutManager
        //var adapter= OrderViewAdapter(listItem, this)
        //order_list_view.adapter = adapter



        //initPrinter()


        //sendCheck()
    }


    fun sendCheck(){
        val evoReceipt = ReceiptApi.getReceipt(this, Receipt.Type.SELL)
        val checkToSend = Check.fromEvoReceipt(evoReceipt!!)
        App.prefs.currCheck = checkToSend


        fun onSuccess(resp_data: String){

        }

        val call = App.api.sendCheckServ(App.prefs.currCheck)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("processServerRquests",response.errorBody().toString() )
                if (response.isSuccessful)
                    if (response.isSuccessful)
                            onSuccess(response.body()!!)
                    else
                        Log.e("sendPhone", "Sorry, failure on request "+ response.errorBody())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("sendPhone", "Sorry, unable to make request", t)
                Toast.makeText(getApplicationContext(),"Sorry, unable to make request" + t.toString(),Toast.LENGTH_SHORT).show()
            }
        })

    }




    override fun onBackPressed() {
       // val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        // if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        //    drawerLayout.closeDrawer(GravityCompat.START)
        //} else {
            super.onBackPressed()
        //}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
