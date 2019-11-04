package ru.acurresearch.dombyta_new.ui.token

import android.os.Bundle
import ga.nk2ishere.dev.base.BaseActivity
import ru.acurresearch.dombyta.R

class TokenActivity: BaseActivity(
    homeController = TokenController.create()
) {
    companion object {
        const val REQUEST_CODE = 101
        const val RESULT_OK = 1
        const val RESULT_KEY_TOKEN = "TOKEN"
    }
}