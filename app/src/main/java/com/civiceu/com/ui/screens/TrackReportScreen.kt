package com.civiceu.com.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civiceu.com.R
import com.civiceu.com.data.ReportRepository
import com.civiceu.com.model.CorruptionReport
import com.civiceu.com.model.EncryptedEvidence
import com.civiceu.com.model.ReportMessage
import com.civiceu.com.model.ReportStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var trackingCode by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var searchedReport by remember { mutableStateOf<CorruptionReport?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    var selectedPreviewEvidence by remember { mutableStateOf<EncryptedEvidence?>(null) }
    var selectedKeyEvidence by remember { mutableStateOf<EncryptedEvidence?>(null) }

    selectedKeyEvidence?.let { evidence ->
        EncryptionKeyDialog(
            evidence = evidence,
            onDismiss = { selectedKeyEvidence = null }
        )
    }

    selectedPreviewEvidence?.let { evidence ->
        EvidencePreviewDialog(
            evidence = evidence,
            onDismiss = { selectedPreviewEvidence = null }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.track_report_title), fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        stringResource(R.string.track_report_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    StyledTextField(
                        value = trackingCode,
                        onValueChange = { trackingCode = it },
                        label = stringResource(R.string.tracking_code_label),
                        placeholder = stringResource(R.string.tracking_code_placeholder)
                    )

                    StyledTextField(
                        value = passcode,
                        onValueChange = { passcode = it },
                        label = stringResource(R.string.tracking_passcode_label),
                        placeholder = stringResource(R.string.tracking_passcode_placeholder)
                    )

                    Button(
                        onClick = {
                            hasSearched = true
                            searchedReport = ReportRepository.findReportByTracking(trackingCode, passcode)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = trackingCode.isNotBlank() && passcode.isNotBlank()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.verify_status_button))
                    }

                    if (hasSearched && searchedReport == null) {
                        Text(
                            stringResource(R.string.invalid_tracking_credentials),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            searchedReport?.let { report ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                report.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(status = report.status)
                        }

                        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(report.timestamp))
                        Text(
                            text = "Data: $dateStr | Categorie: ${report.category}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = report.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.report_tracking_code_info, report.trackingCode, report.trackingPasscode),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (report.evidenceList.isNotEmpty()) {
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                stringResource(R.string.e2e_evidence_title) + " (${report.evidenceList.size})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            report.evidenceList.forEach { evidence ->
                                EvidenceItemCard(
                                    evidence = evidence,
                                    onShowKey = { selectedKeyEvidence = evidence },
                                    onPreview = { selectedPreviewEvidence = evidence },
                                    onDelete = {} // Read-only mode
                                )
                            }
                        }
                    }
                }

                // Anonymous Q&A / Messages Section
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.anonymous_communication_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val messages = report.messages
                        if (messages.isEmpty()) {
                            Text(
                                stringResource(R.string.no_messages_yet),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                messages.forEach { message ->
                                    val isReporter = message.senderRole.contains("Raportor", ignoreCase = true) || message.senderRole.contains("Tu", ignoreCase = true)
                                    Surface(
                                        color = if (isReporter) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    if (isReporter) stringResource(R.string.role_reporter) else stringResource(R.string.role_authority),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isReporter) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                                val msgTime = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(message.timestamp))
                                                Text(
                                                    msgTime,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                            Text(message.content, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                placeholder = { Text(stringResource(R.string.reply_placeholder), style = MaterialTheme.typography.bodyMedium) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = false,
                                maxLines = 3
                            )

                            IconButton(
                                onClick = {
                                    if (replyText.isNotBlank()) {
                                        val newMessage = ReportMessage(
                                            senderRole = "Tu (Raportor Anonim)",
                                            content = replyText.trim()
                                        )
                                        ReportRepository.addMessage(report.id, newMessage)
                                        searchedReport = report.copy(messages = report.messages + newMessage)
                                        replyText = ""
                                    }
                                },
                                enabled = replyText.isNotBlank()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = stringResource(R.string.send_reply_button),
                                    tint = if (replyText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatusBadge(status: ReportStatus) {
    val (labelRes, containerColor, contentColor, icon) = when (status) {
        ReportStatus.PENDING -> Quadruple(
            R.string.status_pending,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Default.HourglassEmpty
        )
        ReportStatus.UNDER_INVESTIGATION -> Quadruple(
            R.string.status_under_investigation,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Default.Search
        )
        ReportStatus.RESOLVED -> Quadruple(
            R.string.status_resolved,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Default.CheckCircle
        )
        ReportStatus.REJECTED -> Quadruple(
            R.string.status_rejected,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Warning
        )
    }

    SuggestionChip(
        onClick = { },
        label = { Text(stringResource(labelRes), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp)) },
        modifier = Modifier.height(28.dp),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
            iconContentColor = contentColor
        ),
        border = null
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
