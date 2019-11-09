package ru.acurresearch.dombyta_new.ui.order.complete

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.R as BaseR

class OrderFinalActivityWrapper: AppCompatActivity() {
    companion object {
        const val KEY_ORDER_ID = "ORDER_ID"
        const val KEY_ORDER_ALREADY_EXISTS = "ORDER_ALREADY_EXISTS"
    }
    private val layout: Int = BaseR.layout.activity
    private lateinit var homeController: BaseController
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        homeController = OrderFinalController.create(
            orderId = intent.getLongExtra(KEY_ORDER_ID, 0),
            orderAlreadyExists = intent.getBooleanExtra(KEY_ORDER_ALREADY_EXISTS, false)
        )

        router = Conductor.attachRouter(this, findViewById(BaseR.id.main_container), savedInstanceState)
        if (!router.hasRootController()) router.setRoot(RouterTransaction.with(homeController))
    }

    override fun onBackPressed() {
        if (!router.handleBack()) super.onBackPressed()
    }
}