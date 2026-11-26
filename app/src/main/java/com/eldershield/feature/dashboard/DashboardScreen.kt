package com.eldershield.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eldershield.R
import com.eldershield.core.ui.components.CyberBadge
import com.eldershield.core.ui.components.CyberCard
import com.eldershield.core.ui.theme.CyberBlack
import com.eldershield.core.ui.theme.CyberBorder
import com.eldershield.core.ui.theme.CyberCardSurface
import com.eldershield.core.ui.theme.NeonCyan
import com.eldershield.core.ui.theme.NeonGreen
import com.eldershield.core.ui.theme.NeonPink
import com.eldershield.core.ui.theme.NeonYellow
import com.eldershield.core.ui.theme.StatusAttention
import com.eldershield.core.ui.theme.StatusCritical
import com.eldershield.core.ui.theme.StatusSafe
import com.eldershield.core.ui.theme.StatusSuspicious
import com.eldershield.domain.models.SecurityStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToFamily: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cyber Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ELDERSHIELD",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = NeonCyan
                        )
                    }
                    Text(
                        text = stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CyberBadge(
                    text = "V 2.0 // SHIELD",
                    color = NeonYellow
                )
            }
        }

        // Cyber Safety Status Banner (Glowing Neon)
        item {
            CyberSafetyStatusCard(
                status = uiState.overallStatus,
                isSpeaking = uiState.isSpeaking,
                onListenClicked = {
                    val explanation = when (uiState.overallStatus) {
                        SecurityStatus.SAFE -> "No concerns detected in recent checks. Your device indicators appear normal."
                        SecurityStatus.ATTENTION -> "Attention recommended. Several sensitive permissions should be reviewed."
                        SecurityStatus.SUSPICIOUS -> "Suspicious activity detected. Please review the security findings."
                        SecurityStatus.CRITICAL -> "Immediate attention recommended. High-risk security concerns were detected."
                        SecurityStatus.PENDING_CHECKS -> "Additional checks are required. Run a quick scan to verify your device."
                    }
                    viewModel.speakOverview(explanation)
                },
                onStopVoice = { viewModel.stopVoice() }
            )
        }

        // Cyber Quick Actions Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEFENSE ACTIONS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                CyberBadge(text = "RAPID ACCESS", color = NeonCyan)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberActionButton(
                    icon = Icons.Default.QrCodeScanner,
                    label = stringResource(R.string.btn_quick_scan),
                    accentColor = NeonCyan,
                    isLoading = uiState.isQuickScanning,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.runQuickScan() }
                )
                CyberActionButton(
                    icon = Icons.Default.Campaign,
                    label = stringResource(R.string.btn_check_ad),
                    accentColor = NeonYellow,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToScan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberActionButton(
                    icon = Icons.Default.Link,
                    label = stringResource(R.string.btn_check_link),
                    accentColor = NeonGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToScan
                )
                CyberActionButton(
                    icon = Icons.Default.PhoneInTalk,
                    label = stringResource(R.string.btn_emergency_help),
                    accentColor = NeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToFamily
                )
            }
        }

        // Metrics Terminal Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CyberMetricCard(
                    title = stringResource(R.string.concerns_count),
                    value = uiState.activeConcernsCount.toString(),
                    highlightColor = if (uiState.activeConcernsCount > 0) NeonPink else NeonGreen,
                    badgeText = if (uiState.activeConcernsCount > 0) "WARNING" else "CLEAN",
                    modifier = Modifier.weight(1f)
                )
                CyberMetricCard(
                    title = stringResource(R.string.permission_alerts),
                    value = "${uiState.sensitiveAppsCount} APPS",
                    highlightColor = if (uiState.sensitiveAppsCount > 5) NeonYellow else NeonCyan,
                    badgeText = "AUDIT",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSafety
                )
            }
        }

        // Family Guardian Status Card
        item {
            CyberCard(
                borderColor = if (uiState.isFamilyGuardianActive) NeonCyan else CyberBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToFamily() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FamilyRestroom,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.family_status),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (uiState.isFamilyGuardianActive) {
                                stringResource(R.string.family_active)
                            } else {
                                stringResource(R.string.family_inactive)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (uiState.isFamilyGuardianActive) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    CyberBadge(
                        text = if (uiState.isFamilyGuardianActive) "SYNC ON" else "OFFLINE",
                        color = if (uiState.isFamilyGuardianActive) NeonGreen else CyberBorder
                    )
                }
            }
        }

        // Latest Scan Terminal Card
        item {
            CyberCard(
                borderColor = NeonCyan.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.latest_scan),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CyberBadge(text = "LOGGED", color = NeonCyan)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val latest = uiState.latestScan
                    if (latest != null) {
                        val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                            .format(Date(latest.executedAt))
                        Text(
                            text = "// COMPLETED: $formattedDate",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = NeonYellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.languageCode == "ta") latest.summaryTa else latest.summaryEn,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.no_scans_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Cyber Protection Disclaimer
        item {
            CyberCard(
                borderColor = CyberBorder,
                containerColor = CyberBlack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.disclaimer_protection),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CyberSafetyStatusCard(
    status: SecurityStatus,
    isSpeaking: Boolean,
    onListenClicked: () -> Unit,
    onStopVoice: () -> Unit
) {
    val (statusColor, statusTextRes, statusIcon) = when (status) {
        SecurityStatus.SAFE -> Triple(StatusSafe, R.string.status_safe, Icons.Default.CheckCircle)
        SecurityStatus.ATTENTION -> Triple(StatusAttention, R.string.status_attention, Icons.Default.Warning)
        SecurityStatus.SUSPICIOUS -> Triple(StatusSuspicious, R.string.status_suspicious, Icons.Default.Warning)
        SecurityStatus.CRITICAL -> Triple(StatusCritical, R.string.status_critical, Icons.Default.Warning)
        SecurityStatus.PENDING_CHECKS -> Triple(NeonCyan, R.string.status_pending_checks, Icons.Default.Info)
    }

    CyberCard(
        borderColor = statusColor,
        borderWidth = 1.5.dp,
        containerColor = CyberCardSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .border(1.5.dp, statusColor, CircleShape)
                            .background(statusColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.safety_overview),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "// REAL-TIME SYSTEM AUDIT",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = statusColor
                        )
                    }
                }
                CyberBadge(
                    text = if (status == SecurityStatus.PENDING_CHECKS) "PENDING" else status.name,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(statusTextRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { if (isSpeaking) onStopVoice() else onListenClicked() },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSpeaking) stringResource(R.string.btn_voice_stop) else stringResource(R.string.btn_voice_listen),
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonCyan
                )
            }
        }
    }
}

@Composable
private fun CyberActionButton(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor.copy(alpha = 0.12f),
            disabledContainerColor = accentColor.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
        contentPadding = PaddingValues(14.dp),
        modifier = modifier.height(106.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                com.eldershield.core.ui.components.CyberLoadingSpinner(
                    color = accentColor,
                    size = 28.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CyberMetricCard(
    title: String,
    value: String,
    highlightColor: Color,
    badgeText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    CyberCard(
        borderColor = highlightColor.copy(alpha = 0.5f),
        containerColor = CyberCardSurface,
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberBadge(text = badgeText, color = highlightColor)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Monospace),
                fontWeight = FontWeight.Black,
                color = highlightColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
