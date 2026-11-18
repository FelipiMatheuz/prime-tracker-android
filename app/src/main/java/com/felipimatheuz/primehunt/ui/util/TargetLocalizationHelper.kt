package com.felipimatheuz.primehunt.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType

object TargetLocalizationHelper {

    @Composable
    fun getLocalizedTargetName(targetName: String, targetType: GoalTargetType, targetId: String = ""): String {
        return when (targetType) {
            GoalTargetType.PRIME_SET -> targetName
            GoalTargetType.RELIC -> targetName
            GoalTargetType.OTHER -> {
                if (targetId == "forma_blueprint" || targetName.contains("Forma", ignoreCase = true)) {
                    stringResource(R.string.forma_blueprint)
                } else {
                    targetName
                }
            }
            GoalTargetType.PRIME_PART -> {
                normalizeRewardName(targetName)
            }
        }
    }

    fun determineTargetType(targetId: String, targetName: String): GoalTargetType {
        return when {
            targetId == "forma_blueprint" || targetId == "ducat" -> GoalTargetType.OTHER
            targetId.startsWith("lith_") || targetId.startsWith("meso_") || targetId.startsWith("neo_") || targetId.startsWith("axi_") -> GoalTargetType.RELIC
            else -> {
                val lowerName = targetName.lowercase()
                val isPart = PrimePartType.entries.any { partType ->
                    if (partType == PrimePartType.PRIME_SET) return@any false
                    val partStr = partType.name.replace("_", " ").lowercase()
                    lowerName.contains(partStr)
                }
                if (isPart || targetName.contains("Blueprint", ignoreCase = true)) {
                    GoalTargetType.PRIME_PART
                } else {
                    GoalTargetType.PRIME_SET
                }
            }
        }
    }

    @Composable
    private fun normalizeRewardName(name: String): String {
        val primeIndex = name.indexOf("Prime")
        if (primeIndex == -1) {
            return name
        }

        val immutablePart = name.substring(0, primeIndex + "Prime".length).trim()
        val translatablePart = name.substring(primeIndex + "Prime".length).trim()
        if (translatablePart.isEmpty()) return name
        val primePart = PrimePartType.fromString(translatablePart)
        if (primePart == PrimePartType.PRIME_SET) {
            return "$immutablePart: $translatablePart"
        }

        return buildString {
            append(immutablePart.replaceFirstChar { it.uppercase() })
            append(": ")
            append(stringResource(primePart.text))
        }
    }
}
