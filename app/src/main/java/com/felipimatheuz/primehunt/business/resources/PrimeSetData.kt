package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrimeSetData @Inject constructor(@ApplicationContext context: Context) {

    private val localData = context.getSharedPreferences("PRIME_SET_DATA", MODE_PRIVATE)

    fun getLocalData(): SharedPreferences? = localData

    fun setStatusItem(name: String, value: Boolean) {
        localData.edit {
            putBoolean(name, value)
        }
    }
}