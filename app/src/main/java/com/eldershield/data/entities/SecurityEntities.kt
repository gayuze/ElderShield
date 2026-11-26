package com.eldershield.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eldershield.domain.models.SecurityStatus
import com.eldershield.domain.models.SeverityLevel
import com.eldershield.domain.models.ThreatCategory

/**
 * 1. UserProfile: Basic local preferences and identity configuration.
 */
@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val displayName: String,
    val preferredLanguage: String = "en", // "en" or "ta"
    val isLargeTextEnabled: Boolean = false,
    val isVoiceGuidanceEnabled: Boolean = true,
    val isFamilyAssistanceEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 2. TrustedContact: Family member or guardian contact for assistance.
 */
@Entity(tableName = "trusted_contacts")
data class TrustedContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userProfileId: Long = 1,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val email: String? = null,
    val isAlertSyncEnabled: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * 3. ConsentRecord: Explicit record of user consent for features (e.g. family sharing, usage stats).
 */
@Entity(tableName = "consent_records")
data class ConsentRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userProfileId: Long = 1,
    val featureKey: String, // e.g., "FAMILY_SHARING", "USAGE_STATS_AUDIT"
    val isGranted: Boolean,
    val consentTimestamp: Long = System.currentTimeMillis(),
    val revokedTimestamp: Long? = null
)

/**
 * 4. AppSnapshot: Inventory of installed applications and metadata.
 */
@Entity(tableName = "app_snapshots")
data class AppSnapshot(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val installerPackageName: String? = null,
    val isSideloaded: Boolean = false,
    val sha256Checksum: String? = null,
    val firstInstalledTime: Long = 0L,
    val lastUpdatedTime: Long = 0L,
    val snapshotTimestamp: Long = System.currentTimeMillis()
)

/**
 * 5. PermissionSnapshot: Tracks permissions declared & granted by each application.
 */
@Entity(tableName = "permission_snapshots")
data class PermissionSnapshot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val permissionName: String,
    val isGranted: Boolean,
    val isSensitive: Boolean, // Camera, Mic, SMS, Location, Contacts, etc.
    val detectedAt: Long = System.currentTimeMillis()
)

/**
 * 6. UsageSample: Application foreground / background usage metrics for baseline analysis.
 */
@Entity(tableName = "usage_samples")
data class UsageSample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val totalTimeInForegroundMs: Long,
    val lastTimeUsedMs: Long,
    val sampleDate: String, // YYYY-MM-DD
    val isAnomalous: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis()
)

/**
 * 7. ThreatFinding: Discrete security finding produced by the detection engines.
 */
@Entity(tableName = "threat_findings")
data class ThreatFinding(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userProfileId: Long = 1,
    val title: String,
    val category: ThreatCategory,
    val severity: SeverityLevel,
    val confidence: Float,
    val evidence: String,
    val explanation: String,
    val recommendedAction: String,
    val isResolved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 8. ScanRecord: Comprehensive log of scans executed by the user.
 */
@Entity(tableName = "scan_records")
data class ScanRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanType: String, // "FULL", "AD_SCREENSHOT", "URL", "PERMISSION"
    val overallStatus: SecurityStatus,
    val itemsScannedCount: Int,
    val concernsDetectedCount: Int,
    val summaryEn: String,
    val summaryTa: String,
    val executedAt: Long = System.currentTimeMillis()
)

/**
 * 9. IncidentRecord: Correlates high-risk threat findings into security incident history.
 */
@Entity(tableName = "incident_records")
data class IncidentRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val threatFindingId: Long,
    val actionTaken: String, // "RESOLVED", "IGNORED", "REPORTED_TO_FAMILY"
    val incidentTimestamp: Long = System.currentTimeMillis()
)

/**
 * 10. FamilyAlert: History of security summaries/alerts prepared for or shared with trusted contacts.
 */
@Entity(tableName = "family_alerts")
data class FamilyAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val incidentRecordId: Long,
    val recipientName: String,
    val recipientPhone: String,
    val alertMessage: String,
    val isSharedWithExplicitConsent: Boolean,
    val sharedAt: Long = System.currentTimeMillis()
)

/**
 * 11. LearningProgress: Tracks completion of digital safety lessons and quizzes.
 */
@Entity(tableName = "learning_progress")
data class LearningProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userProfileId: Long = 1,
    val lessonId: String, // e.g., "LESSON_UPI_SCAM", "LESSON_FAKE_ADS"
    val isCompleted: Boolean = false,
    val quizScore: Int = 0,
    val lastAccessedAt: Long = System.currentTimeMillis()
)
