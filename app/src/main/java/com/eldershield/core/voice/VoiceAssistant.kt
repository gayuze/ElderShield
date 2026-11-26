package com.eldershield.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceAssistant @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isTamilVoiceAvailable = MutableStateFlow(true)
    val isTamilVoiceAvailable: StateFlow<Boolean> = _isTamilVoiceAvailable.asStateFlow()

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            checkTamilSupport()
        }
    }

    private fun checkTamilSupport() {
        val tamilLocale = Locale("ta", "IN")
        val result = tts?.isLanguageAvailable(tamilLocale)
        _isTamilVoiceAvailable.value = (result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED)
    }

    fun speak(text: String, languageCode: String = "en") {
        if (!isInitialized || tts == null) return

        val targetLocale = if (languageCode == "ta") {
            if (_isTamilVoiceAvailable.value) Locale("ta", "IN") else Locale.ENGLISH
        } else {
            Locale.ENGLISH
        }

        tts?.language = targetLocale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ElderShieldUtteranceId")
        _isSpeaking.value = true
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
