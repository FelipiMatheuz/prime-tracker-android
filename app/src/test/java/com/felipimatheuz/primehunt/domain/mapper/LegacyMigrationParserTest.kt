package com.felipimatheuz.primehunt.domain.mapper

import org.junit.Assert.assertEquals
import org.junit.Test

class LegacyMigrationParserTest {

    private val parser = LegacyMigrationParser()

    @Test
    fun `parse SET category should remove bundle prefix and numeric suffix`() {
        val data = mapOf(
            "Gauss_Gauss_NEUROPTICS_0" to true,
            "Gauss_Acceltra_BLUEPRINT" to true
        )
        val result = parser.parse(data, isSetCategory = true)

        assertEquals(1, result["Gauss_NEUROPTICS"])
        assertEquals(1, result["Acceltra_BLUEPRINT"])
    }

    @Test
    fun `parse should accumulate quantities for same part`() {
        val data = mapOf(
            "Fang_BLADE_0" to true,
            "Fang_BLADE_1" to true
        )
        val result = parser.parse(data, isSetCategory = false)

        assertEquals(2, result["Fang_BLADE"])
    }

    @Test
    fun `parse should normalize part name BLADE when first segment is venka`() {
        val data = mapOf(
            "Venka_BLADE_0" to true,
            "Venka_BLADE_1" to true
        )
        val result = parser.parse(data, isSetCategory = false)

        assertEquals(2, result["Venka_BLADES"])
    }

    @Test
    fun `parse should normalize part names`() {
        val data = mapOf(
            "Hydroid_Ballistica_ULIMB_0" to true,
            "Hydroid_Ballistica_LLIMB_0" to true
        )
        val result = parser.parse(data, isSetCategory = true)

        assertEquals(1, result["Ballistica_UPPER_LIMB"])
        assertEquals(1, result["Ballistica_LOWER_LIMB"])
    }

    @Test
    fun `normalizeNewName should remove Prime suffix`() {
        assertEquals("Ash", parser.normalizeNewName("Ash Prime"))
        assertEquals("Lex", parser.normalizeNewName("Lex prime"))
    }
}
