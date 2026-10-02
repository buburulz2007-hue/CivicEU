# R8 / ProGuard rules for Civic App (Pure Kotlin JVM Bytecode - No Native JNI)

# Keep all project models, data classes, repositories, and crypto utilities
-keep class com.civiceu.com.model.** { *; }
-keep class com.civiceu.com.data.** { *; }
-keep class com.civiceu.com.crypto.** { *; }
-keep class com.civiceu.com.util.** { *; }

# Keep Jetpack Compose navigation and UI state attributes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep cryptographic ciphers and key generators from being stripped or renamed by R8
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# Keep AndroidX ExifInterface
-keep class androidx.exifinterface.media.ExifInterface { *; }

# R8 Optimization and Obfuscation directives
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
