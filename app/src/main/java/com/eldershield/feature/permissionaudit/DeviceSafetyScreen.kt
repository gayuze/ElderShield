package com.eldershield.feature.permissionaudit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eldershield.R
import com.eldershield.core.ui.components.CyberBadge
import com.eldershield.core.ui.components.CyberCard
import com.eldershield.core.ui.theme.CyberBlack
import com.eldershield.core.ui.theme.CyberBorder
import com.eldershield.core.ui.theme.NeonCyan
import com.eldershield.core.ui.theme.NeonGreen
import com.eldershield.core.ui.theme.NeonPink
import com.eldershield.core.ui.theme.NeonYellow
import com.eldershield.core.ui.theme.StatusCritical
import com.eldershield.core.ui.theme.StatusSafe
import com.eldershield.core.ui.theme.StatusSuspicious

@Composable
fun DeviceSafetyScreen(
    viewModel: DeviceSafetyViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val appReports by viewModel.appReports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.nav_safety),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time installed application & permission auditor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.refreshAudit() },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = NeonCyan)
                }
            }
        }

        // Remote Access & Accessibility Monitor Warning Card (Module 8)
        item {
            CyberCard(
                borderColor = NeonYellow,
                borderWidth = 1.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.module_remote_guard),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonYellow
                        )
                        CyberBadge(text = "REMOTE MONITOR", color = NeonYellow)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scammers often ask victims to install screen-sharing apps (AnyDesk, TeamViewer) or enable Accessibility services to control banking screens remotely.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.openAccessibilitySettings() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonYellow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ScreenShare,
                            contentDescription = null,
                            tint = CyberBlack
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHECK ACCESSIBILITY SERVICES",
                            fontWeight = FontWeight.Bold,
                            color = CyberBlack
                        )
                    }
                }
            }
        }

        // Section Title: Audited Applications
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUDITED APPLICATIONS (${appReports.size})",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (isLoading) {
                    com.eldershield.core.ui.components.CyberLoadingSpinner(
                        color = NeonCyan,
                        size = 20.dp
                    )
                }
            }
        }

        // App List
        items(appReports) { report ->
            val isHighRisk = report.isRemoteSupportApp || report.grantedSensitivePermissions.size >= 4
            val cardBorderColor = when {
                report.isRemoteSupportApp -> NeonPink
                report.grantedSensitivePermissions.size >= 4 -> NeonYellow
                report.isSideloaded -> NeonCyan
                else -> CyberBorder
            }

            CyberCard(
                borderColor = cardBorderColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = report.appSnapshot.appName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = report.appSnapshot.packageName,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (report.isRemoteSupportApp) {
                            CyberBadge(text = "REMOTE ACCESS RISK", color = NeonPink)
                        } else if (report.isSideloaded) {
                            CyberBadge(text = "SIDELOADED", color = NeonYellow)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = report.riskSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isHighRisk) FontWeight.Bold else FontWeight.Normal,
                        color = if (report.isRemoteSupportApp) NeonPink else MaterialTheme.colorScheme.onSurface
                    )

                    if (report.grantedSensitivePermissions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            report.grantedSensitivePermissions.take(4).forEach { perm ->
                                Box(
                                    modifier = Modifier
                                        .background(NeonCyan.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = perm,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.openAppSettings(report.appSnapshot.packageName) },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.action_open_settings),
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonCyan
                        )
                    }
                }
            }
        }
    }
}
