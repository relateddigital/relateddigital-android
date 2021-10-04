# relateddigital-android
relateddigital-android

# Dependency

 - In your project-level build.gradle file, add mavenCentral() to repositories e.g.
 ```java
 buildscript {
    repositories {
        google()
        mavenCentral()
	...
    }
	...
 }
	
 allprojects {
     repositories {
         google()
         mavenCentral()
	 ...
     }
     ...
 }
 ```
 
 - In your app-level build.gradle file, add the line below to the dependencies
 ```java
 implementation 'io.github.relateddigital:relateddigital-android:1.0.0'
 ```
 
# TODOs
  - Lock mechanism for in-app actions like for the in-app messages
