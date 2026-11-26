package com.eldershield.feature.scamdetector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldershield.core.voice.VoiceAssistant
import com.eldershield.data.dao.ScanDao
import com.eldershield.data.dao.ThreatDao
import com.eldershield.data.entities.ScanRecord
import com.eldershield.data.entities.ThreatFinding
import com.eldershield.domain.engines.FinancialScamEngine
import com.eldershield.domain.engines.ScamTextEngine
import com.eldershield.domain.engines.UrlRiskEngine
import com.eldershield.domain.models.SecurityStatus
import com.eldershield.domain.models.ThreatFindingModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

data class ScanUiState(
    val isOcrScanning: Boolean = false,
    val extractedText: String = "",
    val activeFinding: ThreatFindingModel? = null,
    val detectedIndicators: List<String> = emptyList(),
    val exposedIdentities: List<String> = emptyList(),
    val urlInput: String = "",
    val urlResult: UrlRiskEngine.UrlAnalysisResult? = null,
    val financialInput: String = "",
    val financialResult: FinancialScamEngine.FinancialCheckResult? = null
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val threatDao: ThreatDao,
    private val scanDao: ScanDao,
    private val voiceAssistant: VoiceAssistant
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun onImageSelected(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOcrScanning = true)
            try {
                val bitmap = loadBitmapFromUri(imageUri)
                if (bitmap != null) {
                    val recognizedText = recognizeTextFromBitmap(bitmap)
                    analyzeExtractedText(recognizedText)
                } else {
                    _uiState.value = _uiState.value.copy(isOcrScanning = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isOcrScanning = false)
            }
        }
    }

    fun testWithSampleText(sample: String) {
        analyzeExtractedText(sample)
    }

    private fun analyzeExtractedText(text: String) {
        val result = ScamTextEngine.analyzeText(text)
        _uiState.value = _uiState.value.copy(
            isOcrScanning = false,
            extractedText = text,
            activeFinding = result.primaryFinding,
            detectedIndicators = result.detectedIndicators,
            exposedIdentities = result.exposedIdentityItems
        )

        // If finding is high risk, save to Room database
        result.primaryFinding?.let { finding ->
            viewModelScope.launch {
                threatDao.insertThreat(
                    ThreatFinding(
                        title = finding.title,
                        category = finding.category,
                        severity = finding.severity,
                        confidence = finding.confidence,
                        evidence = finding.evidence,
                        explanation = finding.explanation,
                        recommendedAction = finding.recommendedAction
                    )
                )

                scanDao.insertScan(
                    ScanRecord(
                        scanType = "SCREENSHOT_OCR",
                        overallStatus = SecurityStatus.SUSPICIOUS,
                        itemsScannedCount = 1,
                        concernsDetectedCount = 1,
                        summaryEn = finding.title,
                        summaryTa = "சந்தேகத்திற்குரிய விளம்பரம் அல்லது அச்சுறுத்தல் கண்டறியப்பட்டது.",
                        executedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun setUrlInput(url: String) {
        _uiState.value = _uiState.value.copy(urlInput = url)
    }

    fun analyzeUrl(url: String = _uiState.value.urlInput) {
        if (url.isBlank()) return
        val result = UrlRiskEngine.inspectUrl(url)
        _uiState.value = _uiState.value.copy(urlResult = result)

        if (result.isSuspicious) {
            viewModelScope.launch {
                threatDao.insertThreat(
                    ThreatFinding(
                        title = result.finding.title,
                        category = result.finding.category,
                        severity = result.finding.severity,
                        confidence = result.finding.confidence,
                        evidence = result.finding.evidence,
                        explanation = result.finding.explanation,
                        recommendedAction = result.finding.recommendedAction
                    )
                )
            }
        }
    }

    fun setFinancialInput(text: String) {
        _uiState.value = _uiState.value.copy(financialInput = text)
    }

    fun analyzeFinancialMessage(text: String = _uiState.value.financialInput) {
        if (text.isBlank()) return
        val result = FinancialScamEngine.evaluateFinancialMessage(text)
        _uiState.value = _uiState.value.copy(financialResult = result)

        if (result.isFraudulent) {
            viewModelScope.launch {
                threatDao.insertThreat(
                    ThreatFinding(
                        title = result.finding.title,
                        category = result.finding.category,
                        severity = result.finding.severity,
                        confidence = result.finding.confidence,
                        evidence = result.finding.evidence,
                        explanation = result.finding.explanation,
                        recommendedAction = result.finding.recommendedAction
                    )
                )
            }
        }
    }

    fun speakFinding(text: String) {
        voiceAssistant.speak(text)
    }

    private suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String =
        suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text)
                }
                .addOnFailureListener {
                    continuation.resume("")
                }
        }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            null
        }
    }
}
