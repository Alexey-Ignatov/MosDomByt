package ru.acurresearch.dombyta.ui.order.complete

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ga.nk2ishere.dev.base.BaseController
import ga.nk2ishere.dev.base.BaseLCE
import kotlinx.android.synthetic.main.activity_order_final.view.*
import ru.acurresearch.dombyta.R
import ru.acurresearch.dombyta.data.common.model.Check
import ru.acurresearch.dombyta.data.common.model.CheckPosition
import ru.acurresearch.dombyta.data.common.model.Order
import ru.evotor.devices.commons.ConnectionWrapper
import ru.evotor.devices.commons.Constants
import ru.evotor.devices.commons.DeviceServiceConnector
import ru.evotor.devices.commons.printer.PrinterDocument
import ru.evotor.devices.commons.printer.printable.PrintableText
import ru.evotor.devices.commons.services.IPrinterServiceWrapper
import ru.evotor.devices.commons.services.IScalesServiceWrapper
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand
import ru.evotor.framework.navigation.NavigationApi
import java.lang.Exception
import java.util.*
import kotlin.properties.Delegates

class OrderFinalController(args: Bundle): BaseController(args), OrderFinalView, OrderFinalViewPMRenderer {
    companion object {
        fun create(
            orderId: Long,
            orderAlreadyExists: Boolean
        ) =
            OrderFinalController(Bundle(2).apply {
                putLong(KEY_ORDER_ID, orderId)
                putBoolean(KEY_ORDER_ALREADY_EXISTS, orderAlreadyExists)
            })

        const val KEY_ORDER_ID = "ORDER_ID"
        const val KEY_ORDER_ALREADY_EXISTS = "ORDER_ALREADY_EXISTS"
    }
    private var orderId by Delegates.notNull<Long>()
    private var orderAlreadyExists by Delegates.notNull<Boolean>()

    private val diffElement = OrderFinalViewPMDiffDispatcher.Builder()
        .target(this)
        .build()
    private var state: OrderFinalViewPM? = null
    private val positionsAdapter = GroupAdapter<GroupieViewHolder>()

    private fun commonPrint(text: String) {
        Thread(Runnable {
            try {
                DeviceServiceConnector.getPrinterService().printDocument(
                    Constants.DEFAULT_DEVICE_INDEX,
                    PrinterDocument(PrintableText(text))
                )
            } catch (e: Exception) { /*pass*/ }
        }).start()
    }

    override fun renderCurrentOrder(currentOrder: BaseLCE<Order>) {
        view?.order_client_name_holder?.text = currentOrder.content?.client?.target!!.name
        view?.order_client_phone_holder?.text = currentOrder.content?.client?.target!!.phone
        view?.order_final_total_price?.text = "${currentOrder.content?.price} руб."
        view?.order_status_text?.text = Order.OrderStatus.MAP_TO_RUSSIAN[currentOrder.content?.status]
        if (currentOrder.content?.isPaid == true){
            view?.order_is_paid_text?.text = "ОПЛАЧЕН"
            view?.order_is_paid_text?.setTextColor(Color.GREEN)
        }
        positionsAdapter.update(
            currentOrder.content!!.positionsList
                .map { PositionItem(it) }
        )
    }

    override fun renderSuggestedAction(suggestedAction: BaseLCE<String>) { when {
            suggestedAction.content == Order.OrderSuggestedAction.PAY -> {
                view?.order_final_pay_btn?.visibility = View.VISIBLE
                view?.order_final_close_order_btn?.visibility = View.INVISIBLE
                view?.order_final_client_print_btn?.visibility = View.INVISIBLE
            }
            suggestedAction.content == Order.OrderSuggestedAction.CLOSE -> {
                view?.order_final_pay_btn?.visibility = View.INVISIBLE
                view?.order_final_close_order_btn?.visibility = View.VISIBLE
                view?.order_final_client_print_btn?.visibility = View.INVISIBLE
            }
            suggestedAction.content == Order.OrderSuggestedAction.CREATE -> {
                view?.order_final_pay_btn?.visibility = View.INVISIBLE
                view?.order_final_close_order_btn?.visibility = View.INVISIBLE
                view?.order_final_client_print_btn?.visibility = View.VISIBLE
            }
            suggestedAction.content == Order.OrderSuggestedAction.NOTHING -> {
                view?.order_final_pay_btn?.visibility = View.INVISIBLE
                view?.order_final_close_order_btn?.visibility = View.INVISIBLE
                view?.order_final_client_print_btn?.visibility = View.INVISIBLE
            }
    } }

    private fun updatePMAction(action: OrderFinalViewUpdatePMAction) {
        diffElement.dispatch(action.pm, state)
        state = action.pm
    }

    private fun printLabelAction(action: OrderFinalViewPrintLabelAction) {
        commonPrint(action.text)
    }

    private fun showPrintLabelDialogAction(action: OrderFinalViewShowPrintLabelDialog) {
        val alertDialog = AlertDialog.Builder(view?.context!!)
        alertDialog.setTitle("Распечатать ярлык с номером заказа?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Да") { dialog, id ->
            presenter.handleViewEvent(OrderFinalViewLabelPrintConfirmedEvent())
        }
        alertDialog.setNegativeButton("Нет") { dialog, id ->
            activity?.finish()
        }
        alertDialog.create().show()
    }

    private fun closeAction(action: OrderFinalViewCloseAction) {
        activity?.finish()
    }

    private fun openSellCloseAction(action: OrderFinalViewOpenSellCloseAction) {
        OpenSellReceiptCommand(action.positions, null).process(activity!!) { integrationManagerFuture ->
            val result = integrationManagerFuture.result
            if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                activity?.startActivity(NavigationApi.createIntentForSellReceiptEdit())
                activity?.finish()
            }
        }
    }

    @InjectPresenter lateinit var presenter: OrderFinalPresenter
    @ProvidePresenter fun providePresenter(): OrderFinalPresenter = OrderFinalPresenter(orderId, orderAlreadyExists)
    override fun getLayoutId(): Int = R.layout.activity_order_final

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        orderId = args.getLong(KEY_ORDER_ID, 0)
        orderAlreadyExists = args.getBoolean(KEY_ORDER_ALREADY_EXISTS, false)

        return super.onCreateView(inflater, container)
    }

    override fun initView(view: View) {
        view.order_list_final.layoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
        view.order_list_final.adapter = positionsAdapter
        DeviceServiceConnector.startInitConnections(applicationContext, true)
        DeviceServiceConnector.addConnectionWrapper(object : ConnectionWrapper {
            override fun onPrinterServiceDisconnected() = Unit
            override fun onScalesServiceConnected(scalesService: IScalesServiceWrapper) = Unit
            override fun onScalesServiceDisconnected() = Unit

            override fun onPrinterServiceConnected(printerService: IPrinterServiceWrapper) {
                presenter.handleViewEvent(OrderFinalViewPrinterInitializedEvent())
            }
        })

        view.order_final_pay_btn.setOnClickListener { presenter.handleViewEvent(OrderFinalViewPayClickedEvent()) }
        view.order_final_close_order_btn.setOnClickListener{ presenter.handleViewEvent(OrderFinalViewCloseClickedEvent()) }
        view.order_final_client_print_btn.setOnClickListener{ presenter.handleViewEvent(OrderFinalViewPrintClickedEvent()) }
    }

    override fun applyAction(action: OrderFinalViewAction) { when(action) {
        is OrderFinalViewUpdatePMAction -> updatePMAction(action)
    } }

    override fun applyActionWithSkip(action: OrderFinalViewAction) { when(action) {
        is OrderFinalViewPrintLabelAction -> printLabelAction(action)
        is OrderFinalViewShowPrintLabelDialog -> showPrintLabelDialogAction(action)
        is OrderFinalViewCloseAction -> closeAction(action)
        is OrderFinalViewOpenSellCloseAction -> openSellCloseAction(action)
    } }

}