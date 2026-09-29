-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepattributes *Annotation*,InnerClasses,EnclosingMethod

-keep class com.example.lostnfound.domain.model.** { *; }

-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
