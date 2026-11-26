# ElderShield – Intelligent Personal Cybersecurity & Digital Safety Assistant

ElderShield is an Android application built for elderly and non-technical smartphone users to help identify digital threats such as fake advertisements, phishing links, fraudulent applications, privacy leakage, identity theft attempts, misleading security warnings, and financial fraud.

---

## 🌟 Key Highlights

- **Bilingual Interface:** Fully localized in **Tamil (தமிழ்)** and **English**.
- **Accessible & High-Contrast Design:** Large buttons, high-contrast typography, and simple navigation designed specifically for elderly smartphone users.
- **Voice Guidance:** Integrated `TextToSpeech` reads security warnings and explanations aloud in simple language.
- **Privacy First:** Zero background surveillance, no keystroke tracking, no password/OTP collection, and explicit consent for family report sharing.
- **Explainable Assessments:** Avoids arbitrary, frightening percentages (e.g. "98% hacked"). Uses clear, factual statuses:
  - *No concerns detected in recent checks*
  - *Attention recommended*
  - *Suspicious activity detected*
  - *Additional checks required*

---

## 🏗️ Architecture & Technology Stack

- **Architecture:** MVVM + Clean Architecture
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Dependency Injection:** Dagger Hilt
- **Local Database:** Room Database with SQLite & Encrypted Security
- **Asynchronous Flow:** Kotlin Coroutines & StateFlow
- **Preferences:** Jetpack DataStore Preferences
- **Machine Learning / Vision:** Google ML Kit (Barcode Scanning & Text Recognition)
- **Minimum SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 14 (API 34)

---

## 📁 Project Structure

```
ElderShield/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/eldershield/
│   │   │   ├── ElderShieldApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── core/
│   │   │   │   ├── database/       # Room database, Converters, Hilt modules
│   │   │   │   ├── localization/   # LocaleHelper (English & Tamil)
│   │   │   │   ├── preferences/    # DataStore UserPreferences
│   │   │   │   ├── ui/theme/       # Material 3 Navy & Cyan theme, accessible Type
│   │   │   │   └── voice/          # TextToSpeech VoiceAssistant
│   │   │   ├── data/
│   │   │   │   ├── dao/            # ThreatDao, ScanDao, AppSecurityDao, etc.
│   │   │   │   └── entities/       # 11 Room entities
│   │   │   ├── domain/models/      # SecurityStatus, SeverityLevel, ThreatCategory
│   │   │   ├── feature/
│   │   │   │   ├── dashboard/      # DashboardScreen & DashboardViewModel
│   │   │   │   ├── scamdetector/   # Screenshot & Scam analysis hub
│   │   │   │   ├── permissionaudit/# App & permission audit
│   │   │   │   ├── reports/        # Threat reports & scan history
│   │   │   │   ├── learning/       # Educational safety lessons & quizzes
│   │   │   │   └── settings/       # Language, accessibility, family consent
│   │   │   └── navigation/         # Navigation Compose routes & bottom bar
│   │   └── res/
│   │       ├── values/strings.xml     # English strings
│   │       └── values-ta/strings.xml  # Tamil strings
│   └── src/test/                      # Unit tests
├── build.gradle.kts
├── settings.gradle.kts
└── DEVELOPMENT_STATUS.md
```

---

## 🛠️ How to Build & Run

### Prerequisites
- Android Studio Ladybug or later
- JDK 17 or JDK 21 (Bundled Android Studio JBR supported)
- Android SDK Platform 34 & Build-Tools 34.0.0+

### Command Line Build
To compile the debug APK:
```powershell
.\gradlew assembleDebug
```

To run unit tests:
```powershell
.\gradlew test
```

Generated APK output directory:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🛡️ Privacy & Permissions

ElderShield strictly requests permissions only when relevant features are activated:
- `INTERNET` – For optional threat intelligence and domain checks.
- `PACKAGE_USAGE_STATS` – Optional special permission for local app usage baselines.
- `READ_MEDIA_IMAGES` / Photo Picker – For user-initiated screenshot scanning.
- No root access, no unauthorized surveillance, and no hidden background data transmission.
