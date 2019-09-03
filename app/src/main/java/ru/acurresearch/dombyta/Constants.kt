package ru.acurresearch.dombyta

object Constants {
    const val BASE_URL = "http://acur-research24.ru"
    //const val BASE_URL = "http://10.8.10.201"
    const val SHARED_FILE_PATH = "ru.acur_research.dombyta.prefs"
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
    const val CASHBOX_SERVER_DATA = "CASHBOX_SERVER_DATA"


    object BillingType {
        const val PREPAY = "PREP"
        const val POSTPAY = "POST"
    }

    object OrderStatus {
        const val PRE_CREATED = "PRE_CREATED"
        const val CREATED = "CRTD"
        const val PENDING = "PEND"
        const val IN_PROGRESS = "INWK"
        const val READY = "REDY"
        const val CLOSED = "CLSD"
        val MAP_TO_RUSSIAN = mapOf(PRE_CREATED to "НОВЫЙ", CREATED to "ОЖИДАНИЕ", READY to "ВЫДАЧА", CLOSED to "ЗАКРЫТ",IN_PROGRESS to "В РАБОТЕ" )
    }

    object TaskStatus {
        const val NEW = "CRTD"
        const val IN_WORK = "INWK"
        const val COMPLETE = "REDY"
    }

    object OrderSuggestedAction{
        const val PAY = "PAY"
        const val CLOSE = "CLOSE"
        const val CREATE = "CREATE"
        const val NOTHING= "NOTHING"

    }
}