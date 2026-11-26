# Proguard rules for ElderShield
-keepclassmembers class * extends androidx.room.RoomDatabase {
    *;
}
-dontwarn com.google.mlkit.**
