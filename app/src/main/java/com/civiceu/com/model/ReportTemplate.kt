package com.civiceu.com.model

data class ReportTemplate(
    val id: String,
    val iconName: String,
    val title: String,
    val category: String,
    val descriptionSkeleton: String,
    val defaultAgencyName: String
)

val QuickReportTemplates = listOf(
    ReportTemplate(
        id = "health_bribe",
        iconName = "🏥",
        title = "Solicitare de mită în spital / cadru medical",
        category = "Sănătate",
        descriptionSkeleton = "La data de [DATA], la spitalul [NUME SPITAL / SECȚIE], mi s-a solicitat suma de [SUMA LEI/EUR] de către [NUME / FUNCȚIE] pentru acordarea îngrijirilor medicale.",
        defaultAgencyName = "DNA (Romania)"
    ),
    ReportTemplate(
        id = "construction_permit",
        iconName = "🏗️",
        title = "Mituire pentru autorizație de construcție",
        category = "Administrație Publică",
        descriptionSkeleton = "În cadrul primăriei [NUME LOCALITATE], funcționarii de la direcția de urbanism au condiționat eliberarea autorizației de construcție de plata unei sume necuvenite.",
        defaultAgencyName = "DNA (Romania)"
    ),
    ReportTemplate(
        id = "rigged_procurement",
        iconName = "📑",
        title = "Licitație publică trucată / Caiet de sarcini dedicat",
        category = "Achiziții Publice",
        descriptionSkeleton = "Licitația nr. [NUMĂR LICITAȚIE / SEAP] organizată de [INSTITUȚIE] conține cerințe restrictive dedicate unei anumite firme favorizate.",
        defaultAgencyName = "DNA (Romania)"
    ),
    ReportTemplate(
        id = "traffic_dga",
        iconName = "🚗",
        title = "Condiționare sau pretindere mită examen/control",
        category = "Afaceri Interne / DGA",
        descriptionSkeleton = "La data de [DATA], în locația [LOCAȚIE], s-a pretins suma de [SUMA] pentru trecerea examenului / evitarea sancțiunii.",
        defaultAgencyName = "DNA (Romania)"
    )
)
