RadioBrowser4j 
===================
[![Java CI](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-ref.yml/badge.svg)](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-ref.yml)
[![Java Integration](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-integration.yml/badge.svg)](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-integration.yml)
[![Coverage](https://raw.githubusercontent.com/sfuhrm/radiobrowser4j/gh-pages/jacoco.svg)]() 
![Maven Central](https://img.shields.io/maven-central/v/de.sfuhrm/radiobrowser4j)
[![ReleaseDate](https://img.shields.io/github/release-date/sfuhrm/radiobrowser4j)](https://github.com/sfuhrm/radiobrowser4j/releases)
[![javadoc](https://javadoc.io/badge2/de.sfuhrm/radiobrowser4j/javadoc.svg)](https://javadoc.io/doc/de.sfuhrm/radiobrowser4j)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 

A [Java API](https://javadoc.io/doc/de.sfuhrm/radiobrowser4j/latest/de/sfuhrm/radiobrowser4j/package-summary.html) for
the [RadioBrowser](http://www.radio-browser.info)
internet radio station online repository.

The library is useful for applications wanting to retrieve
internet radio station URLs, streams and meta information.

## How to use

A detailed introduction on how to use the library can be found
in [Radiobrowser4j - A Java API for Internet radio stations](https://sfuhrm.de/open-source-java/radiobrowser4j-a-java-api-for-internet-radio-stations/).

### Maven for JDK targets

First step is to include the dependency in your Maven or
Gradle project.
For Maven, you need this dependency:

```xml
<dependency>
    <groupId>de.sfuhrm</groupId>
    <artifactId>radiobrowser4j</artifactId>
    <version>3.4.0</version>
</dependency>
```

### Use within Android projects

The library works well with Android projects simply by referencing the library within the build.gradle

```gradle
    implementation 'de.sfuhrm:radiobrowser4j:3.4.0'
```

If Proguard or R8 code shrinking and obfuscation is being used, then the following entries should be added to the Proguard configuration file (usually proguard-rules.pro):

```
-dontwarn lombok**
-keep class de.sfuhrm.radiobrowser4j.** { *; }
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
```
### Java use

After adding the dependency, you can start
by creating one instance and using it

```java
// discover endpoint
String myAgent = "www.superradioplayer.com";
Optional<String> endpoint = new EndpointDiscovery(myAgent).discover();

// build instance
RadioBrowser radioBrowser = new RadioBrowser(
    ConnectionParams.builder().apiUrl(endpoint.get()).userAgent(myAgent).timeout(5000).build());

// list stations
radioBrowser.listStations(ListParameter.create().order(FieldName.NAME))
    .limit(64)
    .forEach(s -> System.out.printf("%s: %s%n",
        s.getName(),
        s.getUrl()
));
```

You can take a look at the [javadoc](https://javadoc.io/doc/de.sfuhrm/radiobrowser4j)
documentation to get the full concepts of the API.

### More examples

The library was extracted from an internet radio player and recorder program
in Java, [radiorecorder](https://github.com/sfuhrm/radiorecorder).
You can take a look at real-life usage patterns
[there](https://github.com/sfuhrm/radiorecorder/blob/master/radiorecorder/src/main/java/de/sfuhrm/radiorecorder/Main.java).

### How it is tested

The API is tested using the [WireMock](http://wiremock.org/) REST testing
framework. Mocked web requests/responses are
located in the test resources.

## License

Copyright 2017-2026 Stephan Fuhrmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
