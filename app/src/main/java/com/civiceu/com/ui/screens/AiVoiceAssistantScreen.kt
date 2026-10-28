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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civiceu.com.R
import com.civiceu.com.ai.AiReportAnalyzer
import com.civiceu.com.data.ReportRepository
import com.civiceu.com.model.CorruptionReport
import kotlinx.coroutines.launch

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiVoiceAssistantScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf<AiReportAnalyzer.AiAnalysisResult?>(null) }
    var createdReport by remember { mutableStateOf<CorruptionReport?>(null) }
    
    // TTS Engine for Playback
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale("ro", "RO")
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    // Speech Recognizer Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isRecording = false
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val res = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = res?.get(0) ?: ""
            if (recognizedText.isNotBlank()) {
                spokenText = recognizedText
                analysisResult = AiReportAnalyzer.analyzeTestimony(recognizedText)
            }
        }
    }

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
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro-RO")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Vorbiți acum. Detaliați cazul de corupție...")
                        }
                        try {
                            speechRecognizerLauncher.launch(intent)
                            isRecording = true
                        } catch (e: Exception) {
                            Toast.makeText(context, "Asistentul vocal nu este disponibil pe acest dispozitiv.", Toast.LENGTH_SHORT).show()
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

            val isPro = com.civiceu.com.util.PremiumManager.isPremium(context)
            var voiceObfuscation by remember { mutableStateOf(false) }

            if (isPro) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = voiceObfuscation, onCheckedChange = { voiceObfuscation = it })
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.pro_voice_obfuscation),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (spokenText.isNotBlank()) {
                var isPlaying by remember { mutableStateOf(false) }
                
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.ai_transcription), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(spokenText, style = MaterialTheme.typography.bodyMedium)
                        
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        
                        OutlinedButton(
                            onClick = {
                                if (!isPlaying) {
                                    isPlaying = true
                                    Toast.makeText(context, context.getString(R.string.ai_playback_playing), Toast.LENGTH_SHORT).show()
                                    
                                    // Folosim TTS pentru redarea textului cu voce tare
                                    val params = android.os.Bundle()
                                    if (voiceObfuscation) {
                                        // Daca anonimizarea e activata, scadem puternic tonul vocii
                                        tts?.setPitch(0.5f)
                                        tts?.setSpeechRate(0.8f)
                                    } else {
                                        tts?.setPitch(1.0f)
                                        tts?.setSpeechRate(1.0f)
                                    }
                                    
                                    val utteranceId = java.util.UUID.randomUUID().toString()
                                    tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                                        override fun onStart(id: String?) {}
                                        override fun onDone(id: String?) { isPlaying = false }
                                        override fun onError(id: String?) { isPlaying = false }
                                    })
                                    
                                    tts?.speak(spokenText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            enabled = !isPlaying,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (isPlaying) stringResource(R.string.ai_playback_playing) else stringResource(R.string.ai_playback_btn), fontSize = 12.sp)
                        }
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
                                
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    val fakeBody = "Sesizare dictata vocal AI: ${result.summary}"
                                    com.civiceu.com.data.FirebaseUploader.uploadReport(newReport, fakeBody)
                                    com.civiceu.com.util.SmtpEmailSender.sendEmailBackground(
                                        toEmail = newReport.targetAgencyEmail,
                                        subject = "SESIZARE CORUPȚIE VOCALĂ (AI CivicEU): ${newReport.title}",
                                        bodyText = fakeBody
                                    )
                                }
                                
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
