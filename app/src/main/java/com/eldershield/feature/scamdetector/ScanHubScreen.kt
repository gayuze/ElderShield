package com.eldershield.feature.scamdetector

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.eldershield.core.ui.theme.CyberCardSurface
import com.eldershield.core.ui.theme.NeonCyan
import com.eldershield.core.ui.theme.NeonGreen
import com.eldershield.core.ui.theme.NeonPink
import com.eldershield.core.ui.theme.NeonYellow
import com.eldershield.core.ui.theme.StatusCritical
import com.eldershield.core.ui.theme.StatusSafe
import com.eldershield.core.ui.theme.StatusSuspicious
import com.eldershield.domain.models.SeverityLevel

@Composable
fun ScanHubScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Ad & Screenshot", "URL & Phishing", "Financial Scam")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cyber Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberCardSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 1: Screenshot & Scam OCR
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
                                        text = stringResource(R.string.module_ad_scam),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CyberBadge(text = "OCR ENGINE", color = NeonYellow)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Select any suspicious screenshot from your gallery, or choose a test sample below to scan for fake virus warnings and lottery scams.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = CyberBlack
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PICK SCREENSHOT FROM GALLERY",
                                        fontWeight = FontWeight.Bold,
                                        color = CyberBlack
                                    )
                                }
                            }
                        }
                    }

                    // Sample Presets for Instant Testing
                    item {
                        Text(
                            text = "// TEST PRESET SAMPLES",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                            color = NeonYellow
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CyberSampleChip(
                                text = "Fake Virus Alert",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.testWithSampleText(
                                        "WARNING! Your phone is infected with 13 viruses! Battery is severely damaged. Install Antivirus Cleaner immediately or device will be blocked!"
                                    )
                                }
                            )
                            CyberSampleChip(
                                text = "Lottery Win Scam",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.testWithSampleText(
                                        "CONGRATULATIONS! You won ₹25,00,000 cash in KBC Lucky Draw! Claim your prize now. Pay fee to release funds."
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CyberSampleChip(
                                text = "Aadhaar Identity Leak",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.testWithSampleText(
                                        "Government Verification. Customer Name: Mohan Kumar. Aadhaar Number: 4589 1234 9876. Contact: +919876543210."
                                    )
                                }
                            )
                            CyberSampleChip(
                                text = "Urgent Power Threat",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.testWithSampleText(
                                        "Dear Consumer, your electricity power bill will be disconnected tonight at 9:30 PM due to pending KYC. Call our officer immediately."
                                    )
                                }
                            )
                        }
                    }

                    // Progress
                    if (uiState.isOcrScanning) {
                        item {
                            CyberCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    com.eldershield.core.ui.components.CyberLoadingSpinner(
                                        color = NeonCyan,
                                        size = 28.dp
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Scanning image with Google ML Kit OCR...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }

                    // Analysis Result
                    uiState.activeFinding?.let { finding ->
                        item {
                            val sevColor = when (finding.severity) {
                                SeverityLevel.LOW -> StatusSafe
                                SeverityLevel.MEDIUM -> StatusSuspicious
                                SeverityLevel.HIGH -> StatusCritical
                                SeverityLevel.CRITICAL -> StatusCritical
                            }

                            CyberCard(
                                borderColor = sevColor,
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
                                            text = finding.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = sevColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                        CyberBadge(
                                            text = "SEV: ${finding.severity.name}",
                                            color = sevColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "CONFIDENCE: ${(finding.confidence * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                        color = NeonYellow
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "${stringResource(R.string.label_evidence)} ${finding.evidence}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "${stringResource(R.string.label_explanation)} ${finding.explanation}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "${stringResource(R.string.label_action)} ${finding.recommendedAction}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedButton(
                                        onClick = { viewModel.speakFinding(finding.explanation) },
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Listen Explanation", color = NeonCyan)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 2: URL & Phishing Inspector
                    item {
                        CyberCard(
                            borderColor = NeonCyan,
                            borderWidth = 1.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.module_url_phishing),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Inspect suspicious links, shortened URLs, and fake domains before opening them.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = uiState.urlInput,
                                    onValueChange = { viewModel.setUrlInput(it) },
                                    label = { Text("Enter Website Link / URL") },
                                    placeholder = { Text("e.g. sbi-kyc-update.xyz or bit.ly/prize") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = CyberBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.analyzeUrl() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = CyberBlack
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("CHECK URL SAFETY", fontWeight = FontWeight.Bold, color = CyberBlack)
                                }
                            }
                        }
                    }

                    // Quick URL Test Samples
                    item {
                        Text(
                            text = "// TEST URL PRESETS",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CyberSampleChip(
                                text = "Phishing: sbi-kyc.xyz",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.setUrlInput("http://sbi-verify-kyc.xyz/login")
                                    viewModel.analyzeUrl("http://sbi-verify-kyc.xyz/login")
                                }
                            )
                            CyberSampleChip(
                                text = "IP URL: 192.168.1.1",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.setUrlInput("http://192.168.1.45/bank")
                                    viewModel.analyzeUrl("http://192.168.1.45/bank")
                                }
                            )
                        }
                    }

                    // URL Result
                    uiState.urlResult?.let { res ->
                        item {
                            val sevColor = if (res.isSuspicious) StatusCritical else StatusSafe

                            CyberCard(
                                borderColor = sevColor,
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
                                            text = if (res.isSuspicious) "SUSPICIOUS LINK" else "SAFE LINK INDICATORS",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = sevColor
                                        )
                                        CyberBadge(
                                            text = if (res.isHttps) "HTTPS" else "HTTP UNENCRYPTED",
                                            color = if (res.isHttps) NeonGreen else NeonPink
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "HOST: ${res.host}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                        color = NeonYellow
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    res.indicators.forEach { ind ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("• ", color = sevColor, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = ind,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = res.finding.recommendedAction,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 3: Financial Scam Protection
                    item {
                        CyberCard(
                            borderColor = NeonPink,
                            borderWidth = 1.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.module_financial_scam),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Scan payment messages, fake refund claims, and UPI payment requests.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = uiState.financialInput,
                                    onValueChange = { viewModel.setFinancialInput(it) },
                                    label = { Text("Paste Suspicious SMS or Payment Request") },
                                    placeholder = { Text("e.g. Scan QR code and enter UPI PIN to receive ₹3,000 refund") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonPink,
                                        unfocusedBorderColor = CyberBorder
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.analyzeFinancialMessage() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("EVALUATE FINANCIAL SCAM", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // Golden Rules & Cybercrime 1930
                    item {
                        CyberCard(
                            borderColor = NeonYellow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneInTalk,
                                        contentDescription = null,
                                        tint = NeonYellow,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "CYBERCRIME HELPLINE: 1930",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonYellow
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "If you were tricked into sending money or sharing your PIN, dial 1930 immediately or visit cybercrime.gov.in within 24 hours.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Financial Result
                    uiState.financialResult?.let { res ->
                        item {
                            CyberCard(
                                borderColor = if (res.isFraudulent) StatusCritical else StatusSafe,
                                borderWidth = 1.5.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = res.finding.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.isFraudulent) StatusCritical else StatusSafe
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = res.finding.explanation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    res.tips.forEach { tip ->
                                        Text(
                                            text = "⚠️ $tip",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = NeonYellow,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CyberSampleChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(NeonCyan.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = NeonCyan
        )
    }
}
