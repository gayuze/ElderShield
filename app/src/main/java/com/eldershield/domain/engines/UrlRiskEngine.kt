package com.eldershield.domain.engines

import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory
import com.eldershield.domain.models.ThreatFindingModel
import java.net.URI

object UrlRiskEngine {

    data class UrlAnalysisResult(
        val isSuspicious: Boolean,
        val finding: ThreatFindingModel,
        val indicators: List<String>,
        val isHttps: Boolean,
        val host: String
    )

    private val SHORTENER_DOMAINS = setOf(
        "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "is.gd",
        "buff.ly", "adf.ly", "bit.do", "cutt.ly", "rb.gy"
    )

    private val HIGH_RISK_TLDS = setOf(
        "xyz", "top", "tk", "ml", "ga", "cf", "gq", "click", "buzz",
        "fit", "work", "loan", "men", "icu", "party", "country"
    )

    private val SPOOF_KEYWORDS = listOf(
        "sbi", "hdfc", "icici", "pnb", "axis", "paytm", "phonepe", "gpay",
        "kyc", "aadhaar", "pan-card", "reward", "lottery", "gift", "bonus",
        "verify-account", "login-bank", "security-alert", "apk-download"
    )

    private val IP_PATTERN = Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")

    fun inspectUrl(rawUrl: String): UrlAnalysisResult {
        val sanitized = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else rawUrl

        val indicators = mutableListOf<String>()
        val uri = try {
            URI(sanitized)
        } catch (e: Exception) {
            null
        }

        val host = uri?.host?.lowercase() ?: rawUrl.lowercase()
        val isHttps = sanitized.startsWith("https://", ignoreCase = true)

        if (!isHttps) {
            indicators.add("Unencrypted HTTP connection: Any information you enter can be intercepted.")
        }

        // 1. IP Address check
        if (IP_PATTERN.matches(host)) {
            indicators.add("Direct IP Address host: Legitimate banks and services use standard domain names, not raw IP addresses.")
        }

        // 2. Shortener check
        if (SHORTENER_DOMAINS.contains(host)) {
            indicators.add("Hidden URL shortener ($host): Conceals the actual destination website.")
        }

        // 3. High-Risk TLD check
        val tld = host.substringAfterLast('.', "")
        if (HIGH_RISK_TLDS.contains(tld)) {
            indicators.add("High-risk domain extension (.$tld): Frequently used for temporary scam and phishing pages.")
        }

        // 4. Keyword Spoofing check
        val matchedSpoofs = SPOOF_KEYWORDS.filter { host.contains(it) }
        val isOfficial = isLikelyOfficialDomain(host)
        if (matchedSpoofs.isNotEmpty() && !isOfficial) {
            indicators.add("Brand or financial keyword detected outside official domain: ${matchedSpoofs.joinToString(", ")}")
        }

        val isSuspicious = indicators.isNotEmpty()
        val severity = when {
            IP_PATTERN.matches(host) || (matchedSpoofs.isNotEmpty() && !isOfficial) -> SeverityLevel.HIGH
            indicators.size >= 2 -> SeverityLevel.HIGH
            indicators.isNotEmpty() -> SeverityLevel.MEDIUM
            else -> SeverityLevel.LOW
        }

        val finding = if (isSuspicious) {
            ThreatFindingModel(
                title = if (severity == SeverityLevel.HIGH) "High-Risk Suspicious Link Detected" else "Caution Recommended on Link",
                category = ThreatCategory.PHISHING_URL,
                severity = severity,
                confidence = if (severity == SeverityLevel.HIGH) 0.90f else 0.70f,
                evidence = indicators.joinToString(" | "),
                explanation = "This website link displays warning signs common to phishing pages or hidden destinations. Note: An HTTPS padlock does NOT guarantee a website is legitimate.",
                recommendedAction = "Do NOT enter your passwords, PINs, or bank account numbers on this page. Close the link."
            )
        } else {
            ThreatFindingModel(
                title = "No Obvious Link Warning Signs",
                category = ThreatCategory.PHISHING_URL,
                severity = SeverityLevel.LOW,
                confidence = 0.85f,
                evidence = "Standard domain structure with HTTPS encryption.",
                explanation = "The link uses standard security protocols. However, always exercise care before entering sensitive credentials.",
                recommendedAction = "Safe to view with normal digital caution."
            )
        }

        return UrlAnalysisResult(
            isSuspicious = isSuspicious,
            finding = finding,
            indicators = indicators,
            isHttps = isHttps,
            host = host
        )
    }

    private fun isLikelyOfficialDomain(host: String): Boolean {
        val officialRoots = listOf(
            "onlinesbi.sbi", "sbi.co.in", "hdfcbank.com", "icicibank.com",
            "axisbank.com", "paytm.com", "google.com", "phonepe.com",
            "amazon.in", "amazon.com", "flipkart.com", "gov.in", "nic.in"
        )
        return officialRoots.any { host == it || host.endsWith(".$it") }
    }
}
