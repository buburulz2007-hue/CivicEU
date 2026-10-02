package com.civiceu.com.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.civiceu.com.R
import kotlinx.coroutines.delay

@Composable
fun SecurityProcessingDialog(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1200)
        step = 1
        delay(1500)
        step = 2
        delay(1500)
        step = 3
        delay(800)
        onComplete()
    }

    Dialog(
        onDismissRequest = { /* Cannot dismiss */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.sec_dialog_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                // Step 1: Metadata
                SecurityStepRow(
                    isLoading = step == 0,
                    isDone = step >= 1,
                    loadingText = stringResource(R.string.sec_step1_loading),
                    doneText = stringResource(R.string.sec_step1_done)
                )

                // Step 2: Encryption
                if (step >= 1) {
                    SecurityStepRow(
                        isLoading = step == 1,
                        isDone = step >= 2,
                        loadingText = stringResource(R.string.sec_step2_loading),
                        doneText = stringResource(R.string.sec_step2_done)
                    )
                }

                // Step 3: Secure Connection
                if (step >= 2) {
                    SecurityStepRow(
                        isLoading = step == 2,
                        isDone = step >= 3,
                        loadingText = stringResource(R.string.sec_step3_loading),
                        doneText = stringResource(R.string.sec_step3_done)
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityStepRow(isLoading: Boolean, isDone: Boolean, loadingText: String, doneText: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        } else if (isDone) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = if (isDone) doneText else loadingText,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
