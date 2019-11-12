package ru.acurresearch.dombyta.ui.token

import ga.nk2ishere.dev.base.BaseActivity

class TokenActivity: BaseActivity(
    homeController = TokenController.create()
) {
    companion object {
        const val REQUEST_CODE = 101
        const val RESULT_OK = 1
        const val RESULT_KEY_TOKEN = "TOKEN"
    }
}