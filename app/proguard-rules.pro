# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\a_heng\android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# author -heng
# 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-ignorewarnings
-verbose
-dontwarn

# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 混淆时应用侵入式重载
-overloadaggressively
# 确定统一的混淆类的成员名称来增加混淆
-useuniqueclassmembernames
# 重新包装所有重命名的包并放在给定的单一包中
#-flattenpackagehierarchy {package_name}
# 重新包装所有重命名的类文件中放在给定的单一包中
#-repackageclass {package_name}



#-libraryjars libs/android-async-http-1.4.6.jar
#-libraryjars libs/android-support-v4.jar
#-libraryjars libs/jsoup-1.8.3.jar
#-libraryjars libs/umeng-update-v2.6.0.1.jar
-keep class com.loopj.android.http.** { *; }
-keep class android.support.v4.** { *; }
-keep class org.jsoup.** { *; }
-keep class io.vov.utils.** { *; }
-keep class io.vov.vitamio.** { *; }

# umeng
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class com.umeng.**
-keep public class com.idea.fifaalarmclock.app.R$*{
    public static final int *;
}
-keep public class com.umeng.fb.ui.ThreadView {
}
-dontwarn com.umeng.**
-dontwarn org.apache.commons.**
-keep public class * extends com.umeng.**
-keep class com.umeng.** {*; }
-keep class com.alimama.mobile.** {*; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-dontwarn net.youmi.android.**
-keep class net.youmi.android.** {*;}

