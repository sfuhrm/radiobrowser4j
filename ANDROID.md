# Creating apps on Android

There needs to be some tweaking to make it run on Android. I am listing them in the following sections.

## Apps build.gradle

Packaging exclusions:

```
android.packagingOptions.resources.excludes += "**/*.md"
android.packagingOptions.resources.excludes += "**/*.markdown"
```

Dependencies:

```
    implementation 'de.sfuhrm:radiobrowser4j:2.2.5';
    implementation 'org.osgi:org.osgi.framework:1.10.0';
```

## Calling of Radiobrowser4j

Before calling radiobrowser4j you need to set a System property for Android to find the proper jersey client builder:

```java
System.setProperty("jakarta.ws.rs.client.ClientBuilder", "org.glassfish.jersey.client.JerseyClientBuilder");
RadioBrowser browser = new RadioBrowser(5000, "Demo agent/1.0");
// print the first 64 stations in station name order
String str = browser.listStations(ListParameter.create().order(FieldName.NAME))
        .limit(64)
        .map(s -> s.getName() + " / " + s.getUrl()
        ).collect(Collectors.toList()).toString();
System.out.println("after radiobrowser4j");
```

## proguard

The Proguard adjustments from one of the previous versions were:

```
-keep class org.glassfish.hk2.utilities.** { *; } -keep class org.glassfish.jersey.** { *; } -keep class org.jvnet.hk2.internal.** { *; } -keep class de.sfuhrm.radiobrowser4j.** { *; }
```

## Demo

An Android app demo printing radio stations to a full-screen-view is given in the following TAR ball:

[radiobrowser4j-android-demo.tar.gz](https://github.com/sfuhrm/radiobrowser4j/files/11995789/radiobrowser4j-android-demo.tar.gz)
