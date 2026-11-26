package com.eldershield.domain.models

enum class SecurityStatus {
    SAFE,
    ATTENTION,
    SUSPICIOUS,
    CRITICAL,
    PENDING_CHECKS
}

enum class SeverityLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class ThreatCategory {
    ADVERTISEMENT_SCAM,
    PHISHING_URL,
    EXCESSIVE_PERMISSIONS,
    BEHAVIOR_ANOMALY,
    IDENTITY_EXPOSURE,
    FINANCIAL_FRAUD,
    UNTRUSTED_APP,
    REMOTE_ACCESS_RISK
}

data class ThreatFindingModel(
    val id: Long = 0,
    val title: String,
    val category: ThreatCategory,
    val severity: SeverityLevel,
    val confidence: Float, // 0.0 to 1.0
    val evidence: String,
    val explanation: String,
    val recommendedAction: String,
    val timestamp: Long = System.currentTimeMillis()
)
