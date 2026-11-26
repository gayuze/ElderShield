package com.eldershield.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eldershield.data.entities.FamilyAlert
import com.eldershield.data.entities.IncidentRecord
import com.eldershield.data.entities.ThreatFinding
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreat(threat: ThreatFinding): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreats(threats: List<ThreatFinding>)

    @Update
    suspend fun updateThreat(threat: ThreatFinding)

    @Query("SELECT * FROM threat_findings ORDER BY createdAt DESC")
    fun getAllThreatsFlow(): Flow<List<ThreatFinding>>

    @Query("SELECT * FROM threat_findings WHERE isResolved = 0 ORDER BY createdAt DESC")
    fun getActiveThreatsFlow(): Flow<List<ThreatFinding>>

    @Query("SELECT COUNT(*) FROM threat_findings WHERE isResolved = 0")
    fun getActiveThreatCountFlow(): Flow<Int>

    @Query("DELETE FROM threat_findings")
    suspend fun clearAllThreats()

    // Incident records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentRecord): Long

    @Query("SELECT * FROM incident_records ORDER BY incidentTimestamp DESC")
    fun getAllIncidentsFlow(): Flow<List<IncidentRecord>>

    // Family alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyAlert(alert: FamilyAlert): Long

    @Query("SELECT * FROM family_alerts ORDER BY sharedAt DESC")
    fun getAllFamilyAlertsFlow(): Flow<List<FamilyAlert>>
}
