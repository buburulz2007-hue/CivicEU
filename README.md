# 🛡️ CivicEU - Secure Whistleblowing Platform

**CivicEU** is an open-source, fully decentralized Android application designed to protect whistleblowers, journalists, and everyday citizens who report corruption, fraud, and public administration abuses. 

Built with absolute anonymity in mind, CivicEU complies with the **EU Whistleblower Protection Directive (2019/1937)** by offering highly secure, untraceable reporting channels.

---

## 🌟 Why CivicEU?

Whistleblowers often face severe retaliation. Traditional reporting forms log IP addresses, metadata, and require emails. **CivicEU takes a Zero-Knowledge approach:**
- **No Central Servers:** Reports are encrypted locally and dispatched directly from the device via secure email intents. We host no databases that can be hacked or subpoenaed.
- **Zero Metadata:** We automatically strip all EXIF, GPS, and device data from media attachments before they leave the phone.
- **Untraceable Identity:** No accounts, no phone numbers, no emails required.

## 🚀 Core Features

### 🔒 1. Military-Grade Security & OpSec
- **End-to-End Encryption (E2EE):** All documents, photos, and audio recordings are encrypted locally using **AES-256-GCM** before transmission.
- **EXIF & GPS Sanitization:** Automatically strips tracking metadata from photos to prevent location or device-based identification.
- **Digital Integrity Seal:** Every report generates a local **SHA-256 Digital Fingerprint** to guarantee the payload hasn't been altered in transit.
- **Anti-Screen Capture:** App screens are protected with `FLAG_SECURE` to prevent spyware, malware, or accidental screenshots from compromising sensitive data.

### 🤖 2. AI Voice Assistant
- Voice-first reporting: Users can simply speak their testimony.
- **On-Device NLP:** Automatically transcribes, categorizes, and structures the report.
- **Smart Routing:** The AI identifies the nature of the crime (e.g., EU funds fraud vs. local bribery) and automatically routes the report to the correct agency (OLAF, EPPO, DNA, etc.).

### 🕵️ 3. Anonymous Q&A Tracking
- When submitting anonymously, the app generates a unique **Tracking Code** and **Secret Passcode** purely on the client side.
- Whistleblowers can use these credentials to check the investigation status or answer follow-up questions from authorities/journalists without ever revealing their identity.

### 🏛️ 4. Official Complaint Generator & NGO Hub
- **Legal Assistant:** Guides users to determine if an act is corruption or a mere administrative issue.
- **Auto-Generation:** Automatically drafts formal legal complaints (PDF/Text) ready to be filed with prosecutors.
- **Journalist Hub:** Directly connects whistleblowers with trusted investigative journalism NGOs (e.g., RISE Project, Funky Citizens, Transparency International) for unresolved cases.

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Cryptography:** `javax.crypto` (AES-256-GCM), SHA-256 Integrity Hashing.
- **Media Processing:** `androidx.exifinterface` for metadata stripping.
- **Architecture:** Pure JVM Bytecode. **No Native JNI (C/C++) code**, ensuring the entire codebase is fully transparent and auditable by security researchers. R8 Obfuscation enabled for release builds.

---

## 🤝 For NGOs & Investigative Journalists

Are you an anti-corruption NGO or investigative journalism network? **CivicEU is built for you.**
By directing whistleblowers to use CivicEU, you ensure that the tips and evidence you receive are sanitized, encrypted, and legally structured. 

We welcome partnerships! Feel free to audit our codebase, submit PRs, or contact us to add your organization to the **CivicEU Amplifier Hub**.

---

## 💻 How to Build

1. Clone the repository:
   ```bash
   git clone https://github.com/buburulz2007-hue/CivicEU.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run on any device with **Android 7.0 (API 24)** or higher.

---

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details. Transparency is our greatest weapon against corruption.
