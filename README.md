RadioBrowser4j 
===================
[![Java CI](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-ref.yml/badge.svg)](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-ref.yml)
[![Java Integration](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-integration.yml/badge.svg)](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven-integration.yml)
[![Coverage](https://raw.githubusercontent.com/sfuhrm/radiobrowser4j/gh-pages/jacoco.svg)]() 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/radiobrowser4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/radiobrowser4j)
[![ReleaseDate](https://img.shields.io/github/release-date/sfuhrm/radiobrowser4j)](https://github.com/sfuhrm/radiobrowser4j/releases)
[![javadoc](https://javadoc.io/badge2/de.sfuhrm/radiobrowser4j/javadoc.svg)](https://javadoc.io/doc/de.sfuhrm/radiobrowser4j)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 

A [Java API](https://javadoc.io/doc/de.sfuhrm/radiobrowser4j/latest/de/sfuhrm/radiobrowser4j/package-summary.html) for
the [RadioBrowser](http://www.radio-browser.info)
internet radio station online repository.

The library is useful for applications wanting to retrieve
internet radio station URLs, streams and meta information.

## How to use

### Maven for JDK targets

First step is to include the dependency in your Maven or
Gradle project.
For Maven, you need this dependency:

```xml
<dependency>
    <groupId>de.sfuhrm</groupId>
    <artifactId>radiobrowser4j</artifactId>
    <version>3.2.0</version>
</dependency>
```

### Use within Android projects

The library works well with Android projects simply by referencing the library within the build.gradle

```gradle
    implementation 'de.sfuhrm:radiobrowser4j:3.2.0'
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

## Version history

* v3.1.0
  - Improvement: Provide `listCountryCodes()` call as a symmetric replacement for now deprecated `listCountries()`. #123 
  - Deprecation: Deprecated `listCountries()` since the HTTP API has deprecated all country fields in favor of ISO 3166-1 country codes. #123 
  - Improvement: Provide new `List<>` returning call for advanced search. #126 
  - Bugfix: Use view boundary on `Stream<>` returning calls. The stream will page through the view. #126 
  - Minor library updates (slf4j, hamcrest, lombok, wiremock, etc).
  - Minor Maven plugin updates.
* v3.0.3
  - Minor updates of dependencies and plugins
* v3.0.1
  - Minor updates of dependencies and plugins
* v3.0.0
  - Replacement of JAX-RS / Jersey with more lightweight URLConnection / GSon.
    Will make Android usage work out of the box.
  - Removed deprecations (Station country, deprecated constructors).
  - Introduced new ConnectionParams object.
  - Introduced retry mechnism (see ConnectionParams).
* v2.6.0
  - Refactorings for reducing code duplication.
  - Major refactorings to limit the JAX-RS / Jersey exposure of the code to a minimum of classes. This is a preparation for possible alternative implementations (see issue #14 ).
  - Freed the Station class from Jackson annotations (replacing with a mixin class).
  - Migrate from JMockit to Mockito since JMockit is conflicting in Java 21.
  - Building with JDK 21 now (still targeting JDK 8) since Mockito is needing this.
  - Migrate JUnit 4 to JUnit 5. Welcome to 2016.
* v2.5.3
  - Update slf4j dependency
  - Update log4j2 dependency
  - Update multiple build plugins
* v2.5.2
  - Add deprecation warnings for countrycode and countrycodeexact fields as suggested in the upstream API. Countrycode should be used instead which is standardized.
  - Update of some plugins.
* v2.5.1
  - Removed now unnecessary JAXB dependency (was needed by Jersey before)
  - Updated Jersey dependency from 3.0.8 -> 3.0.11
  - Changed resource closing to try-with-resources, reducing code 
* v2.5.0
  - Added new call listStationsWithAdvancedSearch for creating more complex queries to the API.
  - Added Station geoLatitude and geoLongitude.
  - Added the getServerStats call.
  - Fix: Mapping of now necessary CountryCode was broken.
  - Fix: Deprecation of the RadioBrowser.DEFAULT_API_URL and the related constructor. The cluster concept of radiobrowser is to rely on DNS roundrobin and probing. The German RadioBrowser.DEFAULT_API_URL returned HTTP 503 today which sucks.
  - Fix: "Improvable stations" are gone in the REST API, they are now deprecated and return empty lists/streams.
  - Refactored code to use a common ParameterProvider for mapping parameters to Maps.
  - Re-created offline wiremock tests
  - Integration test or real-world tests with real radiobrowser API
* v2.4.0
  - Default to GZIP encoding if supported by the server (#27).
* v2.3.1:
  - Bugfix when dealing with duplicate data  (fixes #15)
  - Add special info for  Android setup (obsolete)
* v2.2.5:
  - Update slf4j dependency to 2.0.7.
  - Update lombok dependency to 1.8.26.
  - Update log4j dependency to 2.20.0.
* v2.2.4:
  - Introduce RestClientFactory to deal at one place with Proxy config.
  - Update Jersey dependency to 3.0.8.
  - Update lombok dependency to 1.8.24.
  - Update log4j dependency to 2.19.0.
  - Update slf4j dependency to 2.0.3.
* v2.2.3:
  - Add proxy support for EndpointDiscovery also.
  - Update Jersey dependency to 3.0.4.
  - Fix multiple typos, rework some Javadoc.
* v2.2.2:
  - Support for HTTP proxy usage.
* v2.2.1:
  - Update log4j2 test dependency.
* v2.2.0:
  - Update multiple dependencies.
* v2.1.0:
  - Switch Jersey JAX-RS 2.3.3 -> 3.0.1.
  - Updated lombok/junit/log4j2 dependencies.
* v2.0.5:
  - Android support!
* v2.0.4:
  - Updated dependencies.
* v2.0.3:
  - Updated dependencies.
* v2.0.2:
  - Minor code improvements.
  - Updated dependencies.
* v2.0.0:
  - Support new radiobrowser API.
  - Clients will have to do minor changes to calling the library.
* v1.3.0:
  - Use new API DNS names.
  - Update maven plugins / dependencies.
* v1.2.1:
  - Made building with JDK 11 possible.
  - JDK 10/11 building in TravisCI.
  - Updated dependencies.
* v1.2.0:
  - Minor code improvements.
  - Updated many dependencies, for example Jersey JAX-RS.
* v1.1.0:
  - Added stream API calls.
  - Added tags list version to Station.
  - ListParameter for list order control.
  - editStation call in RadioBrowser.
* v1.0.0: First maven release.

## License

Copyright 2017-2024 Stephan Fuhrmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
