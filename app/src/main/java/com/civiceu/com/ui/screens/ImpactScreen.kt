package com.civiceu.com.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssuredWorkload
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civiceu.com.R

data class CountryStat(val name: String, val count: Int, val flag: String)
data class InstitutionStat(val name: String, val sent: Int, val ack: Int, val status: String, val statusColor: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactScreen(onBack: () -> Unit) {
    // Mock Data simulating real-time server fetch
    val countryStats = listOf(
        CountryStat("România", 8430, "🇷🇴"),
        CountryStat("Franța", 2150, "🇫🇷"),
        CountryStat("Italia", 1820, "🇮🇹"),
        CountryStat("Polonia", 940, "🇵🇱"),
        CountryStat("Ucraina", 910, "🇺🇦")
    )
    val instStats = listOf(
        InstitutionStat("A.N.I. (România)", 142, 138, "📧 Primit / În analiză", Color(0xFF2196F3)),
        InstitutionStat("DNA (România)", 89, 85, "⚖️ În lucru", Color(0xFFFF9800)),
        InstitutionStat("EPPO (EU)", 45, 45, "🇪🇺 Verificare", Color(0xFF4CAF50)),
        InstitutionStat("PNF (Franța)", 24, 22, "⏳ În așteptare", Color(0xFF9E9E9E))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.impact_title), fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Global Impact Indicators
            SectionHeader(title = stringResource(R.string.global_stats_title), icon = Icons.Default.Insights)
            
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.total_reports_ue),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "14,250",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("68%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.anonymous_reports), style = MaterialTheme.typography.labelMedium)
                        }
                        Divider(modifier = Modifier.height(40.dp).width(1.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("32%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.assumed_reports), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${stringResource(R.string.avg_time)}: ",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                stringResource(R.string.avg_time_val),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 2. Interactive Map / Country Distribution
            SectionHeader(title = stringResource(R.string.map_section_title), icon = Icons.Default.Public)
            Text(
                stringResource(R.string.map_section_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    countryStats.forEach { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stat.flag, fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(stat.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Text(
                                "%,d".format(stat.count),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (stat != countryStats.last()) {
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // 3. Institution Monitoring (Public Pressure)
            SectionHeader(title = stringResource(R.string.institutions_section_title), icon = Icons.Default.AssuredWorkload)
            
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.inst_col_name), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.5f))
                        Text(stringResource(R.string.inst_col_sent), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                        Text(stringResource(R.string.inst_col_ack), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.6f), textAlign = TextAlign.Center)
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    instStats.forEach { inst ->
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(inst.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    color = inst.statusColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        inst.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = inst.statusColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                inst.sent.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                inst.ack.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        if (inst != instStats.last()) {
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}


