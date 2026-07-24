package com.felipimatheuz.primehunt.data.remote

import androidx.room.TypeConverter
import com.felipimatheuz.primehunt.data.remote.enums.RelicEra
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.data.remote.enums.DropRarity
import com.felipimatheuz.primehunt.data.remote.enums.PrimePartType
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType

class Converters {
    @TypeConverter
    fun fromRelicEra(value: RelicEra): String = value.name

    @TypeConverter
    fun toRelicEra(value: String): RelicEra = RelicEra.valueOf(value)

    @TypeConverter
    fun fromRelicSource(value: RelicSource): String = value.name

    @TypeConverter
    fun toRelicSource(value: String): RelicSource = RelicSource.valueOf(value)

    @TypeConverter
    fun fromPrimeType(value: PrimeType): String = value.name

    @TypeConverter
    fun toPrimeType(value: String): PrimeType = PrimeType.valueOf(value)

    @TypeConverter
    fun fromPrimePartType(value: PrimePartType): String = value.name

    @TypeConverter
    fun toPrimePartType(value: String): PrimePartType = PrimePartType.valueOf(value)

    @TypeConverter
    fun fromDropRarity(value: DropRarity): String = value.name

    @TypeConverter
    fun toDropRarity(value: String): DropRarity = DropRarity.valueOf(value)
}
