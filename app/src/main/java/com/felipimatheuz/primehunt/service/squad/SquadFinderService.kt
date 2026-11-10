package com.felipimatheuz.primehunt.service.squad

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region
import com.felipimatheuz.primehunt.domain.repository.AuthRepository
import com.felipimatheuz.primehunt.domain.repository.SquadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SquadFinderService : Service() {

    @Inject lateinit var squadRepository: SquadRepository
    @Inject lateinit var squadNotificationManager: SquadNotificationManager
    @Inject lateinit var authRepository: AuthRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatJob: Job? = null
    private var connectionJob: Job? = null

    private var currentTargetName: String = ""
    private var currentPlatform: Platform = Platform.PC
    private var currentRegion: Region = Region.NORTH_AMERICA
    private var isConnected: Boolean = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START

        when (action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> handleStop()
        }

        return START_STICKY
    }

    private fun handleStart(intent: Intent?) {
        if (authRepository.getCurrentUser() == null) {
            stopSelf()
            return
        }

        intent?.let {
            currentTargetName = it.getStringExtra(EXTRA_TARGET_NAME) ?: currentTargetName
            val platformStr = it.getStringExtra(EXTRA_PLATFORM)
            if (platformStr != null) {
                currentPlatform = runCatching { Platform.valueOf(platformStr) }.getOrDefault(Platform.PC)
            }
            val regionStr = it.getStringExtra(EXTRA_REGION)
            if (regionStr != null) {
                currentRegion = runCatching { Region.valueOf(regionStr) }.getOrDefault(Region.NORTH_AMERICA)
            }
        }

        val notification = squadNotificationManager.buildNotification(
            targetName = currentTargetName,
            platform = currentPlatform,
            region = currentRegion,
            isConnected = isConnected
        )

        val foregroundType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else {
            0
        }

        try {
            ServiceCompat.startForeground(
                this,
                SquadNotificationManager.NOTIFICATION_ID,
                notification,
                foregroundType
            )
        } catch (_: Exception) {
            // Fallback for startup constraints
        }

        startConnectionMonitoring()
        startHeartbeatLoop()
    }

    private fun handleStop() {
        heartbeatJob?.cancel()
        connectionJob?.cancel()

        serviceScope.launch {
            runCatching { squadRepository.deleteAnnouncement() }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun startConnectionMonitoring() {
        connectionJob?.cancel()
        connectionJob = serviceScope.launch {
            squadRepository.observeConnectionState()
                .catch { }
                .collect { connected ->
                    isConnected = connected
                    squadNotificationManager.updateNotification(
                        targetName = currentTargetName,
                        platform = currentPlatform,
                        region = currentRegion,
                        isConnected = isConnected
                    )
                }
        }
    }

    private fun startHeartbeatLoop() {
        heartbeatJob?.cancel()
        heartbeatJob = serviceScope.launch {
            while (true) {
                delay(HEARTBEAT_INTERVAL_MS.milliseconds)
                if (authRepository.getCurrentUser() == null) {
                    handleStop()
                    break
                }
                val result = squadRepository.heartbeatAnnouncement()
                if (result.isFailure) {
                    isConnected = false
                    squadNotificationManager.updateNotification(
                        targetName = currentTargetName,
                        platform = currentPlatform,
                        region = currentRegion,
                        isConnected = false
                    )
                }
            }
        }
    }

    override fun onDestroy() {

        serviceScope.cancel()
        squadNotificationManager.cancelNotification()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.felipimatheuz.primehunt.squad.ACTION_START"
        const val ACTION_STOP = "com.felipimatheuz.primehunt.squad.ACTION_STOP"

        const val EXTRA_TARGET_ID = "extra_target_id"
        const val EXTRA_TARGET_NAME = "extra_target_name"
        const val EXTRA_PLATFORM = "extra_platform"
        const val EXTRA_REGION = "extra_region"

        const val HEARTBEAT_INTERVAL_MS = 120_000L // 2 minutes
    }
}
