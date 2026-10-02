package com.civiceu.com.model

data class Agency(
    val name: String,
    val email: String,
    val country: String // "EU" for OLAF/EPPO
)

val EuropeanAgencies = listOf(
    // EU Level
    Agency("OLAF (EU Anti-Fraud)", "OLAF-COURRIER@ec.europa.eu", "EU"),
    Agency("EPPO (EU Prosecutor)", "info@eppo.europa.eu", "EU"),
    
    // National Agencies
    Agency("DNA (Romania)", "sesizare@pna.ro", "Romania"),
    Agency("AFA (France)", "afa@afa.gouv.fr", "France"),
    Agency("ANAC (Italy)", "protocollo@pec.anticorruzione.it", "Italy"),
    Agency("CBA (Poland)", "sygnal@cba.gov.pl", "Poland"),
    Agency("NABU (Ukraine)", "info@nabu.gov.ua", "Ukraine"),
    Agency("STT (Lithuania)", "pranesk@stt.lt", "Lithuania"),
    Agency("MENAC (Portugal)", "geral@mec-anticorrupcao.pt", "Portugal"),
    Agency("USKOK (Croatia)", "Tajnistvo@uskok.dorh.hr", "Croatia")
)
