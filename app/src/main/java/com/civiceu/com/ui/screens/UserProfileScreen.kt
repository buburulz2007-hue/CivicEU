package com.civiceu.com.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.civiceu.com.R
import com.civiceu.com.model.UserProfile
import com.civiceu.com.util.UserProfileManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val savedProfile = UserProfileManager.getProfile(context)
        fullName = savedProfile.fullName
        email = savedProfile.email
        address = savedProfile.address
        phone = savedProfile.phone
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile_settings_title), fontWeight = FontWeight.Bold) },
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
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        stringResource(R.string.profile_encrypted_info),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.profile_details_section), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    StyledTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = stringResource(R.string.name_label),
                        placeholder = stringResource(R.string.name_placeholder)
                    )

                    StyledTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail (Opțional)",
                        placeholder = "exemplu@email.com"
                    )

                    StyledTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = stringResource(R.string.address_label),
                        placeholder = stringResource(R.string.address_placeholder)
                    )

                    StyledTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = stringResource(R.string.phone_label),
                        placeholder = stringResource(R.string.phone_placeholder)
                    )

                    Button(
                        onClick = {
                            val newProfile = UserProfile(
                                fullName = fullName.trim(),
                                email = email.trim(),
                                address = address.trim(),
                                phone = phone.trim()
                            )
                            UserProfileManager.saveProfile(context, newProfile)
                            Toast.makeText(context, "Profil salvat și criptat local!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.save_profile_button))
                    }

                    OutlinedButton(
                        onClick = {
                            UserProfileManager.clearProfile(context)
                            fullName = ""
                            email = ""
                            address = ""
                            phone = ""
                            Toast.makeText(context, "Profilul a fost șters!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.clear_profile_button))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val packageName = context.packageName
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFFFD700))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.rate_app_button))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.theme_settings_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val currentTheme by com.civiceu.com.util.ThemeManager.themeMode.collectAsState()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeSelectionChip(
                            title = stringResource(R.string.theme_system),
                            selected = currentTheme == com.civiceu.com.util.ThemeManager.THEME_SYSTEM,
                            onClick = { com.civiceu.com.util.ThemeManager.setTheme(context, com.civiceu.com.util.ThemeManager.THEME_SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeSelectionChip(
                            title = stringResource(R.string.theme_light),
                            selected = currentTheme == com.civiceu.com.util.ThemeManager.THEME_LIGHT,
                            onClick = { com.civiceu.com.util.ThemeManager.setTheme(context, com.civiceu.com.util.ThemeManager.THEME_LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeSelectionChip(
                            title = stringResource(R.string.theme_dark),
                            selected = currentTheme == com.civiceu.com.util.ThemeManager.THEME_DARK,
                            onClick = { com.civiceu.com.util.ThemeManager.setTheme(context, com.civiceu.com.util.ThemeManager.THEME_DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Security PIN Settings
            var showPinDialog by remember { mutableStateOf(false) }
            val hasPin = com.civiceu.com.crypto.SecurityManager.hasPinSet(context)

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(R.string.security_setup_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (!hasPin) {
                        Button(
                            onClick = { showPinDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.security_setup_btn))
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                com.civiceu.com.crypto.SecurityManager.clearPins(context)
                                Toast.makeText(context, "Securitate PIN dezactivată", Toast.LENGTH_SHORT).show()
                                // Recompose trigger if we had a state, but this works fine
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(R.string.security_remove_btn))
                        }
                    }
                }
            }
            
            if (showPinDialog) {
                PinSetupDialog(
                    onDismiss = { showPinDialog = false },
                    onSave = { realPin, decoyPin ->
                        com.civiceu.com.crypto.SecurityManager.setPins(context, realPin, decoyPin)
                        showPinDialog = false
                        Toast.makeText(context, "PIN-uri salvate cu succes!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun PinSetupDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var realPin by remember { mutableStateOf("") }
    var decoyPin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.security_setup_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = realPin,
                    onValueChange = { realPin = it },
                    label = { Text(stringResource(R.string.pin_real_placeholder)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = decoyPin,
                    onValueChange = { decoyPin = it },
                    label = { Text(stringResource(R.string.pin_decoy_placeholder)) },
                    singleLine = true
                )
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (realPin.length < 4 || decoyPin.length < 4) {
                    errorMsg = "Ambele PIN-uri trebuie să aibă minim 4 cifre."
                } else if (realPin == decoyPin) {
                    errorMsg = "PIN-ul de camuflaj trebuie să fie diferit de cel real."
                } else {
                    onSave(realPin, decoyPin)
                }
            }) {
                Text(stringResource(R.string.pin_save))
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
fun ThemeSelectionChip(title: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
