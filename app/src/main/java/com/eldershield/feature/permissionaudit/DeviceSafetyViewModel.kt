package com.eldershield.feature.permissionaudit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldershield.data.dao.AppSecurityDao
import com.eldershield.data.entities.PermissionSnapshot
import com.eldershield.domain.engines.AppSecurityReport
import com.eldershield.domain.engines.DeviceSecurityEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceSafetyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSecurityDao: AppSecurityDao
) : ViewModel() {

    private val _appReports = MutableStateFlow<List<AppSecurityReport>>(emptyList())
    val appReports: StateFlow<List<AppSecurityReport>> = _appReports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshAudit()
    }

    fun refreshAudit() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val reports = DeviceSecurityEngine.auditInstalledApps(context)
            _appReports.value = reports

            // Persist snapshots to Room database
            try {
                val snapshots = reports.map { it.appSnapshot }
                appSecurityDao.insertApps(snapshots)

                val permissionSnapshots = mutableListOf<PermissionSnapshot>()
                for (r in reports) {
                    for (perm in r.grantedSensitivePermissions) {
                        permissionSnapshots.add(
                            PermissionSnapshot(
                                packageName = r.appSnapshot.packageName,
                                permissionName = perm,
                                isGranted = true,
                                isSensitive = true
                            )
                        )
                    }
                }
                appSecurityDao.insertPermissions(permissionSnapshots)
            } catch (e: Exception) {
                // Ignore Room sync failure
            }

            _isLoading.value = false
        }
    }

    fun openAppSettings(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore
        }
    }
}
