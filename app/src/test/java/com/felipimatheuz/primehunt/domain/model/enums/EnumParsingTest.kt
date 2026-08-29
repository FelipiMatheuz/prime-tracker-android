package com.felipimatheuz.primehunt.domain.model.enums

import org.junit.Assert.assertEquals
import org.junit.Test

class EnumParsingTest {

    @Test
    fun `relic era parsing is case-insensitive`() {
        assertEquals(RelicEra.AXI, RelicEra.fromString("Axi"))
        assertEquals(RelicEra.AXI, RelicEra.fromString("axi"))
        assertEquals(RelicEra.AXI, RelicEra.fromString("AXI"))
        assertEquals(RelicEra.LITH, RelicEra.fromString("Lith"))
    }

    @Test
    fun `relic era parsing handles unknown values safely`() {
        assertEquals(RelicEra.AXI, RelicEra.fromString("Unknown"))
    }

    @Test
    fun `relic source parsing is case-insensitive`() {
        assertEquals(RelicSource.MISSION, RelicSource.fromString("Mission"))
        assertEquals(RelicSource.VAULT, RelicSource.fromString("vault"))
    }

    @Test
    fun `drop rarity parsing is case-insensitive`() {
        assertEquals(DropRarity.RARE, DropRarity.fromString("Rare"))
        assertEquals(DropRarity.COMMON, DropRarity.fromString("common"))
    }

    @Test
    fun `prime type parsing is case-insensitive`() {
        assertEquals(PrimeType.WARFRAME, PrimeType.fromString("Warframe"))
        assertEquals(PrimeType.MELEE, PrimeType.fromString("melee"))
    }

    @Test
    fun `prime part type parsing is case-insensitive`() {
        assertEquals(PrimePartType.BLUEPRINT, PrimePartType.fromString("Blueprint"))
        assertEquals(PrimePartType.CHASSIS, PrimePartType.fromString("chassis"))
    }
}
