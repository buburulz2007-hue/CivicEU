package com.civiceu.com.ai

import com.civiceu.com.model.Agency
import com.civiceu.com.model.EuropeanAgencies
import java.util.Locale

object AiReportAnalyzer {

    data class AiAnalysisResult(
        val title: String,
        val category: String,
        val summary: String,
        val targetAgency: Agency
    )

    /**
     * Analyzes spoken or raw whistleblower testimony text using local heuristic AI NLP to extract
     * title, category, summary, and match the most appropriate anti-corruption agency.
     */
    fun analyzeTestimony(spokenText: String): AiAnalysisResult {
        val text = spokenText.lowercase(Locale.getDefault())

        val category = when {
            text.contains("sanatate") || text.contains("spital") || text.contains("medic") -> "Sănătate"
            text.contains("educatie") || text.contains("scoala") || text.contains("universitate") -> "Educație"
            text.contains("achizitie") || text.contains("licitatie") || text.contains("contract") || text.contains("bani publici") -> "Achizitii Publice"
            text.contains("politie") || text.contains("primarie") || text.contains("functionar") -> "Administrație Publică"
            else -> "Corupție Generală"
        }

        val title = if (spokenText.length > 40) spokenText.substring(0, 40) + "..." else spokenText

        // Match agency based on keywords or default to DNA for Romania / OLAF for EU
        val agency = when {
            text.contains("europa") || text.contains("fonduri europene") || text.contains("ue") -> EuropeanAgencies.find { it.name.contains("OLAF") } ?: EuropeanAgencies.first()
            text.contains("franta") -> EuropeanAgencies.find { it.name.contains("AFA") } ?: EuropeanAgencies.first()
            text.contains("italia") -> EuropeanAgencies.find { it.name.contains("ANAC") } ?: EuropeanAgencies.first()
            else -> EuropeanAgencies.find { it.name.contains("DNA") } ?: EuropeanAgencies.first()
        }

        return AiAnalysisResult(
            title = if (title.isBlank()) "Sesizare AI Anticorupție" else title,
            category = category,
            summary = spokenText,
            targetAgency = agency
        )
    }
}
