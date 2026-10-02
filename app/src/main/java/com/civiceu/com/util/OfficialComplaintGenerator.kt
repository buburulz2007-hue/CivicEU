package com.civiceu.com.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OfficialComplaintGenerator {
    fun generateComplaintText(
        context: Context,
        title: String,
        description: String,
        category: String,
        senderName: String,
        senderAddress: String,
        senderPhone: String,
        agencyName: String
    ): String {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        return """
            CĂTRE: $agencyName
            DATA: $dateStr
            
            PLÂNGERE / SESIZARE OFICIALĂ PRIVIND FAPTE DE CORUPȚIE
            
            I. DATELE DE IDENTIFICARE ALE PETENTULUI:
            - Nume și Prenume: ${if (senderName.isBlank()) "[NESPECIFICAT - ANONIM]" else senderName}
            - Domiciliu / Adresă: ${if (senderAddress.isBlank()) "[NESPECIFICAT]" else senderAddress}
            - Telefon / Contact: ${if (senderPhone.isBlank()) "[NESPECIFICAT]" else senderPhone}
            
            II. OBIECTUL SESIZĂRII:
            Titlu: $title
            Categorie: $category
            
            III. EXPUNEREA DETALIATĂ A FAPTELOR:
            $description
            
            IV. CADRUL LEGAL ȘI SOLICITĂRI:
            În temeiul legislației în vigoare privind combaterea corupției și protecția avertizorilor de integritate, solicit organelor competente verificarea aspectelor sesizate, identificarea persoanelor responsabile și tragerea acestora la răspundere legală.
            
            Subsemnatul/a declar pe propria răspundere, sub sancțiunile prevăzute de legea penală pentru falsul în declarații, că cele expuse în prezenta plângere corespund realității.
            
            Semnătura:
            ${if (senderName.isBlank()) "[ANONIM]" else senderName}
            
            --------------------------------------------------
            Document generat automat prin aplicația Civic.
        """.trimIndent()
    }
}
