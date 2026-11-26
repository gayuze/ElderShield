package com.eldershield.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.eldershield.R

sealed class Screen(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
) {
    data object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    data object Scan : Screen("scan", R.string.nav_scan, Icons.Default.QrCodeScanner)
    data object Safety : Screen("safety", R.string.nav_safety, Icons.Default.Security)
    data object Reports : Screen("reports", R.string.nav_reports, Icons.Default.CastConnected)
    data object Learn : Screen("learn", R.string.nav_learn, Icons.Default.MenuBook)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)

    companion object {
        val bottomNavItems = listOf(Home, Scan, Safety, Reports, Learn, Settings)
    }
}
