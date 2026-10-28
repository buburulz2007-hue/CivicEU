package com.civiceu.com.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.civiceu.com.R
import com.civiceu.com.data.CrashReporter
import com.civiceu.com.data.ReportRepository
import com.civiceu.com.model.CorruptionReport
import com.civiceu.com.model.EncryptedEvidence
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddReportClick: () -> Unit,
    onTrackReportClick: () -> Unit,
    onJournalistHubClick: () -> Unit,
    onLegalAssistantClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onUserProfileClick: () -> Unit,
    onWhistleblowerGuideClick: () -> Unit,
    onImpactClick: () -> Unit,
    onPremiumClick: () -> Unit
) {
    val reports by ReportRepository.reports.collectAsState()
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showDebugLogs by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDebugLogs) {
        AlertDialog(
            onDismissRequest = { showDebugLogs = false },
            title = { Text(stringResource(R.string.debug_logs)) },
            text = {
                val logs = CrashReporter.getLogs(context)
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(logs, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDebugLogs = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    CrashReporter.clearLogs(context)
                    showDebugLogs = false
                }) {
                    Text(stringResource(R.string.clear_logs))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text(stringResource(R.string.home_slogan), style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    val isPro = com.civiceu.com.util.PremiumManager.isPremium(LocalContext.current)
                    val context = LocalContext.current
                    if (isPro) {
                        IconButton(onClick = {
                            com.civiceu.com.crypto.SecurityManager.executePanicWipe(context)
                            android.widget.Toast.makeText(context, "PANIC WIPE EXECUTAT!", android.widget.Toast.LENGTH_LONG).show()
                            (context as? android.app.Activity)?.finishAffinity()
                        }) {
                            Icon(Icons.Default.Warning, contentDescription = "Panic Wipe", tint = androidx.compose.ui.graphics.Color.Red)
                        }
                    }
                    IconButton(onClick = onPremiumClick) {
                        Icon(Icons.Default.Diamond, contentDescription = "Premium", tint = androidx.compose.ui.graphics.Color(0xFFFFD700))
                    }
                    IconButton(onClick = onUserProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile_settings_title))
                    }
                    IconButton(onClick = onTrackReportClick) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.track_report_button))
                    }
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(Icons.Default.Language, contentDescription = stringResource(R.string.change_language))
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            val languages = listOf(
                                "ro" to R.string.language_ro,
                                "en" to R.string.language_en,
                                "fr" to R.string.language_fr,
                                "de" to R.string.language_de,
                                "es" to R.string.language_es,
                                "it" to R.string.language_it
                            )
                            languages.forEach { (code, labelRes) ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(labelRes)) },
                                    onClick = {
                                        changeLanguage(code)
                                        showLanguageMenu = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.debug_logs)) },
                                onClick = {
                                    showDebugLogs = true
                                    showLanguageMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddReportClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.new_report)) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedMenuCard(
                        onClick = onTrackReportClick,
                        icon = Icons.Default.Search,
                        title = stringResource(R.string.track_report_button),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    ElevatedMenuCard(
                        onClick = onJournalistHubClick,
                        icon = Icons.Default.Security,
                        title = stringResource(R.string.transmit_to_press),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedMenuCard(
                        onClick = onLegalAssistantClick,
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = stringResource(R.string.legal_assistant_button),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )

                    ElevatedMenuCard(
                        onClick = onAiAssistantClick,
                        icon = Icons.Default.Mic,
                        title = stringResource(R.string.ai_assistant_button),
                        color = MaterialTheme.colorScheme.error, // Folosim rosu pt AI pentru a ieși în evidență
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedMenuCard(
                        onClick = onWhistleblowerGuideClick,
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        title = stringResource(R.string.whistleblower_guide_button),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    
                    ElevatedMenuCard(
                        onClick = onImpactClick,
                        icon = Icons.Default.BarChart,
                        title = stringResource(R.string.impact_button),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            // Add a spacer or remaining UI if needed
            val reports by ReportRepository.reports.collectAsState()

            if (reports.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            stringResource(R.string.recent_reports),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(reports) { report ->
                        ReportItem(report)
                    }
                }
            }
        }
    }
}

private fun changeLanguage(languageCode: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(R.string.no_reports_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.no_reports_desc),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReportItem(report: CorruptionReport) {
    var showEvidenceDialog by remember { mutableStateOf(false) }
    var selectedKeyEvidence by remember { mutableStateOf<EncryptedEvidence?>(null) }
    var selectedPreviewEvidence by remember { mutableStateOf<EncryptedEvidence?>(null) }

    if (showEvidenceDialog) {
        AlertDialog(
            onDismissRequest = { showEvidenceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.e2e_evidence_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    report.evidenceList.forEach { evidence ->
                        EvidenceItemCard(
                            evidence = evidence,
                            onShowKey = { selectedKeyEvidence = evidence },
                            onPreview = { selectedPreviewEvidence = evidence },
                            onDelete = {}
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEvidenceDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(report.category, fontSize = 10.sp) },
                        modifier = Modifier.height(24.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = null
                    )

                    if (report.evidenceList.isNotEmpty()) {
                        FilterChip(
                            selected = true,
                            onClick = { showEvidenceDialog = true },
                            label = {
                                Text(
                                    stringResource(R.string.attached_evidence_count, report.evidenceList.size),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                            },
                            modifier = Modifier.height(24.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                
                val date = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(report.timestamp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = report.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (report.evidenceList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showEvidenceDialog = true },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.view_evidence) + " (${report.evidenceList.size})",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            if (report.senderName.isNotBlank() || report.senderPhone.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (report.senderName.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.sent_by, report.senderName),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (report.senderPhone.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.phone_short, report.senderPhone),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            if (report.targetAgencyEmail.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Agency: ${report.targetAgencyEmail}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ElevatedMenuCard(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = color.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
