package ru.acurresearch.mosdombyt.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose_pay_type.*
import ru.acurresearch.mosdombyt.Constants
import ru.acurresearch.mosdombyt.R
import ru.evotor.framework.navigation.NavigationApi.createIntentForSellReceiptEdit

class ChoosePayTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pay_type)

        post_pay_option_btn.setOnClickListener {
            var intent =  Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_PAY_TYPE_FIELD, Constants.BillingType.POSTPAY)
            startActivity(intent)

        }
        pre_pay_option_btn.setOnClickListener {
            var intent =  Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_PAY_TYPE_FIELD, Constants.BillingType.PREPAY)
            startActivity(intent)}
        get_compl_order_btn.setOnClickListener {
            var intent =  Intent(this, OrderSearchActivity::class.java)
            startActivity(intent)
        }
        regular_pay_btn.setOnClickListener {
            startActivity(createIntentForSellReceiptEdit())
        }
    }
}
