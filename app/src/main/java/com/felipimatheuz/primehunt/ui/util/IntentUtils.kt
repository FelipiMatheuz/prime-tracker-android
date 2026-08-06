package com.felipimatheuz.primehunt.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.material3.SnackbarHostState
import com.felipimatheuz.primehunt.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object IntentUtils {
    fun openUrl(
        context: Context,
        url: String,
        snackbarHostState: SnackbarHostState? = null,
        scope: CoroutineScope? = null
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (_: Exception) {
            if (snackbarHostState != null && scope != null) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.error_no_browser)
                    )
                }
            }
        }
    }

    fun contactEmail(
        context: Context,
        email: String,
        subject: String,
        snackbarHostState: SnackbarHostState? = null,
        scope: CoroutineScope? = null
    ) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            if (snackbarHostState != null && scope != null) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.error_no_email_app)
                    )
                }
            }
        }
    }
}
