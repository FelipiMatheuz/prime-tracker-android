package com.felipimatheuz.primehunt.business.state

import com.felipimatheuz.primehunt.R

sealed class BottomNavItem(var title: Int, var icon: Int, var screenRoute: String, var filter: String) {

    object Overview : BottomNavItem(R.string.menu_overview, R.drawable.ic_graph, "overview", "")
    object PrimeSets : BottomNavItem(R.string.menu_prime_sets, R.drawable.ic_excalibur_prime, "prime_sets/{filter}", "set_filter")
    object OtherPrimes : BottomNavItem(R.string.menu_other_prime, R.drawable.ic_lato_prime, "other_primes/{filter}", "other_filter")
    object Relics : BottomNavItem(R.string.menu_relics, R.drawable.ic_relic, "relic_list/{filter}", "relic_filter")

        companion object {
        fun getList() = listOf(
            Overview, PrimeSets, OtherPrimes, Relics
        )
    }
}