package com.civiceu.com

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.civiceu.com.ui.screens.AddReportScreen
import com.civiceu.com.ui.screens.AiVoiceAssistantScreen
import com.civiceu.com.ui.screens.HomeScreen
import com.civiceu.com.ui.screens.JournalistHubScreen
import com.civiceu.com.ui.screens.LegalAssistantScreen
import com.civiceu.com.ui.screens.OnboardingScreen
import com.civiceu.com.ui.screens.TrackReportScreen
import com.civiceu.com.ui.screens.UserProfileScreen
import com.civiceu.com.ui.screens.WhistleblowerGuideScreen
import com.civiceu.com.ui.theme.CivicTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Block screenshots, screen recordings, and task switcher previews for security and privacy
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val sharedPrefs = getSharedPreferences("civic_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("is_first_launch", true)
        val startDest = if (isFirstLaunch) "onboarding" else "home"

        enableEdgeToEdge()
        setContent {
            CivicTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDest) {
                    composable("onboarding") {
                        OnboardingScreen(
                            onFinish = {
                                sharedPrefs.edit().putBoolean("is_first_launch", false).apply()
                                navController.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onAddReportClick = {
                                navController.navigate("add_report")
                            },
                            onTrackReportClick = {
                                navController.navigate("track_report")
                            },
                            onJournalistHubClick = {
                                navController.navigate("journalist_hub")
                            },
                            onLegalAssistantClick = {
                                navController.navigate("legal_assistant")
                            },
                            onAiAssistantClick = {
                                navController.navigate("ai_assistant")
                            },
                            onUserProfileClick = {
                                navController.navigate("user_profile")
                            },
                            onWhistleblowerGuideClick = {
                                navController.navigate("whistleblower_guide")
                            }
                        )
                    }
                    composable("add_report") {
                        AddReportScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("track_report") {
                        TrackReportScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("journalist_hub") {
                        JournalistHubScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("legal_assistant") {
                        LegalAssistantScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAddReport = { _ ->
                                navController.navigate("add_report")
                            }
                        )
                    }
                    composable("ai_assistant") {
                        AiVoiceAssistantScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("user_profile") {
                        UserProfileScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("whistleblower_guide") {
                        WhistleblowerGuideScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}
