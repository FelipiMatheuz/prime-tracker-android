package com.felipimatheuz.primehunt.business.util

import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R

enum class PrimeFilter(@StringRes val text: Int) {
    SHOW_ALL(R.string.show_all),
    AVAILABLE(R.string.available),
    UNAVAILABLE(R.string.unavailable),
    INCOMPLETE(R.string.incomplete),
    COMPLETE(R.string.complete)
}