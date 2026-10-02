# Aplicație pentru Sesizarea Corupției (Civic)

Această aplicație va permite utilizatorilor să raporteze cazuri de corupție, oferind o interfață simplă pentru descrierea incidentului, selectarea unei categorii și vizualizarea raportărilor existente.

## User Review Required

> [!IMPORTANT]
> Această implementare inițială va folosi stocare locală temporară (în memorie). Pentru o aplicație reală, va fi necesară integrarea cu un backend securizat (ex. Firebase sau un server privat) și implementarea unor măsuri de anonimizare/securitate.

## Proposed Changes

### Core Data & Logic

#### [NEW] [CorruptionReport.kt](file:///C:/Users/Shifter/AndroidStudioProjects/Civic/app/src/main/java/com/example/civic/model/CorruptionReport.kt)
Definirea modelului de date pentru o sesizare.

#### [NEW] [ReportRepository.kt](file:///C:/Users/Shifter/AndroidStudioProjects/Civic/app/src/main/java/com/example/civic/data/ReportRepository.kt)
Un repository simplu pentru gestionarea listei de sesizări.

---

### UI Components (Compose)

#### [NEW] [HomeScreen.kt](file:///C:/Users/Shifter/AndroidStudioProjects/Civic/app/src/main/java/com/example/civic/ui/screens/HomeScreen.kt)
Ecranul principal care afișează lista de sesizări și un buton (FAB) pentru a adăuga una nouă.

#### [NEW] [AddReportScreen.kt](file:///C:/Users/Shifter/AndroidStudioProjects/Civic/app/src/main/java/com/example/civic/ui/screens/AddReportScreen.kt)
Ecranul cu formularul de sesizare (Titlu, Descriere, Categorie).

---

### Navigation & Integration

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Shifter/AndroidStudioProjects/Civic/app/src/main/java/com/example/civic/MainActivity.kt)
Configurarea NavHost-ului pentru a permite navigarea între ecrane.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Shifter/AndroidStudioProjects/Civic/app/build.gradle.kts)
Adăugarea dependențelor pentru Navigation Compose.

## Verification Plan

### Manual Verification
1. Pornirea aplicației pe emulator.
2. Verificarea afișării listei (inițial goală sau cu date mock).
3. Navigarea către ecranul "Adaugă Sesizare".
4. Completarea formularului și salvarea acestuia.
5. Verificarea apariției noii sesizări în lista de pe ecranul principal.
