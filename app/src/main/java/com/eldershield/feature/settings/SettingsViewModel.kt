package com.eldershield.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldershield.core.preferences.UserPreferences
import com.eldershield.core.preferences.UserPreferencesRepository
import com.eldershield.data.dao.UserContactDao
import com.eldershield.data.entities.ConsentRecord
import com.eldershield.data.entities.TrustedContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val userContactDao: UserContactDao
) : ViewModel() {

    val preferencesState: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val trustedContactsFlow: Flow<List<TrustedContact>> = userContactDao.getContactsFlow()

    fun setLanguage(code: String) {
        viewModelScope.launch {
            preferencesRepository.updateLanguage(code)
        }
    }

    fun setLargeText(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setLargeText(enabled)
        }
    }

    fun setVoiceGuidance(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setVoiceGuidance(enabled)
        }
    }

    fun setFamilyAssistance(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFamilyAssistance(enabled)
            userContactDao.recordConsent(
                ConsentRecord(
                    userProfileId = 1,
                    featureKey = "FAMILY_ASSISTANCE",
                    isGranted = enabled,
                    consentTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun addTrustedContact(name: String, phone: String, relationship: String) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch {
            userContactDao.insertContact(
                TrustedContact(
                    userProfileId = 1,
                    name = name,
                    phoneNumber = phone,
                    relationship = relationship,
                    isAlertSyncEnabled = true
                )
            )
        }
    }

    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            userContactDao.deleteContact(contactId)
        }
    }
}
