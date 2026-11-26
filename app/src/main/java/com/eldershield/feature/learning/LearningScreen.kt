package com.eldershield.feature.learning

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun LearningScreen(
    viewModel: LearningViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val questions = viewModel.quizQuestions

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
                        text = stringResource(R.string.nav_learn),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Interactive digital safety lessons & quiz in Tamil & English.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CyberBadge(text = "ACADEMY", color = NeonCyan)
            }
        }

        // Interactive Quiz Card
        item {
            CyberCard(
                borderColor = NeonCyan,
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
                            text = "CYBER SAFETY QUIZ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonCyan
                        )
                        CyberBadge(
                            text = if (uiState.isCompleted) "SCORE: ${uiState.totalScore}/100" else "Q ${uiState.currentQuestionIndex + 1}/${questions.size}",
                            color = NeonYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!uiState.isCompleted) {
                        val currentQ = questions[uiState.currentQuestionIndex]

                        Text(
                            text = currentQ.questionEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQ.questionTa,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Options
                        currentQ.optionsEn.forEachIndexed { idx, optionEn ->
                            val isSelected = uiState.selectedOptionIndex == idx
                            val optionTa = currentQ.optionsTa.getOrNull(idx) ?: ""

                            val borderCol = when {
                                uiState.isSubmitted && idx == currentQ.correctIndex -> NeonGreen
                                uiState.isSubmitted && isSelected && !uiState.isCorrect -> NeonPink
                                isSelected -> NeonCyan
                                else -> CyberBorder
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                                    .background(borderCol.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                    .clickable(enabled = !uiState.isSubmitted) {
                                        viewModel.selectOption(idx)
                                    }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "${idx + 1}. $optionEn",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = optionTa,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Feedback on submit
                        if (uiState.isSubmitted) {
                            Text(
                                text = if (uiState.isCorrect) "✅ ${currentQ.explanationEn}" else "❌ ${currentQ.explanationEn}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isCorrect) NeonGreen else NeonPink
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("NEXT QUESTION", fontWeight = FontWeight.Bold, color = CyberBlack)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.submitAnswer() },
                                enabled = uiState.selectedOptionIndex != null,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SUBMIT ANSWER", fontWeight = FontWeight.Bold, color = CyberBlack)
                            }
                        }
                    } else {
                        // Completed Screen
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "QUIZ COMPLETED!",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = NeonGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your Cyber Safety Score: ${uiState.totalScore} / 100",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.resetQuiz() },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = NeonCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("RETAKE QUIZ", color = NeonCyan)
                            }
                        }
                    }
                }
            }
        }

        // Lessons Section
        item {
            Text(
                text = "// DIGITAL DEFENSE LESSONS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            CyberLessonCard(
                icon = Icons.Default.CreditCard,
                title = "UPI Payment & PIN Safety",
                titleTa = "UPI கட்டணம் மற்றும் PIN பாதுகாப்பு",
                description = "Golden Rule: You NEVER enter your UPI PIN to RECEIVE money. Scammers send 'Pay' requests pretending to be refunds.",
                accentColor = NeonCyan
            )
        }

        item {
            CyberLessonCard(
                icon = Icons.Default.Password,
                title = "OTP & Banking Protection",
                titleTa = "OTP மற்றும் வங்கி பாதுகாப்பு",
                description = "Banks and government agencies will NEVER call or message asking for your SMS OTP or NetBanking password.",
                accentColor = NeonYellow
            )
        }

        item {
            CyberLessonCard(
                icon = Icons.Default.Phishing,
                title = "Spotting Fake Virus Warnings",
                titleTa = "போலி வைரஸ் விளம்பரங்கள்",
                description = "Browser popups claiming 'Your phone has 13 viruses' are deceptive advertisements (scareware) trying to install unwanted software.",
                accentColor = NeonPink
            )
        }

        item {
            CyberLessonCard(
                icon = Icons.Default.Lock,
                title = "Remote Screen-Sharing Dangers",
                titleTa = "தொலைநிலை அணுகல் ஆபத்துகள்",
                description = "Never install AnyDesk or TeamViewer at the request of callers. They can view your screen and steal banking passwords.",
                accentColor = NeonGreen
            )
        }
    }
}

@Composable
private fun CyberLessonCard(
    icon: ImageVector,
    title: String,
    titleTa: String,
    description: String,
    accentColor: Color
) {
    CyberCard(
        borderColor = accentColor.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, accentColor, RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = titleTa,
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
