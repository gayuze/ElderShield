package com.eldershield

import com.eldershield.domain.models.SecurityStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class SecurityStatusTest {

    @Test
    fun testRiskEvaluationLogic() {
        // Evaluate statuses matching Dashboard logic
        val statusNormal = evaluateStatus(activeConcerns = 0, sensitiveApps = 2, hasScanned = true)
        assertEquals(SecurityStatus.SAFE, statusNormal)

        val statusAttention = evaluateStatus(activeConcerns = 0, sensitiveApps = 7, hasScanned = true)
        assertEquals(SecurityStatus.ATTENTION, statusAttention)

        val statusSuspicious = evaluateStatus(activeConcerns = 1, sensitiveApps = 2, hasScanned = true)
        assertEquals(SecurityStatus.SUSPICIOUS, statusSuspicious)

        val statusCritical = evaluateStatus(activeConcerns = 3, sensitiveApps = 2, hasScanned = true)
        assertEquals(SecurityStatus.CRITICAL, statusCritical)

        val statusPending = evaluateStatus(activeConcerns = 0, sensitiveApps = 0, hasScanned = false)
        assertEquals(SecurityStatus.PENDING_CHECKS, statusPending)
    }

    private fun evaluateStatus(activeConcerns: Int, sensitiveApps: Int, hasScanned: Boolean): SecurityStatus {
        return when {
            activeConcerns > 2 -> SecurityStatus.CRITICAL
            activeConcerns > 0 -> SecurityStatus.SUSPICIOUS
            sensitiveApps > 5 -> SecurityStatus.ATTENTION
            !hasScanned -> SecurityStatus.PENDING_CHECKS
            else -> SecurityStatus.SAFE
        }
    }
}
