package com.eldershield.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldershield.data.dao.ScanDao
import com.eldershield.data.dao.ThreatDao
import com.eldershield.data.entities.ScanRecord
import com.eldershield.data.entities.ThreatFinding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val threatDao: ThreatDao,
    private val scanDao: ScanDao
) : ViewModel() {

    val threatsFlow: Flow<List<ThreatFinding>> = threatDao.getAllThreatsFlow()
    val scansFlow: Flow<List<ScanRecord>> = scanDao.getAllScansFlow()

    fun clearHistory() {
        viewModelScope.launch {
            threatDao.clearAllThreats()
            scanDao.clearScanHistory()
        }
    }
}
