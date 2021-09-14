RadioBrowser4j 
===================
[![Java CI](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven.yml/badge.svg)](https://github.com/sfuhrm/radiobrowser4j/actions/workflows/maven.yml)
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

### JDK

First step is to include the dependency in your Maven or
Gradle project.
For maven you need this dependency:

```xml
<dependency>
    <groupId>de.sfuhrm</groupId>
    <artifactId>radiobrowser4j</artifactId>
    <version>2.1.0</version>
</dependency>
```

After adding this dependency, you can start
by creating one instance and using it

```java
// 5000ms timeout, user agent is Demo agent/1.0
RadioBrowser browser = new RadioBrowser(5000, "Demo agent/1.0");
// print the first 64 stations in station name order
browser.listStations(ListParameter.create().order(FieldName.name))
    .limit(64)
    .forEach(s -> System.out.printf("%s: %s%n",
        s.getName(),
        s.getUrl()
));
```

You can take a look at the [javadoc](https://javadoc.io/doc/de.sfuhrm/radiobrowser4j)
documentation to get the full concepts of the API.

### Android

Some adjustments are necessary to the packaging of your app build.gradle:

```
...

android {
...
    packagingOptions {
        exclude 'META-INF/NOTICE.md'
        exclude 'META-INF/LICENSE.md'
        exclude 'META-INF/NOTICE.markdown'
    }
}

dependencies {
...

    implementation 'de.sfuhrm:radiobrowser4j:2.1.0';
}
```

### More examples

The library was extracted from a internet radio player and recorder program
in Java, [radiorecorder](https://github.com/sfuhrm/radiorecorder).
You can take a look at real-life usage patterns
[there](https://github.com/sfuhrm/radiorecorder/blob/master/src/main/java/de/sfuhrm/radiorecorder/Main.java).

### How it is tested

The API is tested using the [WireMock](http://wiremock.org/) REST testing
framework. Mocked web requests/responses are
located in the test resources.


## Version history

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

Copyright 2017-2020 Stephan Fuhrmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
