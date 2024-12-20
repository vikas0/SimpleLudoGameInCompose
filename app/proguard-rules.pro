# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.gson.** { *; }

# Gson
-keep class com.google.gson.* { *; }
-keep class * {
    @com.google.gson.annotations.SerializedName *;
}

# Prevent obfuscation of model classes
-keepclassmembers class com.example.simplifiedludogame.model.** {
    *;
}

# General Android rules
-keepattributes Exceptions, Signature, InnerClasses
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.** { *; }
-keep class com.android.volley.** { *; }
