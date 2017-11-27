## RadioBrowser4j ![Travis CI Status](https://travis-ci.org/sfuhrm/radiobrowser4j.svg?branch=master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/radiobrowser4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sfuhrm/radiobrowser4j) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/def6bf015f664ed3b9cf19d93232f4bc)](https://www.codacy.com/app/sfuhrm/radiobrowser4j?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=sfuhrm/radiobrowser4j&amp;utm_campaign=Badge_Grade)

A Java API for the [RadioBrowser](http://www.radio-browser.info)
internet radio station online repository.

The library is useful for applications wanting to retrieve
internet radio station URLs, streams and meta information.

## How to use

First step is to include the dependency in your Maven or
Gradle project. 
For maven you need this dependency:

    <dependency>
        <groupId>de.sfuhrm</groupId>
        <artifactId>radiobrowser4j</artifactId>
        <version>1.0.0</version>
    </dependency>

After adding this dependency, you can start
by creating one instance and using it

    // 5000ms timeout, user agent is Demo agent/1.0
    RadioBrowser browser = new RadioBrowser(5000, "Demo agent/1.0");
    // retrieve the first 16 stations
    List<Station> stations = browser.listStations(Paging.at(0,16));

### How it is tested

The API is tested using the WireMock REST testing
framework. Mocked web requests/responses are
located in the test resources.

## License

Copyright 2017 Stephan Fuhrmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and