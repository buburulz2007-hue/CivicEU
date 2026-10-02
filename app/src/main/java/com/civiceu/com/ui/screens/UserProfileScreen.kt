package com.civiceu.com.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                }
            }
        }
    }
}
