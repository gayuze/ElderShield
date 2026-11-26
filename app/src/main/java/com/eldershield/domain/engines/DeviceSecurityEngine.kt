package com.eldershield.domain.engines

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.eldershield.data.entities.AppSnapshot
import com.eldershield.data.entities.PermissionSnapshot

data class AppSecurityReport(
    val appSnapshot: AppSnapshot,
    val grantedSensitivePermissions: List<String>,
    val isRemoteSupportApp: Boolean,
    val isSideloaded: Boolean,
    val riskSummary: String
)

object DeviceSecurityEngine {

    val SENSITIVE_PERMISSIONS = setOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.READ_PHONE_STATE
    )

    private val KNOWN_REMOTE_PACKAGES = setOf(
        "com.anydesk.anydeskandroid",
        "com.teamviewer.host.market",
        "com.teamviewer.quicksupport.market",
        "com.teamviewer.teamviewer.market.mobile",
        "com.rsupport.rs.activity.rsupport",
        "com.sand.airdroid",
        "com.splashtop.remote",
        "com.rustdesk.rustdesk"
    )

    fun auditInstalledApps(context: Context): List<AppSecurityReport> {
        val pm = context.packageManager
        val packages: List<PackageInfo> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val reports = mutableListOf<AppSecurityReport>()

        for (pkg in packages) {
            val appInfo = pkg.applicationInfo ?: continue

            // Filter down primarily to user-facing or non-system apps to avoid cluttering elderly users with 200 system frameworks
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val appName = try {
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                pkg.packageName
            }

            val isRemote = KNOWN_REMOTE_PACKAGES.contains(pkg.packageName)

            val installer = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    pm.getInstallSourceInfo(pkg.packageName).installingPackageName
                } else {
                    @Suppress("DEPRECATION")
                    pm.getInstallerPackageName(pkg.packageName)
                }
            } catch (e: Exception) {
                null
            }

            val isSideloaded = !isSystem && (installer == null || !installer.contains("com.android.vending"))

            val grantedSensitives = mutableListOf<String>()
            val reqPerms = pkg.requestedPermissions
            val reqFlags = pkg.requestedPermissionsFlags

            if (reqPerms != null && reqFlags != null) {
                for (i in reqPerms.indices) {
                    val perm = reqPerms[i]
                    val isGranted = (reqFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                    if (isGranted && SENSITIVE_PERMISSIONS.contains(perm)) {
                        grantedSensitives.add(getPermissionSimpleName(perm))
                    }
                }
            }

            // Include if it's non-system, or has sensitive permissions, or is a remote tool
            if (!isSystem || grantedSensitives.isNotEmpty() || isRemote) {
                val risk = when {
                    isRemote -> "Remote Support Tool: Can potentially view or control your screen."
                    grantedSensitives.size >= 4 -> "High Sensitive Access: Uses Camera, Mic, SMS, or Contacts."
                    isSideloaded -> "Installed outside Google Play Store. Verify legitimacy."
                    grantedSensitives.isNotEmpty() -> "Has access to: ${grantedSensitives.joinToString(", ")}"
                    else -> "Standard Application."
                }

                val snapshot = AppSnapshot(
                    packageName = pkg.packageName,
                    appName = appName,
                    versionName = pkg.versionName ?: "1.0",
                    versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pkg.longVersionCode else 1L,
                    isSystemApp = isSystem,
                    installerPackageName = installer,
                    isSideloaded = isSideloaded,
                    firstInstalledTime = pkg.firstInstallTime,
                    lastUpdatedTime = pkg.lastUpdateTime
                )

                reports.add(
                    AppSecurityReport(
                        appSnapshot = snapshot,
                        grantedSensitivePermissions = grantedSensitives,
                        isRemoteSupportApp = isRemote,
                        isSideloaded = isSideloaded,
                        riskSummary = risk
                    )
                )
            }
        }

        return reports.sortedWith(
            compareByDescending<AppSecurityReport> { it.isRemoteSupportApp }
                .thenByDescending { it.grantedSensitivePermissions.size }
                .thenByDescending { it.isSideloaded }
        )
    }

    fun getPermissionSimpleName(permission: String): String = when (permission) {
        android.Manifest.permission.CAMERA -> "Camera"
        android.Manifest.permission.RECORD_AUDIO -> "Microphone"
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION -> "Location"
        android.Manifest.permission.READ_CONTACTS -> "Contacts"
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.RECEIVE_SMS -> "SMS Messages"
        android.Manifest.permission.READ_PHONE_STATE -> "Phone Calls"
        else -> permission.substringAfterLast('.')
    }

    fun getPermissionExplanation(permissionName: String): String = when (permissionName) {
        "Camera" -> "Allows taking photos or video. Legitimate for cameras and video calling, suspicious for simple utility apps like flashlights or calculators."
        "Microphone" -> "Allows recording audio. Legitimate for communication apps; check whether this application actually needs audio."
        "Location" -> "Allows knowing your physical location. Verify if this application requires navigation or local services."
        "SMS Messages" -> "Allows reading SMS messages, which often contain secret banking OTPs. Never grant this to untrusted apps."
        "Contacts" -> "Allows reading your phone address book and phone numbers of friends and family."
        "Phone Calls" -> "Allows reading phone numbers and carrier network status."
        else -> "Sensitive Android system capability."
    }
}
