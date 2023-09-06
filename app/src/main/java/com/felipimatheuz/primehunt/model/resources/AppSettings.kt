package com.felipimatheuz.primehunt.model.resources

import android.content.Context
import com.felipimatheuz.primehunt.util.AppFilter

class AppSettings(context: Context) {
    private val settings = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)

    fun getPrimeFilter(): AppFilter {
        return AppFilter.valueOf(settings.getString("PrimeSetFilter", "SHOW_ALL")!!)
    }

    fun setPrimeFilter(appFilter: AppFilter) {
        val editor = settings.edit()
        editor.apply {
            putString("PrimeSetFilter", appFilter.toString())
            commit()
        }
    }

    fun getRelicFilter(): AppFilter {
        return AppFilter.valueOf(settings.getString("RelicFilter", "SHOW_ALL")!!)
    }

    fun setRelicFilter(appFilter: AppFilter) {
        val editor = settings.edit()
        editor.apply {
            putString("RelicFilter", appFilter.toString())
            commit()
        }
    }

    fun getOtherFilter(): AppFilter {
        return AppFilter.valueOf(settings.getString("PrimeOtherFilter", "SHOW_ALL")!!)
    }

    fun getOtherFilter(appFilter: AppFilter) {
        val editor = settings.edit()
        editor.apply {
            putString("PrimeOtherFilter", appFilter.toString())
            commit()
        }
    }
}