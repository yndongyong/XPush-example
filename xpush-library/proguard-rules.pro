# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
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
-dontshrink
-flattenpackagehierarchy
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#混淆前后的映射
-printmapping map.txt
#不跳过(混淆) jars中的 非public classes   默认选项
-dontskipnonpubliclibraryclassmembers
#忽略警告
-ignorewarnings
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#不预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions,InnerClasses
-dontwarn org.apache.**
-dontwarn android.support.**

#基础配置
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.view.View {*;}
-keep class android.support.v4.**{ *; }
-keep class android.support.v7.**{ *; }
-keep class android.webkit.**{*;}
-keep interface android.support.v4.app.** { *; }

#smack的反射类
-keep class org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration


-keep class *.R
-keepclasseswithmembers class **.R$* {    public static <fields>;}

#处理入口函数
-keep public class com.ynyx.epic.xpush.smack.XPushInterface{
    public static void *(android.content.Context);
    public static void *(***);
}
#处理实体类不被混淆
-keep public class com.ynyx.epic.xpush.vo.**{
    public void set*(***);
    public *** get*();
    public *** is*();
}
##因为提供给外界继承了
-keep class com.ynyx.epic.xpush.listener.** { *; }

-keep class org.jivesoftware.smack.packet.Message
##处理PushService中不想被混淆的代码
-keep class com.ynyx.epic.xpush.service.** { *; }
#-keep class com.ynyx.epic.xpush.service.PushService { 
#     protected abstract *** get*();
#     protected abstract *** *(***);
#     protected void *(***);
#     public void add*(com.ynyx.epic.xpush.listener.PushMessageListener,com.ynyx.epic.xpush.listener.PushMessageFilter);
#     public abstract *** *();
#}