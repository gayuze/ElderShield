package com.eldershield.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eldershield.data.entities.ScanRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanRecord): Long

    @Query("SELECT * FROM scan_records ORDER BY executedAt DESC LIMIT 1")
    fun getLatestScanFlow(): Flow<ScanRecord?>

    @Query("SELECT * FROM scan_records ORDER BY executedAt DESC")
    fun getAllScansFlow(): Flow<List<ScanRecord>>

    @Query("DELETE FROM scan_records")
    suspend fun clearScanHistory()
}
