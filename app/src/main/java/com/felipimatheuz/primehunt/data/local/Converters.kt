package com.felipimatheuz.primehunt.data.local

import androidx.room.TypeConverter
import com.felipimatheuz.primehunt.domain.model.enums.GoalIcons
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.DropRarity
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType
import com.felipimatheuz.primehunt.domain.model.enums.PrimeType
import com.felipimatheuz.primehunt.domain.model.enums.RelicEra
import com.felipimatheuz.primehunt.domain.model.enums.RelicSource

class Converters {
    @TypeConverter
    fun fromRelicEra(value: RelicEra): String = value.name

    @TypeConverter
    fun toRelicEra(value: String): RelicEra = RelicEra.fromString(value)

    @TypeConverter
    fun fromRelicSource(value: RelicSource): String = value.name

    @TypeConverter
    fun toRelicSource(value: String): RelicSource = RelicSource.fromString(value)

    @TypeConverter
    fun fromPrimeType(value: PrimeType): String = value.name

    @TypeConverter
    fun toPrimeType(value: String): PrimeType = PrimeType.fromString(value)

    @TypeConverter
    fun fromPrimePartType(value: PrimePartType): String = value.name

    @TypeConverter
    fun toPrimePartType(value: String): PrimePartType = PrimePartType.fromString(value)

    @TypeConverter
    fun fromDropRarity(value: DropRarity): String = value.name

    @TypeConverter
    fun toDropRarity(value: String): DropRarity = DropRarity.fromString(value)

    @TypeConverter
    fun fromGoalStatus(value: GoalStatus): String = value.name

    @TypeConverter
    fun toGoalStatus(value: String): GoalStatus = GoalStatus.valueOf(value)

    @TypeConverter
    fun fromGoalTargetType(value: GoalTargetType): String = value.name

    @TypeConverter
    fun toGoalTargetType(value: String): GoalTargetType = GoalTargetType.valueOf(value)

    @TypeConverter
    fun fromGoalIcons(value: GoalIcons): String = value.name

    @TypeConverter
    fun toGoalIcons(value: String): GoalIcons = GoalIcons.valueOf(value)
}
