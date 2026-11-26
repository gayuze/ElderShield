package com.eldershield

import com.eldershield.domain.engines.FinancialScamEngine
import com.eldershield.domain.engines.ScamTextEngine
import com.eldershield.domain.engines.UrlRiskEngine
import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineTests {

    @Test
    fun testFakeVirusScarewareDetection() {
        val sample = "WARNING: Your phone is infected with 13 viruses! Battery is severely damaged. Install Antivirus Cleaner now!"
        val result = ScamTextEngine.analyzeText(sample)

        assertTrue(result.isThreat)
        assertNotNull(result.primaryFinding)
        assertEquals(ThreatCategory.ADVERTISEMENT_SCAM, result.primaryFinding?.category)
        assertEquals(SeverityLevel.HIGH, result.primaryFinding?.severity)
        assertTrue(result.detectedIndicators.isNotEmpty())
    }

    @Test
    fun testLotteryScamDetection() {
        val sample = "Congratulations! You won 25 lakh rupees in KBC Lucky Draw! Claim your prize now."
        val result = ScamTextEngine.analyzeText(sample)

        assertTrue(result.isThreat)
        assertEquals(ThreatCategory.ADVERTISEMENT_SCAM, result.primaryFinding?.category)
        assertTrue(result.detectedIndicators.any { it.contains("Prize Scam") })
    }

    @Test
    fun testAadhaarAndPanMasking() {
        val sample = "Customer Aadhaar 4589 1234 9876 and PAN ABCDE1234F"
        val result = ScamTextEngine.analyzeText(sample)

        assertTrue(result.isThreat)
        assertEquals(ThreatCategory.IDENTITY_EXPOSURE, result.primaryFinding?.category)
        assertTrue(result.exposedIdentityItems.any { it.contains("XXXX XXXX 9876") })
        assertTrue(result.exposedIdentityItems.any { it.contains("ABXXXXX4F") })
    }

    @Test
    fun testSuspiciousUrlDetection() {
        // IP Address URL
        val ipResult = UrlRiskEngine.inspectUrl("http://192.168.1.105/login")
        assertTrue(ipResult.isSuspicious)
        assertEquals(SeverityLevel.HIGH, ipResult.finding.severity)

        // Phishing keyword spoof
        val spoofResult = UrlRiskEngine.inspectUrl("https://sbi-login-verify.xyz/account")
        assertTrue(spoofResult.isSuspicious)
        assertTrue(spoofResult.indicators.any { it.contains("High-risk domain extension") })

        // URL Shortener
        val shortResult = UrlRiskEngine.inspectUrl("https://bit.ly/claim-prize")
        assertTrue(shortResult.isSuspicious)
        assertTrue(shortResult.indicators.any { it.contains("URL shortener") })

        // Safe official site
        val safeResult = UrlRiskEngine.inspectUrl("https://onlinesbi.sbi")
        assertFalse(safeResult.isSuspicious)
        assertEquals(SeverityLevel.LOW, safeResult.finding.severity)
    }

    @Test
    fun testFinancialUpiFraudDetection() {
        val sample = "Scan this QR code and enter UPI PIN to receive ₹3,000 refund"
        val result = FinancialScamEngine.evaluateFinancialMessage(sample)

        assertTrue(result.isFraudulent)
        assertEquals(SeverityLevel.CRITICAL, result.finding.severity)
        assertEquals(ThreatCategory.FINANCIAL_FRAUD, result.finding.category)
        assertTrue(result.tips.any { it.contains("You only enter PIN to SEND money, never to receive") })
    }
}
