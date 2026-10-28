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
import com.civiceu.com.ui.screens.LockScreen
import com.civiceu.com.ui.screens.ImpactScreen
import com.civiceu.com.ui.screens.PremiumScreen
import com.civiceu.com.ui.screens.LegalInfoScreen
import com.civiceu.com.ui.theme.CivicTheme
import com.civiceu.com.crypto.SecurityManager

import com.civiceu.com.util.ThemeManager
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.init(this)

        // Block screenshots, screen recordings, and task switcher previews for security and privacy
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val sharedPrefs = getSharedPreferences("civic_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean("is_first_launch", true)
        
        // Dead Man's Switch Check
        if (com.civiceu.com.util.PremiumManager.isPremium(this) && com.civiceu.com.util.PremiumManager.isDeadMansSwitchEnabled(this)) {
            val lastLogin = sharedPrefs.getLong("last_login_time", System.currentTimeMillis())
            val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000
            if (System.currentTimeMillis() - lastLogin > sevenDaysInMillis) {
                SecurityManager.executePanicWipe(this)
            }
        }
        sharedPrefs.edit().putLong("last_login_time", System.currentTimeMillis()).apply()

        val startDest = when {
            isFirstLaunch -> "onboarding"
            SecurityManager.hasPinSet(this) -> "lock_screen"
            else -> "home"
        }

        enableEdgeToEdge()
        setContent {
            val themeModeState = ThemeManager.themeMode.collectAsState()
            val useDarkTheme = when (themeModeState.value) {
                ThemeManager.THEME_LIGHT -> false
                ThemeManager.THEME_DARK -> true
                else -> isSystemInDarkTheme()
            }
            
            CivicTheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = startDest) {
                    composable("onboarding") {
                        OnboardingScreen(
                            onFinish = {
                                sharedPrefs.edit().putBoolean("is_first_launch", false).apply()
                                val nextDest = if (SecurityManager.hasPinSet(this@MainActivity)) "lock_screen" else "home"
                                navController.navigate(nextDest) {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("lock_screen") {
                        LockScreen(onUnlockSuccess = {
                            navController.navigate("home") {
                                popUpTo("lock_screen") { inclusive = true }
                            }
                        })
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
                            },
                            onImpactClick = {
                                navController.navigate("impact_stats")
                            },
                            onPremiumClick = {
                                navController.navigate("premium_upgrade")
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
                        WhistleblowerGuideScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLegal = {
                                navController.navigate("legal_info")
                            }
                        )
                    }
                    composable("legal_info") {
                        LegalInfoScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("impact_stats") {
                        ImpactScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                    composable("premium_upgrade") {
                        PremiumScreen(onBack = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}
