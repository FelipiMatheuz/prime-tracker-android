package com.felipimatheuz.primehunt.data.local.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.felipimatheuz.primehunt.R

enum class GoalTargetType(@param:StringRes val label: Int, @DrawableRes val icon: Int) {
    PRIME_SET(R.string.goal_target_prime_set, R.drawable.ic_prime),
    PRIME_PART(R.string.goal_target_prime_part, R.drawable.prime_blueprint),
    RELIC(R.string.goal_target_relic, R.drawable.ic_relic),
    FORMA(R.string.goal_target_forma, R.drawable.ic_forma)
}