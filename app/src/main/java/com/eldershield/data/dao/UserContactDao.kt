package com.eldershield.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eldershield.data.entities.ConsentRecord
import com.eldershield.data.entities.TrustedContact
import com.eldershield.data.entities.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile): Long

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    // Trusted contacts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: TrustedContact): Long

    @Query("SELECT * FROM trusted_contacts ORDER BY addedAt DESC")
    fun getContactsFlow(): Flow<List<TrustedContact>>

    @Query("DELETE FROM trusted_contacts WHERE id = :contactId")
    suspend fun deleteContact(contactId: Long)

    // Consent records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordConsent(record: ConsentRecord): Long

    @Query("SELECT * FROM consent_records WHERE featureKey = :featureKey ORDER BY consentTimestamp DESC LIMIT 1")
    suspend fun getLatestConsent(featureKey: String): ConsentRecord?

    @Query("SELECT isGranted FROM consent_records WHERE featureKey = :featureKey ORDER BY consentTimestamp DESC LIMIT 1")
    fun isFeatureConsentGrantedFlow(featureKey: String): Flow<Boolean?>
}
