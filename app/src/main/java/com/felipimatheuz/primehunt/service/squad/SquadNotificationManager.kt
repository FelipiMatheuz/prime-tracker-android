package com.felipimatheuz.primehunt.service.squad

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.core.MainActivity
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SquadNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_squad_finder_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_squad_finder_desc)
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(
        targetName: String,
        platform: Platform,
        region: Region,
        isConnected: Boolean
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, SquadFinderService::class.java).apply {
            action = SquadFinderService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val platformLabel = context.getString(platform.labelRes)
        val regionLabel = context.getString(region.labelRes)
        val statusLabel = context.getString(
            if (isConnected) R.string.notification_squad_connected
            else R.string.notification_squad_disconnected
        )

        val title = context.getString(R.string.notification_squad_title, targetName)
        val body = context.getString(R.string.notification_squad_status, platformLabel, regionLabel, statusLabel)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_squad)
            .setContentTitle(title)
            .setContentText(body)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(contentIntent)
            .addAction(
                R.drawable.ic_close,
                context.getString(R.string.notification_action_stop),
                stopPendingIntent
            )
            .build()
    }

    fun updateNotification(
        targetName: String,
        platform: Platform,
        region: Region,
        isConnected: Boolean
    ) {
        val notification = buildNotification(targetName, platform, region, isConnected)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        const val CHANNEL_ID = "squad_finder_session_channel"
        const val NOTIFICATION_ID = 4001
    }
}
