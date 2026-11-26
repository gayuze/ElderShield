package com.eldershield.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.eldershield.data.entities.AppSnapshot
import com.eldershield.data.entities.PermissionSnapshot
import com.eldershield.data.entities.UsageSample
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSecurityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppSnapshot>)

    @Query("SELECT * FROM app_snapshots ORDER BY appName ASC")
    fun getAllAppsFlow(): Flow<List<AppSnapshot>>

    @Query("SELECT COUNT(*) FROM app_snapshots")
    fun getInstalledAppCountFlow(): Flow<Int>

    // Permissions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermissions(permissions: List<PermissionSnapshot>)

    @Query("SELECT * FROM permission_snapshots WHERE packageName = :packageName")
    suspend fun getPermissionsForApp(packageName: String): List<PermissionSnapshot>

    @Query("SELECT * FROM permission_snapshots WHERE isSensitive = 1 AND isGranted = 1")
    fun getSensitiveGrantedPermissionsFlow(): Flow<List<PermissionSnapshot>>

    @Query("SELECT COUNT(DISTINCT packageName) FROM permission_snapshots WHERE isSensitive = 1 AND isGranted = 1")
    fun getSensitiveAppsCountFlow(): Flow<Int>

    // Usage
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageSample(sample: UsageSample)

    @Query("SELECT * FROM usage_samples WHERE isAnomalous = 1 ORDER BY recordedAt DESC")
    fun getAnomalousUsageFlow(): Flow<List<UsageSample>>

    @Transaction
    @Query("DELETE FROM app_snapshots")
    suspend fun clearAppInventory()
}
