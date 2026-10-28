package com.civiceu.com.data

import android.net.Uri
import android.util.Log
import com.civiceu.com.model.CorruptionReport
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

object FirebaseUploader {
    private const val TAG = "FirebaseUploader"

    /**
     * Uploads the encrypted report metadata to Firestore, and all E2EE encrypted files to Firebase Storage.
     * All connections to Firebase use secure HTTPS/SSL by default.
     */
    suspend fun uploadReport(report: CorruptionReport, rawEmailBody: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance().reference

            // 1. Upload encrypted files to Firebase Storage
            val uploadedFilePaths = mutableListOf<String>()
            report.evidenceList.forEach { evidence ->
                val file = File(evidence.encryptedFilePath)
                if (file.exists()) {
                    val fileRef = storage.child("evidence/${report.id}/${evidence.id}.enc")
                    fileRef.putFile(Uri.fromFile(file)).await()
                    uploadedFilePaths.add(fileRef.path)
                }
            }

            // 2. Upload Report Data to Firestore
            // The "message" sub-map is formatted specially to trigger Firebase "Trigger Email" Extension.
            val reportMap = hashMapOf(
                "id" to report.id,
                "title" to report.title,
                "description" to report.description,
                "category" to report.category,
                "timestamp" to report.timestamp,
                "senderName" to report.senderName, // Usually "" if anonymous
                "senderAddress" to report.senderAddress,
                "senderPhone" to report.senderPhone,
                "targetAgencyEmail" to report.targetAgencyEmail,
                "trackingCode" to report.trackingCode,
                "status" to report.status.name,
                "uploadedFiles" to uploadedFilePaths,
                
                // Triggers "Trigger Email from Firestore" extension
                "to" to report.targetAgencyEmail,
                "message" to hashMapOf(
                    "subject" to "SESIZARE CORUPȚIE (Sistem CivicEU): ${report.title}",
                    "text" to rawEmailBody
                )
            )

            db.collection("reports").document(report.id).set(reportMap).await()

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading to Firebase over SSL", e)
            false
        }
    }
}
