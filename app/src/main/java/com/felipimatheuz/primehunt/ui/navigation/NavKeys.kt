package com.felipimatheuz.primehunt.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.felipimatheuz.primehunt.R
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    val icon: Int
    val label: Int

    companion object {
        val topLevelRoutes = setOf(
            OverviewKey,
            PrimeSetsKey(),
            OtherPrimesKey(),
            RelicsKey(),
            SyncKey,
            HelpKey,
            AboutKey
        )
    }
}

@Serializable
data object OverviewKey : AppNavKey {
    override val icon = R.drawable.ic_graph
    override val label = R.string.menu_overview
}

@Serializable
data class PrimeSetsKey(val filter: String = "SHOW_ALL") : AppNavKey {
    override val icon = R.drawable.ic_excalibur_prime
    override val label = R.string.menu_prime_sets
}

@Serializable
data class OtherPrimesKey(val filter: String = "SHOW_ALL") : AppNavKey {
    override val icon = R.drawable.ic_lato_prime
    override val label = R.string.menu_other_prime
}

@Serializable
data class RelicsKey(val filter: String = "SHOW_ALL") : AppNavKey {
    override val icon = R.drawable.ic_relic
    override val label = R.string.menu_relics
}

@Serializable
data object SyncKey : AppNavKey {
    override val icon = R.drawable.ic_google
    override val label = R.string.menu_sync
}

@Serializable
data object HelpKey : AppNavKey {
    override val icon = R.drawable.ic_help
    override val label = R.string.menu_help
}

@Serializable
data object AboutKey : AppNavKey {
    override val icon = R.drawable.ic_info
    override val label = R.string.menu_about
}
