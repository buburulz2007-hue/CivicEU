package com.civiceu.com.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object SmtpEmailSender {
    private const val TAG = "SmtpEmailSender"
    private const val SENDER_EMAIL = "civiceu.reports@gmail.com"
    private const val SENDER_PASS = "htsdekdwiysbelgw" 

    /**
     * Sends an email in the background via Gmail's SMTP server securely using TLS.
     */
    suspend fun sendEmailBackground(toEmail: String, subject: String, bodyText: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val properties = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(SENDER_EMAIL, SENDER_PASS)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL, "CivicEU Secure Platform"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                    setSubject(subject)
                    setText(bodyText)
                }

                Transport.send(message)
                Log.d(TAG, "Background email sent successfully to $toEmail")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send background email via SMTP", e)
                false
            }
        }
    }
}
