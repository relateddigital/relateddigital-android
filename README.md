# relateddigital-android
relateddigital-android

#Dependency

 In your project-level build.gradle file:
 Add mavenCentral() to repositories e.g.
 buildscript {
    repositories {
        google()
        mavenCentral()
    }
	..
 }
	
 allprojects {
     repositories {
         google()
         mavenCentral()
     }
 }
 
 In your app-level build.gradle file:
 Add this the libe below to the dependencies
 implementation 'io.github.relateddigital:relateddigital-android:1.0.0'
 
#TODOs
  - Lock mechanism for in-app actions like for the in-app messages
