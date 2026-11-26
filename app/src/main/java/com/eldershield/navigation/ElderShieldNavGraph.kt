package com.eldershield.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eldershield.core.ui.theme.CyberBlack
import com.eldershield.core.ui.theme.CyberCardSurface
import com.eldershield.core.ui.theme.CyberDarkSurface
import com.eldershield.core.ui.theme.CyberTextSecondary
import com.eldershield.core.ui.theme.NeonCyan
import com.eldershield.feature.dashboard.DashboardScreen
import com.eldershield.feature.dashboard.DashboardViewModel
import com.eldershield.feature.learning.LearningScreen
import com.eldershield.feature.permissionaudit.DeviceSafetyScreen
import com.eldershield.feature.reports.ReportsScreen
import com.eldershield.feature.scamdetector.ScanHubScreen
import com.eldershield.feature.settings.SettingsScreen

@Composable
fun ElderShieldApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CyberDarkSurface,
                contentColor = NeonCyan,
                tonalElevation = 8.dp
            ) {
                Screen.bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                        label = {
                            Text(
                                text = stringResource(screen.titleRes),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan.copy(alpha = 0.2f),
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        ),
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                    onNavigateToSafety = { navController.navigate(Screen.Safety.route) },
                    onNavigateToFamily = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Scan.route) {
                ScanHubScreen()
            }

            composable(Screen.Safety.route) {
                DeviceSafetyScreen()
            }

            composable(Screen.Reports.route) {
                ReportsScreen()
            }

            composable(Screen.Learn.route) {
                LearningScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
