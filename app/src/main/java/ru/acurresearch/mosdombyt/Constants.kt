package ru.acurresearch.mosdombyt

object Constants {
    const val BASE_URL = "http://acur-research24.ru"
    //const val BASE_URL = "http://10.8.10.201"
    const val SHARED_FILE_PATH = "ru.acur_research.mosdombyt.prefs"
    const val PREFS_CURR_PHONE = "PREFS_CURR_PHONE"
    const val PREFS_CURR_NAME = "PREFS_CURR_NAME"
    const val PREFS_SELECTED_POSITIONS = "PREFS_SELECTED_POSITIONS"
    const val PREFS_ALL_ALLOWED_PRODUCTS = "PREFS_ALL_ALLOWED_PRODUCTS"
    const val PREFS_PROD_IN_PROD_POP_UP_PRICE = "PREFS_PROD_IN_PROD_POP_UP_PRICE"
    const val INTENT_PAY_TYPE_FIELD = "INENT_PAY_TYPE_FIELD"
    const val PREFS_LAST_ORDER = "PREFS_LAST_ORDER"
    const val INTENT_ORDER_TO_ORDER_FINAL = "INTENT_ORDER_TO_ORDER_FINAL"
    const val PREFS_ALL_TASKS = "PREFS_ALL_TASKS"
    const val NUMBER_TELS_TO_SHOW = 5
    const val PREFS_ALL_MASTERS = "PREFS_ALL_MASTERS"
    const val DATE_PATTERN = "yyyy-MM-dd HH:mm"


    object BillingType {
        const val PREPAY = "PREPAY"
        const val POSTPAY = "POSTPAY"
    }

    object OrderStatus {
        const val PRE_CREATED = "PRE_CREATED"
        const val CREATED = "CREATED"
        const val PENDING = "PENDING"
        const val IN_PROGRESS = "IN_PROGRESS"
        const val READY = "READY"
        const val CLOSED = "CLOSED"
    }

    object TaskStatus {
        const val NEW = "NEW"
        const val IN_WORK = "IN_WORK"
        const val COMPLETE = "COMPLETE"
    }

    object OrderSuggestedAction{
        const val PAY = "PAY"
        const val CLOSE = "CLOSE"
        const val CREATE = "CREATE"
        const val NOTHING= "NOTHING"
    }
}