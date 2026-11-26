package com.eldershield

import com.eldershield.core.database.Converters
import com.eldershield.domain.models.SecurityStatus
import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setup() {
        converters = Converters()
    }

    @Test
    fun testSecurityStatusConverter() {
        val original = SecurityStatus.SAFE
        val converted = converters.fromSecurityStatus(original)
        assertEquals("SAFE", converted)
        val restored = converters.toSecurityStatus(converted)
        assertEquals(original, restored)

        assertNull(converters.fromSecurityStatus(null))
        assertNull(converters.toSecurityStatus(null))
    }

    @Test
    fun testSeverityLevelConverter() {
        val original = SeverityLevel.CRITICAL
        val converted = converters.fromSeverityLevel(original)
        assertEquals("CRITICAL", converted)
        val restored = converters.toSeverityLevel(converted)
        assertEquals(original, restored)
    }

    @Test
    fun testThreatCategoryConverter() {
        val original = ThreatCategory.ADVERTISEMENT_SCAM
        val converted = converters.fromThreatCategory(original)
        assertEquals("ADVERTISEMENT_SCAM", converted)
        val restored = converters.toThreatCategory(converted)
        assertEquals(original, restored)
    }
}
