package ru.acurresearch.mosdombyt.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_choose_pay_type.*
import ru.acurresearch.mosdombyt.App.App
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.R

import ru.evotor.framework.navigation.NavigationApi.createIntentForSellReceiptEdit

import ru.evotor.devices.commons.DeviceServiceConnector



import ru.evotor.devices.commons.ConnectionWrapper
import ru.evotor.devices.commons.exception.DeviceServiceException
import ru.evotor.devices.commons.printer.PrinterDocument
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.devices.commons.services.IPrinterServiceWrapper
import ru.evotor.devices.commons.services.IScalesServiceWrapper



class ChoosePayTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pay_type)

        if (isRegistered().not()){
            startActivity(Intent(this, TokenActivity::class.java))
        }


        post_pay_option_btn.setOnClickListener {
            if (isRegistered()) {
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra(Constants.INTENT_PAY_TYPE_FIELD, Constants.BillingType.POSTPAY)
                startActivity(intent)
            }else{
                startActivity(Intent(this, TokenActivity::class.java))
            }

        }
        pre_pay_option_btn.setOnClickListener {
            if (isRegistered()) {
            var intent =  Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_PAY_TYPE_FIELD, Constants.BillingType.PREPAY)
            startActivity(intent)
            }else{
                startActivity(Intent(this, TokenActivity::class.java))
            }
        }
        get_compl_order_btn.setOnClickListener {
            if (isRegistered()) {
                var intent = Intent(this, OrderSearchActivity::class.java)
                startActivity(intent)
            }else{
                startActivity(Intent(this, TokenActivity::class.java))
            }
        }
        regular_pay_btn.setOnClickListener {
            startActivity(createIntentForSellReceiptEdit())
        }
    }

    fun isRegistered(): Boolean{
        return App.prefs.cashBoxServerData != App.prefs.emptyCashBoxServerData
    }

}
