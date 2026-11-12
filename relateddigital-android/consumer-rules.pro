-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod

-keep class com.relateddigital.relateddigital_android.model.** {
    <fields>;
    <init>(...);
}
-keepclassmembers enum com.relateddigital.relateddigital_android.model.** { *; }
-keep class com.relateddigital.relateddigital_android.util.LogUtils$LogConfig {
    <fields>;
    <init>(...);
}

-keep class com.hianalytics.android.** { *; }
-keep class com.huawei.updatesdk.** { *; }
-keep class com.huawei.hms.** { *; }
-keep class com.huawei.agconnect.** { *; }
