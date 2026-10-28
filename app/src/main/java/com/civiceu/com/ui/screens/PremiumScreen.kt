package com.civiceu.com.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civiceu.com.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    
    // Un gradient luxuriant, dar serios (dark blue spre auriu/orange), inspirat din aplicații de cybersecurity PRO
    val premiumGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027), 
            Color(0xFF203A43), 
            Color(0xFF2C5364)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF0F2027) // Fundal dark pentru o senzație PRO
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(premiumGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFFFD700).copy(alpha = 0.15f) // Auriu transparent
                ) {
                    Icon(
                        Icons.Default.Diamond,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.premium_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.premium_desc_1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Feature List
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    PremiumFeatureRow(
                        icon = Icons.Default.Gavel,
                        text = stringResource(R.string.premium_feature_1)
                    )
                    PremiumFeatureRow(
                        icon = Icons.Default.VerifiedUser,
                        text = stringResource(R.string.premium_feature_2)
                    )
                    PremiumFeatureRow(
                        icon = Icons.Default.Security,
                        text = stringResource(R.string.premium_feature_3)
                    )
                    PremiumFeatureRow(
                        icon = Icons.Default.Warning,
                        text = stringResource(R.string.premium_feature_4),
                        iconTint = Color(0xFFFF5252) // Roșu pentru panica/wipe
                    )
                    PremiumFeatureRow(
                        icon = Icons.Default.Mic,
                        text = stringResource(R.string.premium_feature_5)
                    )
                    PremiumFeatureRow(
                        icon = Icons.Default.Delete,
                        text = stringResource(R.string.premium_feature_6)
                    )
                    PremiumFeatureRow(
                        icon = Icons.Default.Timer,
                        text = stringResource(R.string.premium_feature_7)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(20.dp))

                val isProActive = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(com.civiceu.com.util.PremiumManager.isPremium(context)) }

                // Upgrade Button
                Button(
                    onClick = {
                        if (!isProActive.value) {
                            com.civiceu.com.util.PremiumManager.setPremium(context, true)
                            isProActive.value = true
                            Toast.makeText(context, "Protecție Legală PRO Activată cu Succes!", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !isProActive.value,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isProActive.value) Color(0xFF4CAF50) else Color(0xFFFFD700),
                        contentColor = if (isProActive.value) Color.White else Color(0xFF0F2027),
                        disabledContainerColor = Color(0xFF4CAF50),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isProActive.value) "PRO ACTIVAT" else stringResource(R.string.premium_upgrade_button).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                if (isProActive.value) {
                    Spacer(Modifier.height(24.dp))
                    val isDeadMansSwitch = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(com.civiceu.com.util.PremiumManager.isDeadMansSwitchEnabled(context)) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Dead Man's Switch (7 zile inactivitate)",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isDeadMansSwitch.value,
                            onCheckedChange = {
                                isDeadMansSwitch.value = it
                                com.civiceu.com.util.PremiumManager.setDeadMansSwitchEnabled(context, it)
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PremiumFeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, iconTint: Color = Color(0xFFFFD700)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(20.dp)
        )
    }
}
