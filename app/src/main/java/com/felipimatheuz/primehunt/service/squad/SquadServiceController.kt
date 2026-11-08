package com.felipimatheuz.primehunt.service.squad

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.felipimatheuz.primehunt.domain.model.squad.Platform
import com.felipimatheuz.primehunt.domain.model.squad.Region

object SquadServiceController {

    fun startSession(
        context: Context,
        targetId: String,
        targetName: String,
        platform: Platform,
        region: Region
    ) {
        val intent = Intent(context, SquadFinderService::class.java).apply {
            action = SquadFinderService.ACTION_START
            putExtra(SquadFinderService.EXTRA_TARGET_ID, targetId)
            putExtra(SquadFinderService.EXTRA_TARGET_NAME, targetName)
            putExtra(SquadFinderService.EXTRA_PLATFORM, platform.name)
            putExtra(SquadFinderService.EXTRA_REGION, region.name)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopSession(context: Context) {
        val intent = Intent(context, SquadFinderService::class.java).apply {
            action = SquadFinderService.ACTION_STOP
        }
        context.startService(intent)
    }
}
