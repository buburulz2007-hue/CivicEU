package com.civiceu.com.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.civiceu.com.R
import com.civiceu.com.crypto.DocumentEncryptor
import com.civiceu.com.data.ReportRepository
import com.civiceu.com.model.CorruptionReport
import com.civiceu.com.model.EncryptedEvidence
import com.civiceu.com.model.EuropeanAgencies
import com.civiceu.com.model.QuickReportTemplates
import com.civiceu.com.util.UserProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var senderName by remember { mutableStateOf("") }
    var senderAddress by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }

    var evidenceList by remember { mutableStateOf<List<EncryptedEvidence>>(emptyList()) }
    var isEncrypting by remember { mutableStateOf(false) }

    var selectedKeyEvidence by remember { mutableStateOf<EncryptedEvidence?>(null) }
    var selectedPreviewEvidence by remember { mutableStateOf<EncryptedEvidence?>(null) }
    var createdReportForDialog by remember { mutableStateOf<CorruptionReport?>(null) }

    var termsAccepted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showTermsError by remember { mutableStateOf(false) }

    var selectedAgency by remember { mutableStateOf(EuropeanAgencies.first()) }
    var expanded by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            isEncrypting = true
            coroutineScope.launch(Dispatchers.IO) {
                val newItems = mutableListOf<EncryptedEvidence>()
                for (uri in uris) {
                    val encrypted = DocumentEncryptor.encryptFile(context, uri)
                    if (encrypted != null) {
                        newItems.add(encrypted)
                    }
                }
                withContext(Dispatchers.Main) {
                    evidenceList = evidenceList + newItems
                    isEncrypting = false
                }
            }
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text(stringResource(R.string.terms_title)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(stringResource(R.string.terms_content))
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("OK")
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

    createdReportForDialog?.let { report ->
        SuccessTrackingDialog(
            report = report,
            onDismiss = {
                createdReportForDialog = null
                onBack()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.new_report), fontWeight = FontWeight.Bold) },
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
            SectionHeader(title = stringResource(R.string.incident_details), icon = Icons.Default.Info)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.quick_templates_title),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(QuickReportTemplates) { template ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        title = template.title
                                        category = template.category
                                        description = template.descriptionSkeleton
                                        EuropeanAgencies.find { it.name.contains(template.defaultAgencyName, ignoreCase = true) }?.let { agency ->
                                            selectedAgency = agency
                                        }
                                    },
                                    label = { Text("${template.iconName} ${template.title}", fontSize = 11.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    StyledTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = stringResource(R.string.title_label),
                        placeholder = stringResource(R.string.title_placeholder)
                    )

                    StyledTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = stringResource(R.string.category_label),
                        placeholder = stringResource(R.string.category_placeholder)
                    )

                    StyledTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = stringResource(R.string.description_label),
                        placeholder = stringResource(R.string.description_placeholder),
                        minLines = 4
                    )

                    // Agency Selector
                    Text(stringResource(R.string.target_agency), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedAgency.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            EuropeanAgencies.forEach { agency ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(agency.name)
                                            Text(agency.country, style = MaterialTheme.typography.labelSmall)
                                        }
                                    },
                                    onClick = {
                                        selectedAgency = agency
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // End-to-End Encrypted Evidence Section
            SectionHeader(title = stringResource(R.string.e2e_evidence_title), icon = Icons.Default.Lock)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                stringResource(R.string.e2e_evidence_info),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                stringResource(R.string.exif_info_banner),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isEncrypting
                    ) {
                        if (isEncrypting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.encrypting_files))
                        } else {
                            Icon(Icons.Default.AttachFile, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.add_evidence_button))
                        }
                    }

                    if (evidenceList.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            evidenceList.forEach { evidence ->
                                EvidenceItemCard(
                                    evidence = evidence,
                                    onShowKey = { selectedKeyEvidence = evidence },
                                    onPreview = { selectedPreviewEvidence = evidence },
                                    onDelete = { evidenceList = evidenceList - evidence }
                                )
                            }
                        }
                    }
                }
            }

            SectionHeader(title = stringResource(R.string.who_are_you), icon = Icons.Default.Person)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    WarningBanner()

                    var isOfficialMode by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !isOfficialMode,
                            onClick = {
                                isOfficialMode = false
                                senderName = ""
                                senderAddress = ""
                                senderPhone = ""
                            },
                            label = { Text(stringResource(R.string.anonymous_mode_chip), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = isOfficialMode,
                            onClick = {
                                isOfficialMode = true
                                val profile = UserProfileManager.getProfile(context)
                                senderName = profile.fullName
                                senderAddress = profile.address
                                senderPhone = profile.phone
                            },
                            label = { Text(stringResource(R.string.official_mode_chip), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    StyledTextField(
                        value = senderName,
                        onValueChange = { senderName = it },
                        label = stringResource(R.string.name_label),
                        placeholder = stringResource(R.string.name_placeholder)
                    )

                    StyledTextField(
                        value = senderAddress,
                        onValueChange = { senderAddress = it },
                        label = stringResource(R.string.address_label),
                        placeholder = stringResource(R.string.address_placeholder)
                    )

                    StyledTextField(
                        value = senderPhone,
                        onValueChange = { senderPhone = it },
                        label = stringResource(R.string.phone_label),
                        placeholder = stringResource(R.string.phone_placeholder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = {
                            termsAccepted = it
                            if (it) showTermsError = false
                        }
                    )
                    TextButton(onClick = { showTermsDialog = true }) {
                        Text(
                            stringResource(R.string.terms_accept),
                            color = if (showTermsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (showTermsError) {
                    Text(
                        stringResource(R.string.terms_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            var showSecurityOverlay by remember { mutableStateOf(false) }
            var onSecurityOverlayComplete by remember { mutableStateOf<(() -> Unit)?>(null) }

            if (showSecurityOverlay) {
                SecurityProcessingDialog(
                    onComplete = {
                        showSecurityOverlay = false
                        onSecurityOverlayComplete?.invoke()
                        onSecurityOverlayComplete = null
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            if (!termsAccepted) {
                                showTermsError = true
                                return@Button
                            }
                            val newReport = CorruptionReport(
                                title = title,
                                description = description,
                                category = category,
                                senderName = senderName,
                                senderAddress = senderAddress,
                                senderPhone = senderPhone,
                                targetAgencyEmail = selectedAgency.email,
                                evidenceList = evidenceList
                            )
                            onSecurityOverlayComplete = {
                                ReportRepository.addReport(newReport)
                                createdReportForDialog = newReport
                            }
                            showSecurityOverlay = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = title.isNotBlank() && description.isNotBlank(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.save_locally), style = MaterialTheme.typography.titleMedium)
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            if (!termsAccepted) {
                                showTermsError = true
                                return@Button
                            }
                            val newReport = CorruptionReport(
                                title = title,
                                description = description,
                                category = category,
                                senderName = senderName,
                                senderAddress = senderAddress,
                                senderPhone = senderPhone,
                                targetAgencyEmail = selectedAgency.email,
                                evidenceList = evidenceList
                            )
                            onSecurityOverlayComplete = {
                                ReportRepository.addReport(newReport)
                                sendEmailWithEvidence(context, title, description, category, senderName, senderAddress, senderPhone, selectedAgency.email, evidenceList)
                                createdReportForDialog = newReport
                            }
                            showSecurityOverlay = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = title.isNotBlank() && description.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.send_to_agency, selectedAgency.name), style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SuccessTrackingDialog(
    report: CorruptionReport,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val copiedText = stringResource(R.string.credentials_copied_toast)

    AlertDialog(
        onDismissRequest = {},
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.tracking_credentials_saved_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(R.string.tracking_credentials_desc),
                    style = MaterialTheme.typography.bodySmall
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.tracking_code_label), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(
                            report.trackingCode,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(stringResource(R.string.tracking_passcode_label), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(
                            report.trackingPasscode,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val textToCopy = "Cod Urmărire: ${report.trackingCode}\nParolă Secretă: ${report.trackingPasscode}"
                    DocumentEncryptor.copyToClipboard(context, "Tracking Credentials", textToCopy)
                    Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.copy_credentials_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun WarningBanner() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                stringResource(R.string.warning_anonymous),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun EvidenceItemCard(
    evidence: EncryptedEvidence,
    onShowKey: () -> Unit,
    onPreview: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val icon = when {
                    evidence.isPdf -> Icons.Default.PictureAsPdf
                    evidence.isImage -> Icons.Default.Image
                    evidence.isAudio -> Icons.Default.AudioFile
                    else -> Icons.Default.Description
                }
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        evidence.originalName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(evidence.formattedSize, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        SuggestionChip(
                            onClick = { },
                            label = { Text(stringResource(R.string.e2e_badge), fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.height(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = null
                        )
                        if (evidence.isImage) {
                            Spacer(Modifier.width(4.dp))
                            SuggestionChip(
                                onClick = { },
                                label = { Text(stringResource(R.string.exif_cleaned_badge), fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(20.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                border = null
                            )
                        }
                    }
                }
            }

            Row {
                IconButton(onClick = onShowKey, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Key, contentDescription = stringResource(R.string.key_button), tint = MaterialTheme.colorScheme.secondary)
                }
                IconButton(onClick = onPreview, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = stringResource(R.string.preview_button), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_button), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EncryptionKeyDialog(
    evidence: EncryptedEvidence,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val keyCopiedText = stringResource(R.string.key_copied_toast)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.key_dialog_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(R.string.key_dialog_desc),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    evidence.originalName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        evidence.encryptionKeyBase64,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    DocumentEncryptor.copyToClipboard(context, "E2EE Decryption Key", evidence.encryptionKeyBase64)
                    Toast.makeText(context, keyCopiedText, Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.copy_key))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
fun EvidencePreviewDialog(
    evidence: EncryptedEvidence,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var decryptedFile by remember { mutableStateOf<File?>(null) }
    var isDecrypting by remember { mutableStateOf(true) }

    LaunchedEffect(evidence) {
        withContext(Dispatchers.IO) {
            val file = DocumentEncryptor.decryptToTempFile(context, evidence)
            withContext(Dispatchers.Main) {
                decryptedFile = file
                isDecrypting = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.preview_dialog_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 350.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isDecrypting) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.decrypting_file), style = MaterialTheme.typography.bodySmall)
                    }
                } else if (decryptedFile != null && decryptedFile!!.exists()) {
                    if (evidence.isImage) {
                        val bitmap = remember(decryptedFile) {
                            BitmapFactory.decodeFile(decryptedFile!!.absolutePath)
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = evidence.originalName,
                                modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp)
                            )
                        } else {
                            Text(stringResource(R.string.decryption_error))
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (evidence.isPdf) Icons.Default.PictureAsPdf else Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(evidence.originalName, fontWeight = FontWeight.Bold)
                            Text(evidence.formattedSize, style = MaterialTheme.typography.labelMedium)
                            Text(
                                "MIME: ${evidence.mimeType}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    try {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            decryptedFile!!
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, evidence.mimeType)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, e.message ?: "No app found to open file", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Deschide fișierul")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.decryption_error))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

private fun sendEmailWithEvidence(
    context: Context,
    title: String,
    description: String,
    category: String,
    senderName: String,
    senderAddress: String,
    senderPhone: String,
    targetEmail: String,
    evidenceList: List<EncryptedEvidence>
) {
    val res = context.resources
    val unspecified = res.getString(R.string.unspecified)

    var emailBody = """
        ${res.getString(R.string.email_salutation)}
        
        ${res.getString(R.string.email_intro, 
            if (senderName.isBlank()) unspecified else senderName,
            if (senderAddress.isBlank()) unspecified else senderAddress,
            if (senderPhone.isBlank()) unspecified else senderPhone
        )}
        
        ${res.getString(R.string.email_report_title, title)}
        ${res.getString(R.string.email_report_category, category)}
        
        ${res.getString(R.string.email_report_desc)}
        $description
    """.trimIndent()

    if (evidenceList.isNotEmpty()) {
        val evidenceDetails = StringBuilder("\n\n" + res.getString(R.string.email_e2e_attached_header) + "\n")
        evidenceList.forEach { item ->
            evidenceDetails.append(res.getString(R.string.email_e2e_attached_item, item.originalName, item.formattedSize, item.id)).append("\n")
        }
        evidenceDetails.append("\n").append(res.getString(R.string.email_e2e_note))
        emailBody += evidenceDetails.toString()
    }

    emailBody += "\n\n" + res.getString(R.string.email_footer) + "\n" +
            res.getString(R.string.email_date, SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))

    val sha256Fingerprint = try {
        val bytes = MessageDigest.getInstance("SHA-256").digest(emailBody.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        "UNAVAILABLE"
    }
    
    emailBody += res.getString(R.string.email_pgp_seal, sha256Fingerprint)

    val attachmentUris = ArrayList<Uri>()
    evidenceList.forEach { item ->
        val file = File(item.encryptedFilePath)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            attachmentUris.add(uri)
        }
    }

    val intent = if (attachmentUris.isNotEmpty()) {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(targetEmail))
            putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.email_subject, title))
            putExtra(Intent.EXTRA_TEXT, emailBody)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(targetEmail))
            putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.email_subject, title))
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Send via..."))
    } catch (_: Exception) { }
}
