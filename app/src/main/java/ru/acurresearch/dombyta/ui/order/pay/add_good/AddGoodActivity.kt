package ru.acurresearch.dombyta.ui.order.pay.add_good

import ga.nk2ishere.dev.base.BaseActivity

class AddGoodActivity: BaseActivity(
    homeController = AddGoodController.create()
) {
    companion object {
        const val ADD_GOOD_REQUEST = 2
        const val ADD_GOOD_RESULT_OK = 102
        const val KEY_ORDER_POSITION_ID = "ORDER_POSITION_ID"
    }
}