package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettings @Inject constructor(@ApplicationContext context: Context) {
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