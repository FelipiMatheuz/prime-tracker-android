package com.felipimatheuz.primehunt.model.resources

import android.content.Context
import com.felipimatheuz.primehunt.util.PrimeFilter

class AppSettings(context: Context) {
    private val settings = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)

    fun getPrimeFilter(filter: String): PrimeFilter {
        return PrimeFilter.valueOf(settings.getString(filter, "SHOW_ALL")!!)
    }

    fun setPrimeFilter(filter: String, primeFilter: PrimeFilter) {
        val editor = settings.edit()
        editor.apply {
            putString(filter, primeFilter.toString())
            apply()
        }
    }
}