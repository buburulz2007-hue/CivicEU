package com.civiceu.com.model

import java.security.SecureRandom
import java.util.UUID

enum class ReportStatus {
    PENDING,
    UNDER_INVESTIGATION,
    RESOLVED,
    REJECTED
}

data class ReportMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderRole: String, // "Autoritate / Jurnalist" vs "Raportor"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CorruptionReport(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderAddress: String = "",
    val senderPhone: String = "",
    val targetAgencyEmail: String = "",
    val evidenceList: List<EncryptedEvidence> = emptyList(),
    val trackingCode: String = generateTrackingCode(),
    val trackingPasscode: String = generatePasscode(),
    val status: ReportStatus = ReportStatus.PENDING,
    val messages: List<ReportMessage> = emptyList()
) {
    companion object {
        fun generateTrackingCode(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            val random = SecureRandom()
            val part1 = (1..4).map { chars[random.nextInt(chars.length)] }.joinToString("")
            val part2 = (1..4).map { chars[random.nextInt(chars.length)] }.joinToString("")
            return "CIVIC-$part1-$part2"
        }

        fun generatePasscode(): String {
            val random = SecureRandom()
            val pin = random.nextInt(900000) + 100000
            return pin.toString()
        }
    }
}
