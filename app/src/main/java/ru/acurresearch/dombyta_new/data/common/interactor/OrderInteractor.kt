package ru.acurresearch.dombyta_new.data.common.interactor

import ga.nk2ishere.dev.utils.RxBoxRepository
import io.reactivex.Completable
import io.reactivex.Single
import ru.acurresearch.dombyta_new.data.common.model.Order
import ru.acurresearch.dombyta_new.data.common.model.OrderPosition
import ru.acurresearch.dombyta_new.data.common.model.ServiceItemCustom
import ru.acurresearch.dombyta_new.data.network.Api
import ru.acurresearch.dombyta_new.data.network.model.CashBoxServerData
import timber.log.Timber

class OrderInteractor(
    private val box: RxBoxRepository<Order>,
    private val boxOrderPosition: RxBoxRepository<OrderPosition>,
    private val boxServiceItemCustom: RxBoxRepository<ServiceItemCustom>,
    private val api: Api
) {
    private fun mergeOrderPositionsInOrder(
        order: Order
    ) = Completable.fromCallable {
        box.box.attach(order)
        order.positionsList.clear()
        order.positionsListUnmerged.forEach {
            boxOrderPosition.box.attach(it)
            order.positionsList.add(it)
        }
    }

    fun searchOrder(
        token: CashBoxServerData,
        searchString: String
    ) = api.searchOrder(searchString, token.authHeader)
        .flattenAsObservable { it }
        .switchMapSingle {
            mergeOrderPositionsInOrder(it)
                .andThen(Single.just(it))
        }.toList()

    fun addOrderPositionToOrder(
        order: Order,
        orderPosition: OrderPosition
    ) = Single.fromCallable {
        orderPosition.order.target = order
        order.positionsList.add(orderPosition)
        order
    }

    fun deleteOrderPositionFromOrder(
        order: Order,
        orderPosition: OrderPosition
    ) = Single.fromCallable {
        orderPosition.order.target = null
        order.positionsList.remove(orderPosition)
        order
    }

    fun saveOrder(
        order: Order
    ) = box.createOrUpdate(order)
        .andThen(Single.just(order))

    fun getOrderById(
        orderId: Long
    ) = box.read()
        .map {
            it.find { it.id == orderId }
        }

    fun updateOrder(
        token: CashBoxServerData,
        order: Order,
        orderAlreadyExists: Boolean
    ) = when(orderAlreadyExists) {
        true -> api.syncOrderStatus(order, order.id.toInt(), token.authHeader)
        false -> api.sendOrder(order, token.authHeader)
    }.flatMap {
        mergeOrderPositionsInOrder(it)
            .andThen(Single.just(it))
    }

    fun updateServiceItems(
        token: CashBoxServerData
    ) = api.fetchAllowedProds(token.authHeader)
        .flatMap {
            boxServiceItemCustom.createOrUpdate(it)
                .andThen(Single.just(it))
        }

    fun getServiceItems() =
        boxServiceItemCustom.read()

    fun saveOrderPosition(
        orderPosition: OrderPosition
    ) = boxOrderPosition.createOrUpdate(orderPosition)
        .andThen(Single.just(orderPosition))

    fun getOrderPositionById(
        orderPositionId: Long
    ) = boxOrderPosition.read()
        .map {
            it.find { it.id == orderPositionId }
        }
}