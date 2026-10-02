package com.civiceu.com.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civiceu.com.R

data class MediaPartner(
    val name: String,
    val country: String,
    val email: String,
    val description: String,
    val website: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalistHubScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val partners = listOf(
        // EU Level
        MediaPartner(
            name = "Transparency International EU",
            country = "Uniunea Europeană",
            email = "eu@transparency.org",
            description = stringResource(R.string.ngo_ti_desc),
            website = "https://www.transparency.eu"
        ),
        MediaPartner(
            name = "Investigate Europe",
            country = "Uniunea Europeană",
            email = "contact@investigate-europe.eu",
            description = "Pan-European investigative journalism cooperative covering cross-border systemic corruption.",
            website = "https://www.investigate-europe.eu"
        ),
        // Romania
        MediaPartner(
            name = "Rise Project",
            country = "România",
            email = "contact@riseproject.ro",
            description = stringResource(R.string.ngo_rise_desc),
            website = "https://www.riseproject.ro"
        ),
        MediaPartner(
            name = "Funky Citizens",
            country = "România",
            email = "contact@funkycitizens.org",
            description = stringResource(R.string.ngo_funky_desc),
            website = "https://funkycitizens.org"
        ),
        MediaPartner(
            name = "Centrul de Resurse Juridice (CRJ)",
            country = "România",
            email = "crj@crj.ro",
            description = stringResource(R.string.ngo_crj_desc),
            website = "https://www.crj.ro"
        ),
        // France
        MediaPartner(
            name = "Mediapart",
            country = "Franța",
            email = "enquetes@mediapart.fr",
            description = "Leading independent French investigative newspaper uncovering political and financial scandals.",
            website = "https://www.mediapart.fr"
        ),
        MediaPartner(
            name = "Sherpa NGO",
            country = "Franța",
            email = "contact@asso-sherpa.org",
            description = "Protects and defends victims of economic crimes, corruption, and corporate impunity.",
            website = "https://www.asso-sherpa.org"
        ),
        // Italy
        MediaPartner(
            name = "IRPI Media",
            country = "Italia",
            email = "redazione@irpimedia.com",
            description = "Investigative Reporting Project Italy focusing on organized crime, corruption, and abuse of power.",
            website = "https://www.irpimedia.com"
        ),
        MediaPartner(
            name = "A Sud",
            country = "Italia",
            email = "info@asud.net",
            description = "Italian civic association monitoring environmental justice, public funds misuse, and transparency.",
            website = "https://www.asud.net"
        ),
        // Poland
        MediaPartner(
            name = "Fundacja Reporterów",
            country = "Polonia",
            email = "kontakt@fundacjareporterow.pl",
            description = "Polish investigative journalism center exposing misuse of public funds and international corruption.",
            website = "https://www.fundacjareporterow.pl"
        ),
        MediaPartner(
            name = "Sieć Obywatelska Watchdog",
            country = "Polonia",
            email = "biuro@siecobywatelska.pl",
            description = "Defends public access to information and fights institutional secrecy.",
            website = "https://www.siecobywatelska.pl"
        ),
        // Ukraine
        MediaPartner(
            name = "Bihus.Info",
            country = "Ucraina",
            email = "info@bihus.info",
            description = "Leading Ukrainian anti-corruption investigative project exposing high-level state procurement fraud.",
            website = "https://bihus.info"
        ),
        MediaPartner(
            name = "Slidstvo.Info",
            country = "Ucraina",
            email = "info@slidstvo.info",
            description = "Independent Ukrainian investigative agency uncovering embezzlement and public office abuse.",
            website = "https://www.slidstvo.info"
        ),
        // Lithuania
        MediaPartner(
            name = "Siena.lt",
            country = "Lituania",
            email = "info@siena.lt",
            description = "Baltic investigative journalism center specializing in cross-border financial crime and corruption.",
            website = "https://siena.lt"
        ),
        // Portugal
        MediaPartner(
            name = "Fumaça",
            country = "Portugalia",
            email = "geral@fumaca.pt",
            description = "Independent Portuguese investigative journalism media producing deep-dive corruption and policy reports.",
            website = "https://fumaca.pt"
        ),
        // Croatia
        MediaPartner(
            name = "Oštro",
            country = "Croația",
            email = "info@ostro.hr",
            description = "Center for Investigative Journalism in Croatia and the Adriatic region exposing financial crimes.",
            website = "https://www.ostro.hr"
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.journalist_hub_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(
                            stringResource(R.string.journalist_hub_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            items(partners) { partner ->
                MediaPartnerCard(partner = partner, onContactClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(partner.email))
                        putExtra(Intent.EXTRA_SUBJECT, "SESIZARE / CAZ CORUPȚIE (Civic App)")
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send via..."))
                    } catch (_: Exception) {}
                })
            }
        }
    }
}

@Composable
fun MediaPartnerCard(partner: MediaPartner, onContactClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = partner.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SuggestionChip(
                    onClick = { },
                    label = { Text(partner.country, fontSize = 10.sp) },
                    modifier = Modifier.height(24.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    border = null
                )
            }

            Text(
                text = partner.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = partner.email,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onContactClick,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.share_with_ngo), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
