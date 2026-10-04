const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

admin.initializeApp();

// === CONFIGURARE SMTP ===
// Aceste date vor fi inlocuite cu contul real de la care se trimit mailurile.
// Pentru productie este recomandat sa folosesti Firebase Secrets sau Environment Variables,
// dar le lasam hardcodate aici pentru usurinta instalarii.
const SMTP_USER = "emailul_tau@gmail.com"; // Inlocuieste
const SMTP_PASS = "parola_de_aplicatie"; // Inlocuieste (ex. Google App Password)

const transporter = nodemailer.createTransport({
    service: "gmail", // sau completeaza cu setarile tale (host, port)
    auth: {
        user: SMTP_USER,
        pass: SMTP_PASS,
    },
});

/**
 * Cloud Function care se declanseaza automat ori de cate ori se adauga un document NOU
 * in colectia "reports" din Firestore.
 */
exports.sendReportEmail = functions.firestore
    .document("reports/{reportId}")
    .onCreate(async (snap, context) => {
        const reportData = snap.data();

        // Preluam campurile generate de aplicatia Android in `FirebaseUploader.kt`
        const targetEmail = reportData.to;
        const messagePayload = reportData.message;

        if (!targetEmail || !messagePayload || !messagePayload.text) {
            console.log("Raport incomplet, nu s-a trimis e-mailul. Lipesc atribute de routing.");
            return null;
        }

        const mailOptions = {
            from: `"CivicEU Secure Platform" <${SMTP_USER}>`,
            to: targetEmail,
            subject: messagePayload.subject || "SESIZARE CORUPTIE (Sistem CivicEU)",
            text: messagePayload.text,
            // Aici nu mai trimitem fisiere (sunt prea mari).
            // Am specificat procurorilor in email ca pot extrage si decripta dovezile cerand cheile de la avertizor.
        };

        try {
            const info = await transporter.sendMail(mailOptions);
            console.log(`Email trimis cu succes catre ${targetEmail}. ID: ${info.messageId}`);

            // Optional: Marcam in Firestore ca s-a trimis emailul
            return snap.ref.update({ emailStatus: "SENT" }, { merge: true });
        } catch (error) {
            console.error(`Eroare la trimiterea emailului catre ${targetEmail}: `, error);

            // Optional: Marcam in Firestore eroarea
            return snap.ref.update({ emailStatus: "ERROR", emailErrorDetails: error.message }, { merge: true });
        }
    });
