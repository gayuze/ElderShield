package com.eldershield.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eldershield.data.dao.AppSecurityDao
import com.eldershield.data.dao.LearningDao
import com.eldershield.data.dao.ScanDao
import com.eldershield.data.dao.ThreatDao
import com.eldershield.data.dao.UserContactDao
import com.eldershield.data.entities.AppSnapshot
import com.eldershield.data.entities.ConsentRecord
import com.eldershield.data.entities.FamilyAlert
import com.eldershield.data.entities.IncidentRecord
import com.eldershield.data.entities.LearningProgress
import com.eldershield.data.entities.PermissionSnapshot
import com.eldershield.data.entities.ScanRecord
import com.eldershield.data.entities.ThreatFinding
import com.eldershield.data.entities.TrustedContact
import com.eldershield.data.entities.UsageSample
import com.eldershield.data.entities.UserProfile

@Database(
    entities = [
        UserProfile::class,
        TrustedContact::class,
        ConsentRecord::class,
        AppSnapshot::class,
        PermissionSnapshot::class,
        UsageSample::class,
        ThreatFinding::class,
        ScanRecord::class,
        IncidentRecord::class,
        FamilyAlert::class,
        LearningProgress::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ElderShieldDatabase : RoomDatabase() {
    abstract fun threatDao(): ThreatDao
    abstract fun scanDao(): ScanDao
    abstract fun appSecurityDao(): AppSecurityDao
    abstract fun userContactDao(): UserContactDao
    abstract fun learningDao(): LearningDao

    companion object {
        const val DATABASE_NAME = "eldershield_secure.db"
    }
}
