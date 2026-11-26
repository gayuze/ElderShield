package com.eldershield

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.eldershield.core.localization.LocaleHelper
import com.eldershield.core.preferences.UserPreferencesRepository
import com.eldershield.core.ui.theme.ElderShieldTheme
import com.eldershield.core.voice.VoiceAssistant
import com.eldershield.feature.settings.SettingsViewModel
import com.eldershield.navigation.ElderShieldApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        // Read persisted language if available, default to English
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val prefs by settingsViewModel.preferencesState.collectAsState()

            // Apply selected language context
            LocaleHelper.applyLocale(this@MainActivity, prefs.languageCode)

            ElderShieldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ElderShieldApp()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceAssistant.shutdown()
    }
}
