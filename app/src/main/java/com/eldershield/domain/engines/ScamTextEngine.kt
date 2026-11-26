package com.eldershield.domain.engines

import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory
import com.eldershield.domain.models.ThreatFindingModel
import java.util.regex.Pattern

object ScamTextEngine {

    data class ScanAnalysisResult(
        val isThreat: Boolean,
        val primaryFinding: ThreatFindingModel?,
        val detectedIndicators: List<String>,
        val exposedIdentityItems: List<String>
    )

    private val VIRUS_PATTERNS = listOf(
        Pattern.compile("(?i)(phone|device|system)\\s*(is)?\\s*(infected|damaged|hacked|compromised)"),
        Pattern.compile("(?i)(\\d+|several)\\s*(viruses|trojans|malware)\\s*(found|detected)"),
        Pattern.compile("(?i)(battery|storage)\\s*(is)?\\s*(severely|heavily)?\\s*(damaged|full)"),
        Pattern.compile("(?i)(install|download)\\s*(cleaner|booster|antivirus)\\s*(now|immediately)?"),
        Pattern.compile("(?i)(clean|repair|fix)\\s*(phone|system|device)\\s*now")
    )

    private val LOTTERY_PATTERNS = listOf(
        Pattern.compile("(?i)(congratulations|congrats).*?(won|winner).*?(prize|lottery|reward|cash)"),
        Pattern.compile("(?i)(claim|redeem)\\s*(your)?\\s*(reward|prize|cash|gift|bonus)\\s*(immediately|now)?"),
        Pattern.compile("(?i)(lucky\\s*draw|kbc|spin\\s*to\\s*win).*?(crore|lakh|rupees|\\$|₹)"),
        Pattern.compile("(?i)(free|instant)\\s*(recharge|cashback|gift\\s*card)")
    )

    private val URGENCY_PATTERNS = listOf(
        Pattern.compile("(?i)(will\\s*be|is)\\s*(blocked|suspended|deactivated|terminated)\\s*(within|in)?\\s*(\\d+\\s*(hours|minutes|days))?"),
        Pattern.compile("(?i)(immediate|urgent)\\s*(action|payment|verification|kyc)\\s*(required|needed)"),
        Pattern.compile("(?i)(electricity|power|sim)\\s*(disconnected|blocked)\\s*tonight"),
        Pattern.compile("(?i)(police|court|arrest)\\s*(notice|action|case|warrant)")
    )

    private val FINANCIAL_SCAM_PATTERNS = listOf(
        Pattern.compile("(?i)(enter|type|share)\\s*(upi\\s*pin|mpin|atm\\s*pin|otp)\\s*(to\\s*receive|for\\s*credit)"),
        Pattern.compile("(?i)(receive|get)\\s*(money|cash|refund).*?(scan|enter\\s*pin)"),
        Pattern.compile("(?i)(pay|send)\\s*(fee|charges|processing|tax)\\s*to\\s*release")
    )

    // Identity Regex Patterns
    private val AADHAAR_PATTERN = Pattern.compile("\\b[2-9]\\d{3}\\s\\d{4}\\s\\d{4}\\b")
    private val PAN_PATTERN = Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z]{1}\\b")
    private val PHONE_PATTERN = Pattern.compile("(?:\\+91|91)?[6-9]\\d{9}\\b")
    private val EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")

    fun analyzeText(rawText: String): ScanAnalysisResult {
        val indicators = mutableListOf<String>()
        val exposedIdentities = mutableListOf<String>()

        // 1. Check Identity Leak Exposure
        val aadhaarMatches = AADHAAR_PATTERN.matcher(rawText)
        while (aadhaarMatches.find()) {
            val masked = maskAadhaar(aadhaarMatches.group())
            exposedIdentities.add("Aadhaar Identifier ($masked)")
        }

        val panMatches = PAN_PATTERN.matcher(rawText)
        while (panMatches.find()) {
            val masked = maskPan(panMatches.group())
            exposedIdentities.add("PAN Identifier ($masked)")
        }

        val phoneMatches = PHONE_PATTERN.matcher(rawText)
        while (phoneMatches.find()) {
            exposedIdentities.add("Phone: ${maskPhone(phoneMatches.group())}")
        }

        val emailMatches = EMAIL_PATTERN.matcher(rawText)
        while (emailMatches.find()) {
            exposedIdentities.add("Email: ${maskEmail(emailMatches.group())}")
        }

        // 2. Check Threat Patterns
        var hasVirus = false
        for (pattern in VIRUS_PATTERNS) {
            val m = pattern.matcher(rawText)
            if (m.find()) {
                hasVirus = true
                indicators.add("Fake Virus Claim: '${m.group().take(40)}'")
            }
        }

        var hasLottery = false
        for (pattern in LOTTERY_PATTERNS) {
            val m = pattern.matcher(rawText)
            if (m.find()) {
                hasLottery = true
                indicators.add("Prize Scam Indicator: '${m.group().take(40)}'")
            }
        }

        var hasUrgency = false
        for (pattern in URGENCY_PATTERNS) {
            val m = pattern.matcher(rawText)
            if (m.find()) {
                hasUrgency = true
                indicators.add("Urgency & Extortion Indicator: '${m.group().take(40)}'")
            }
        }

        var hasFinancialFraud = false
        for (pattern in FINANCIAL_SCAM_PATTERNS) {
            val m = pattern.matcher(rawText)
            if (m.find()) {
                hasFinancialFraud = true
                indicators.add("Payment/PIN Trap Indicator: '${m.group().take(40)}'")
            }
        }

        // 3. Synthesize Primary Finding
        val primaryFinding: ThreatFindingModel? = when {
            hasFinancialFraud -> ThreatFindingModel(
                title = "Deceptive Financial / UPI Scam Detected",
                category = ThreatCategory.FINANCIAL_FRAUD,
                severity = SeverityLevel.CRITICAL,
                confidence = 0.95f,
                evidence = indicators.joinToString("; "),
                explanation = "This content requests your UPI PIN or payment under the false pretense of receiving money or rewards. You NEVER need to enter your PIN to receive money.",
                recommendedAction = "Never enter your UPI PIN, MPIN, or OTP. Cancel the transaction immediately."
            )
            hasVirus -> ThreatFindingModel(
                title = "Fake Security / Device Infection Scareware",
                category = ThreatCategory.ADVERTISEMENT_SCAM,
                severity = SeverityLevel.HIGH,
                confidence = if (hasUrgency) 0.95f else 0.90f,
                evidence = indicators.joinToString("; "),
                explanation = "This is a deceptive advertisement trying to frighten you into downloading unwanted software by falsely claiming your device is infected.",
                recommendedAction = "Do NOT click any download buttons. Close the webpage or advertisement immediately."
            )
            hasLottery -> ThreatFindingModel(
                title = "Deceptive Lottery / Prize Scam",
                category = ThreatCategory.ADVERTISEMENT_SCAM,
                severity = SeverityLevel.HIGH,
                confidence = 0.88f,
                evidence = indicators.joinToString("; "),
                explanation = "Claims of sudden cash, lottery wins, or unearned gifts are classic social-engineering tricks designed to steal banking details or advance fees.",
                recommendedAction = "Ignore and delete. Legitimate organizations do not ask for personal details to claim unentered lotteries."
            )
            exposedIdentities.isNotEmpty() -> ThreatFindingModel(
                title = "Sensitive Personal Identity Data Visible",
                category = ThreatCategory.IDENTITY_EXPOSURE,
                severity = SeverityLevel.MEDIUM,
                confidence = 0.90f,
                evidence = exposedIdentities.joinToString(", "),
                explanation = "This image contains sensitive personal identifiers (Aadhaar/PAN/Contact). Sharing it online or in chats could expose you to identity theft.",
                recommendedAction = "Mask or hide these numbers with photo editing before sharing this image with anyone."
            )
            indicators.isNotEmpty() -> ThreatFindingModel(
                title = "Suspicious Digital Message / Advertisement",
                category = ThreatCategory.ADVERTISEMENT_SCAM,
                severity = SeverityLevel.MEDIUM,
                confidence = 0.75f,
                evidence = indicators.joinToString("; "),
                explanation = "Contains suspicious language commonly associated with online misleading advertisements.",
                recommendedAction = "Exercise caution. Verify with trusted family members before taking any requested action."
            )
            else -> null
        }

        return ScanAnalysisResult(
            isThreat = primaryFinding != null,
            primaryFinding = primaryFinding,
            detectedIndicators = indicators,
            exposedIdentityItems = exposedIdentities
        )
    }

    private fun maskAadhaar(aadhaar: String): String =
        "XXXX XXXX " + aadhaar.trim().takeLast(4)

    private fun maskPan(pan: String): String =
        pan.take(2) + "XXXXX" + pan.takeLast(2)

    private fun maskPhone(phone: String): String =
        phone.take(3) + "XXXX" + phone.takeLast(3)

    private fun maskEmail(email: String): String {
        val parts = email.split("@")
        return if (parts.size == 2 && parts[0].length > 2) {
            "${parts[0].take(2)}***@${parts[1]}"
        } else email
    }
}
