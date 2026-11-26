package com.eldershield.domain.engines

import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory
import com.eldershield.domain.models.ThreatFindingModel

object FinancialScamEngine {

    data class FinancialCheckResult(
        val isFraudulent: Boolean,
        val finding: ThreatFindingModel,
        val tips: List<String>
    )

    private val FRAUD_INDICATORS = listOf(
        "enter pin to receive" to "A scammer is trying to reverse payment. You only enter PIN to SEND money, never to receive.",
        "pay request" to "A 'Pay' or 'Collect' request takes money OUT of your bank account. Do not approve.",
        "scan qr to get refund" to "Scanning a QR code in a payment app authorizes a withdrawal, not a credit.",
        "electricity power bill" to "Fake power disconnection threats are common scams. Verify with your local electricity office.",
        "kyc suspended" to "Banks and payment apps never freeze accounts via WhatsApp or SMS links."
    )

    fun evaluateFinancialMessage(message: String): FinancialCheckResult {
        val lower = message.lowercase()
        val detectedTips = mutableListOf<String>()

        for ((phrase, tip) in FRAUD_INDICATORS) {
            if (lower.contains(phrase) || phrase.split(" ").all { lower.contains(it) }) {
                detectedTips.add(tip)
            }
        }

        val isFraud = detectedTips.isNotEmpty()
        val finding = if (isFraud) {
            ThreatFindingModel(
                title = "Financial / Payment Fraud Attempt Detected",
                category = ThreatCategory.FINANCIAL_FRAUD,
                severity = SeverityLevel.CRITICAL,
                confidence = 0.95f,
                evidence = detectedTips.joinToString(" | "),
                explanation = "This message matches known UPI or banking fraud patterns designed to trick you into authorizing an outgoing payment.",
                recommendedAction = "Do NOT approve any payment request. Never enter your UPI PIN or OTP. Contact your bank directly or call 1930."
            )
        } else {
            ThreatFindingModel(
                title = "No Specific Financial Scam Patterns Detected",
                category = ThreatCategory.FINANCIAL_FRAUD,
                severity = SeverityLevel.LOW,
                confidence = 0.80f,
                evidence = "No high-risk payment or PIN extortion keywords found.",
                explanation = "The text does not show obvious known scam patterns, but always verify unfamiliar payment requests.",
                recommendedAction = "Remember the golden rule: Never share your UPI PIN or SMS OTP with anyone."
            )
        }

        return FinancialCheckResult(
            isFraudulent = isFraud,
            finding = finding,
            tips = if (detectedTips.isEmpty()) listOf(
                "You NEVER enter a UPI PIN to receive money into your account.",
                "Bank officials will NEVER call or message asking for your OTP or password.",
                "If someone claims they sent you money by mistake and wants you to pay them back, check your official bank statement first."
            ) else detectedTips
        )
    }

    val bankingSafetyRules = listOf(
        "Golden Rule #1: UPI PIN is ONLY used for SENDING money. If someone asks for your PIN to receive a prize or refund, it is 100% a scam.",
        "Golden Rule #2: An SMS OTP is secret. Never read it out to any caller, even if they claim to be from SBI, HDFC, Google Pay, or the Police.",
        "Golden Rule #3: Never install AnyDesk, TeamViewer, or QuickSupport on your phone upon a caller's request.",
        "Emergency Reporting: If defrauded, immediately dial 1930 (National Cyber Crime Helpline) or report at cybercrime.gov.in within 24 hours to freeze stolen funds."
    )
}
