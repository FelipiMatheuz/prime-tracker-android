package com.felipimatheuz.primehunt.core.logging

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLogger @Inject constructor() {
    private val logs = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    fun log(tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val entry = "[$timestamp] [$tag] $message"
        synchronized(logs) {
            logs.add(entry)
            if (logs.size > 500) logs.removeAt(0)
        }
    }

    fun getLogs(): String {
        return synchronized(logs) {
            logs.joinToString("\n")
        }
    }
}
