package com.civiceu.com.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.civiceu.com.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalInfoScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Privacy Policy, 1 = Terms of Service

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.privacy_policy_title), fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab, modifier = Modifier.padding(top = 8.dp)) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Confidențialitate") },
                    icon = { Icon(Icons.Default.PrivacyTip, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Termeni și Condiții") },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = null) }
                )
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedTab == 0) {
                        PrivacyPolicyContent()
                    } else {
                        TermsOfServiceContent()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PrivacyPolicyContent() {
    Text("Politica de Confidențialitate - CivicEU", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text("Ultima actualizare: Octombrie 2026", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(8.dp))
    
    Text("1. Angajamentul nostru pentru confidențialitate", fontWeight = FontWeight.Bold)
    Text("CivicEU este o aplicație open-source dedicată raportării corupției în mod sigur și anonim. Aplicăm o arhitectură strictă de tip 'Zero-Knowledge': datele tale personale nu sunt stocate pe servere centralizate.")

    Text("2. Prelucrarea datelor", fontWeight = FontWeight.Bold)
    Text("• Raportare Anonimă (Implicit): Nu colectăm nume, e-mail, telefon sau ID-uri de dispozitiv.\n• Eliminarea Metadatelor: Orice fișier atașat este curățat automat de coordonatele GPS și metadatele EXIF direct pe telefon.\n• Criptare E2EE: Toate datele sunt criptate cu AES-256-GCM.")

    Text("3. Suveranitatea datelor", fontWeight = FontWeight.Bold)
    Text("Rapoartele sunt trimise direct către canalele oficiale ale instituțiilor sau prin servere securizate găzduite exclusiv în Uniunea Europeană, respectând GDPR.")

    Text("4. Drepturile GDPR", fontWeight = FontWeight.Bold)
    Text("Pentru rapoartele asumate oficial, aveți dreptul de acces, rectificare sau ștergere conform legislației europene.")
}

@Composable
fun TermsOfServiceContent() {
    Text("Termeni și Condiții de Utilizare", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text("Ultima actualizare: Octombrie 2026", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(8.dp))

    Text("1. Acceptarea termenilor", fontWeight = FontWeight.Bold)
    Text("Prin descărcarea și utilizarea aplicației CivicEU, confirmați că ați citit și acceptat acești termeni și condiții.")

    Text("2. Scopul platformei", fontWeight = FontWeight.Bold)
    Text("CivicEU este un instrument destinat cetățenilor și avertizorilor de integritate pentru raportarea faptelor de corupție și malpraxis în conformitate cu Directiva UE 2019/1937.")

    Text("3. Utilizarea responsabilă", fontWeight = FontWeight.Bold)
    Text("Utilizatorii se angajază să nu folosească aplicația pentru a transmite rapoarte false, defăimătoare sau cu rea-credință. Creatorii nu își asumă responsabilitatea pentru conținutul sesizărilor trimise de utilizatori.")

    Text("4. Proprietate intelectuală", fontWeight = FontWeight.Bold)
    Text("Codul sursă este open-source sub licență MIT, fiind disponibil public pe GitHub pentru audit independent de securitate.")
}
