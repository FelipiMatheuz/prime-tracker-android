package com.felipimatheuz.primehunt.business.resources

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtherPrimeData @Inject constructor(@ApplicationContext context: Context) {

    private val localData = context.getSharedPreferences("OTHER_PRIME_DATA", Context.MODE_PRIVATE)

    fun getLocalData(): SharedPreferences? = localData

    fun setStatusItem(name: String, value: Boolean) {
        localData.edit {
            putBoolean(name, value)
        }
    }
}