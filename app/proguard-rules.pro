-keepattributes Signature
-keep class kotlin.coroutines.Continuation
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn android.adservices.AdServicesState
-dontwarn android.adservices.measurement.MeasurementManager
-dontwarn android.adservices.topics.GetTopicsRequest$Builder
-dontwarn android.adservices.topics.GetTopicsRequest
-dontwarn android.adservices.topics.GetTopicsResponse
-dontwarn android.adservices.topics.Topic
-dontwarn android.adservices.topics.TopicsManager
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
#-renamesourcefileattribute SourceFile,