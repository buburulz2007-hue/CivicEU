package com.civiceu.com.data

import com.civiceu.com.model.CorruptionReport
import com.civiceu.com.model.ReportMessage
import com.civiceu.com.model.ReportStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ReportRepository {
    private val _reports = MutableStateFlow<List<CorruptionReport>>(emptyList())
    val reports: StateFlow<List<CorruptionReport>> = _reports.asStateFlow()

    fun addReport(report: CorruptionReport) {
        _reports.value = _reports.value + report
    }

    fun findReportByTracking(code: String, passcode: String): CorruptionReport? {
        val cleanCode = code.trim().uppercase()
        val cleanPasscode = passcode.trim()
        return _reports.value.find {
            it.trackingCode.equals(cleanCode, ignoreCase = true) && it.trackingPasscode == cleanPasscode
        }
    }

    fun addMessage(reportId: String, message: ReportMessage) {
        _reports.value = _reports.value.map { report ->
            if (report.id == reportId) {
                report.copy(messages = report.messages + message)
            } else {
                report
            }
        }
    }

    fun updateStatus(reportId: String, newStatus: ReportStatus) {
        _reports.value = _reports.value.map { report ->
            if (report.id == reportId) {
                report.copy(status = newStatus)
            } else {
                report
            }
        }
    }
}
