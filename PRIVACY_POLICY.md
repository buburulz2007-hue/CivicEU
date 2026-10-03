# Privacy Policy for CivicEU

**Last Updated:** October 3, 2026

Welcome to CivicEU, an open-source Civic Tech application dedicated to empowering citizens across Europe to safely report corruption and institutional malpractice. Your privacy and digital security are our absolute priorities.

This Privacy Policy explains how CivicEU processes data when you use our mobile application and our infrastructure.

## 1. Data Controller and Open Source Transparency

CivicEU is operated as a transparent, open-source project. Our full source code is publicly auditable at [github.com](https://github.com/buburulz2007-hue/CivicEU). Because we enforce a strict Zero-Knowledge architecture, we do not act as a traditional data aggregator.

## 2. Types of Data We Process (Anonymous vs. Non-Anonymous)

Depending on your choices, CivicEU handles information in two fundamentally different ways:

### A. Anonymous Reporting (Default Setup)
If you choose to submit a report anonymously:
* **No Personal Identifiers:** We do not collect your name, email address, phone number, national ID, or device UUID.
* **Zero Logs Policy:** Our secure backend infrastructure automatically strips your IP address from incoming network requests before processing the text payload.
* **Automated Metadata Sanitization:** Any attachments (images, PDFs, audio) uploaded to the report are automatically stripped of embedded tracking metadata (such as EXIF or GPS coordinates) before being routed.
* **Tracking Tokens:** To check the status of an anonymous report, the app generates a localized token on your device. This token remains on your client-side storage and is not tied to any identity.

### B. Non-Anonymous Reporting (User Consent Required)
If you explicitly opt to reveal your identity to the receiving authority, the application will process:
* **Identification Data:** Your Name, Surname, Email Address, and Contact Details.
* **Consent:** This data is only transmitted after you explicitly check the consent box, allowing CivicEU to pass this information directly to the selected anti-corruption agency.

## 3. Data Transmission and Sovereignty (Where Data Goes)

* **Direct Routing:** CivicEU functions as a secure digital bridge. Based on your geographical selection or device location, reports are packaged and securely dispatched via encrypted protocols (HTTPS/TLS) straight to the official intake channels of designated national anti-corruption authorities (e.g., ANI/DNA in Romania, PNF in France, EPPO at the EU level).
* **EU Hosting Exclusively:** Any temporary processing or aggregation required for our public statistics platform takes place strictly on secure cloud servers located within the European Union, ensuring full compliance with GDPR data sovereignty laws.

## 4. Public Statistics ("Public Dashboard")

To drive public accountability, CivicEU aggregates completely anonymized, high-level structural data to feed a public dashboard (e.g., number of reports sent per country, institutional response metrics).
* **No PII Stored:** No specific details, case texts, or identifying data are ever written to public databases or exposed in our analytics.

## 5. Device Permissions Used by the App

To protect your operational security, CivicEU requests only the absolute minimum permissions needed to function:
* **Storage/Media Access:** Required solely to let you pick and encrypt documents or photos you wish to attach as evidence.
* **Location Services (Optional):** Used strictly on the client side to suggest the appropriate national anti-corruption agency for your country. Your precise coordinates are never transmitted to our backend.
* **FLAG_SECURE (Anti-Screen Capture):** The application programmatically blocks screenshots and screen recordings within sensitive reporting areas to prevent malware or unauthorized visual tracking.

## 6. Your Rights Under GDPR

If you file a non-anonymous report, you retain all rights under the General Data Protection Regulation (GDPR), including the right to access, rectify, or request erasure of your data. To exercise these rights regarding the transmission phase, contact us at the details below. For data already received by state prosecutors, rights must be exercised directly with the respective state institution.

## 7. Contact Us

For any legal inquiries, cryptographic audits, or data protection questions, please open an issue on our GitHub Repository or contact the project maintainers.
