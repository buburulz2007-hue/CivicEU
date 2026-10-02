package com.civiceu.com.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.civiceu.com.R
import com.civiceu.com.ai.AiReportAnalyzer
import com.civiceu.com.data.ReportRepository
import com.civiceu.com.model.CorruptionReport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiVoiceAssistantScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf<AiReportAnalyzer.AiAnalysisResult?>(null) }
    var createdReport by remember { mutableStateOf<CorruptionReport?>(null) }

    createdReport?.let { report ->
        SuccessTrackingDialog(
            report = report,
            onDismiss = {
                createdReport = null
                onBack()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.ai_assistant_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        stringResource(R.string.ai_assistant_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Microphone Recording Button
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = if (isRecording) MaterialTheme.errorColorCompat() else MaterialTheme.colorScheme.primary
            ) {
                IconButton(
                    onClick = {
                        if (!isRecording) {
                            isRecording = true
                            spokenText = ""
                            analysisResult = null
                        } else {
                            isRecording = false
                            // Simulated AI speech recognition transcription for demo
                            val sample = "S-a cerut mită în spitalul central din București pentru achiziția de medicamente."
                            spokenText = sample
                            analysisResult = AiReportAnalyzer.analyzeTestimony(sample)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(
                text = if (isRecording) stringResource(R.string.ai_recording) else stringResource(R.string.ai_tap_to_record),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            if (spokenText.isNotBlank()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.ai_transcription), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(spokenText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            analysisResult?.let { result ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.ai_analysis_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Titlu: ${result.title}", style = MaterialTheme.typography.bodyMedium)
                        Text("Categorie: ${result.category}", style = MaterialTheme.typography.bodyMedium)
                        Text("Instituție Desemnată: ${result.targetAgency.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

                        Button(
                            onClick = {
                                val newReport = CorruptionReport(
                                    title = result.title,
                                    description = result.summary,
                                    category = result.category,
                                    targetAgencyEmail = result.targetAgency.email,
                                    senderName = "Avertizor Vocal AI",
                                    senderAddress = "",
                                    senderPhone = ""
                                )
                                ReportRepository.addReport(newReport)
                                createdReport = newReport
                                Toast.makeText(context, "Sesizare creată și trimisă cu succes prin AI!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.ai_dispatch_button))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialTheme.errorColorCompat() = colorScheme.error
