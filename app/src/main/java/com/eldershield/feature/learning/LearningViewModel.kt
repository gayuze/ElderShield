package com.eldershield.feature.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldershield.data.dao.LearningDao
import com.eldershield.data.entities.LearningProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizQuestion(
    val id: Int,
    val questionEn: String,
    val questionTa: String,
    val optionsEn: List<String>,
    val optionsTa: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationTa: String
)

data class LearningUiState(
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val totalScore: Int = 0,
    val isCompleted: Boolean = false
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val learningDao: LearningDao
) : ViewModel() {

    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            questionEn = "Someone calls saying they sent you ₹5,000 by mistake and tells you to enter your UPI PIN to return it. What should you do?",
            questionTa = "தவறுதலாக ₹5,000 அனுப்பிவிட்டதாகக் கூறி, UPI PIN ஐ உள்ளிட்டு பணத்தைத் திருப்பித் தரச் சொன்னால் என்ன செய்ய வேண்டும்?",
            optionsEn = listOf(
                "Enter UPI PIN immediately",
                "Refuse and check your official bank balance statement directly",
                "Share your ATM card number instead"
            ),
            optionsTa = listOf(
                "உடனே UPI PIN ஐ உள்ளிடவும்",
                "மறுத்துவிட்டு உங்கள் அதிகாரப்பூர்வ வங்கி இருப்பைச் சரிபார்க்கவும்",
                "ATM அட்டை எண்ணைப் பகிரவும்"
            ),
            correctIndex = 1,
            explanationEn = "Correct! Entering a UPI PIN always sends money OUT of your account. Never enter your PIN to receive money.",
            explanationTa = "சரியானது! UPI PIN ஐ உள்ளிட்டால் எப்போதும் உங்கள் கணக்கிலிருந்து பணம் செல்லும். பணத்தைப் பெற ஒருபோதும் PIN தேவையில்லை."
        ),
        QuizQuestion(
            id = 2,
            questionEn = "A website popup flashes: 'Warning! Your phone has 13 viruses. Install Antivirus Cleaner immediately'. What is this?",
            questionTa = "இணையதளத்தில் 'எச்சரிக்கை! உங்கள் போனில் 13 வைரஸ்கள் உள்ளன. உடனே கிளீனரை நிறுவுங்கள்' என்று தோன்றினால் அது என்ன?",
            optionsEn = listOf(
                "A genuine Android system warning",
                "A deceptive advertisement (scareware) trying to trick you into downloading unwanted apps",
                "A government security notice"
            ),
            optionsTa = listOf(
                "உண்மையான ஆண்ட்ராய்டு சிஸ்டம் எச்சரிக்கை",
                "தேவையற்ற செயலியைப் பதிவிறக்க வைக்க முயற்சிக்கும் போலி அச்சுறுத்தல் விளம்பரம்",
                "அரசு பாதுகாப்பு அறிவிப்பு"
            ),
            correctIndex = 1,
            explanationEn = "Correct! Web browser popups cannot scan your phone for viruses. It is a fake scare advertisement.",
            explanationTa = "சரியானது! இணைய உலாவி பக்கங்களால் உங்கள் போனிலுள்ள வைரஸ்களை ஸ்கேன் செய்ய முடியாது. அது ஒரு போலி விளம்பரம்."
        ),
        QuizQuestion(
            id = 3,
            questionEn = "A caller claiming to be from your bank asks for an SMS OTP sent to your phone to 'prevent your debit card from being blocked'. Will you share it?",
            questionTa = "உங்கள் வங்கி அதிகாரி என்று கூறி டெபிட் கார்டு முடங்குவதைத் தடுக்க வந்த SMS OTP ஐக் கேட்டால் கொடுப்பீர்களா?",
            optionsEn = listOf(
                "Yes, because they said they are from the bank",
                "No! Banks NEVER ask for OTPs or passwords over the phone",
                "Share only the first 3 digits"
            ),
            optionsTa = listOf(
                "ஆம், வங்கியிலிருந்து அழைப்பதால்",
                "இல்லை! வங்கிகள் ஒருபோதும் தொலைபேசியில் OTP அல்லது கடவுச்சொல்லைக் கேட்காது",
                "முதல் 3 எண்களை மட்டும் கூறுவேன்"
            ),
            correctIndex = 1,
            explanationEn = "Correct! OTP stands for One-Time Password. It is strictly confidential.",
            explanationTa = "சரியானது! OTP என்பது ரகசியமானது. வங்கிகள் ஒருபோதும் தொலைபேசியில் அதைக் கேட்காது."
        ),
        QuizQuestion(
            id = 4,
            questionEn = "If you fall victim to financial cyber fraud, which official helpline number should you call immediately in India?",
            questionTa = "சைபர் பண மோசடி நடந்தால் இந்தியாவில் உடனடியாக அழைக்க வேண்டிய அதிகாரப்பூர்வ உதவி எண் எது?",
            optionsEn = listOf(
                "Dial 1930 (National Cyber Crime Helpline)",
                "Call 100 or send WhatsApp",
                "Search for refund agents on Google"
            ),
            optionsTa = listOf(
                "1930 ஐ அழைக்கவும் (தேசிய சைபர் குற்ற உதவி எண்)",
                "100 ஐ அழைக்கவும்",
                "கூகிளில் பணமீட்பு முகவர்களைத் தேடவும்"
            ),
            correctIndex = 0,
            explanationEn = "Correct! 1930 is the official citizen financial cyber fraud reporting helpline.",
            explanationTa = "சரியானது! 1930 என்பது அதிகாரப்பூர்வ இந்திய சைபர் குற்ற அவசர உதவி எண்."
        )
    )

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    fun selectOption(index: Int) {
        if (!_uiState.value.isSubmitted) {
            _uiState.value = _uiState.value.copy(selectedOptionIndex = index)
        }
    }

    fun submitAnswer() {
        val currentQ = quizQuestions[_uiState.value.currentQuestionIndex]
        val selected = _uiState.value.selectedOptionIndex ?: return
        val isCorrect = selected == currentQ.correctIndex
        val newScore = if (isCorrect) _uiState.value.totalScore + 25 else _uiState.value.totalScore

        _uiState.value = _uiState.value.copy(
            isSubmitted = true,
            isCorrect = isCorrect,
            totalScore = newScore
        )
    }

    fun nextQuestion() {
        val nextIdx = _uiState.value.currentQuestionIndex + 1
        if (nextIdx < quizQuestions.size) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = nextIdx,
                selectedOptionIndex = null,
                isSubmitted = false,
                isCorrect = false
            )
        } else {
            _uiState.value = _uiState.value.copy(isCompleted = true)
            // Save progress to Room
            viewModelScope.launch {
                learningDao.saveProgress(
                    LearningProgress(
                        userProfileId = 1,
                        lessonId = "CYBER_AWARENESS_QUIZ",
                        isCompleted = true,
                        quizScore = _uiState.value.totalScore
                    )
                )
            }
        }
    }

    fun resetQuiz() {
        _uiState.value = LearningUiState()
    }
}
