package com.felipimatheuz.primehunt.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey

@Serializable
data object OverviewKey : AppNavKey

@Serializable
data class PrimeSetsKey(val filter: String = "SHOW_ALL") : AppNavKey

@Serializable
data class OtherPrimesKey(val filter: String = "SHOW_ALL") : AppNavKey

@Serializable
data class RelicsKey(val filter: String = "SHOW_ALL") : AppNavKey

@Serializable
data object SyncKey : AppNavKey

@Serializable
data object HelpKey : AppNavKey

@Serializable
data object AboutKey : AppNavKey
