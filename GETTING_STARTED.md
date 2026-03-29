# Getting Started with RadioBrowser4j

This `GETTING_STARTED.md` provides a quick introduction to the RadioBrowser4j API and shows the steps for both simple and complex requests.

## 1. Add dependency

### Maven

```xml
<dependency>
  <groupId>de.sfuhrm</groupId>
  <artifactId>radiobrowser4j</artifactId>
  <version>3.2.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'de.sfuhrm:radiobrowser4j:3.2.0'
```

## 2. Discover an endpoint

The official RadioBrowser API uses DNS round-robin endpoints but the library supports runtime discovery.

```java
String userAgent = "my-app/1.0";
Optional<String> endpoint = new EndpointDiscovery(userAgent).discover();
if (endpoint.isEmpty()) {
    throw new IllegalStateException("No radio-browser endpoint found");
}

RadioBrowser radioBrowser = new RadioBrowser(
    ConnectionParams.builder()
        .apiUrl(endpoint.get())
        .userAgent(userAgent)
        .timeout(5000)
        .build());
```

## 3. Simple request: list stations (basic filtering)

```java
radioBrowser.listStations(ListParameter.create().order(FieldName.NAME))
    .limit(64)
    .forEach(station -> System.out.printf("%s: %s\n", station.getName(), station.getUrl()));
```

- `listStations(...)` returns a `Stream<Station>`.
- `ListParameter.create()` is the builder for query parameters.
- `FieldName` includes supported sorting keys like `NAME`, `COUNTRY`, etc.

## 4. Simple request: top stations by country

```java
radioBrowser.listStations(ListParameter.create()
    .countryCode("US")
    .order(FieldName.BITRATE))
    .limit(20)
    .forEach(station -> System.out.printf("%s (%skbps)\n", station.getName(), station.getBitrate()));
```

## 5. Complex request: advanced search

Use advanced search when you need full-text and multi-field matching power.

```java
AdvancedSearch query = AdvancedSearch.create()
    .withName("jazz")
    .withCountryCode("US")
    .withBitRateMin(128)
    .withOrder(FieldName.NAME);

List<Station> result = radioBrowser.listStationsWithAdvancedSearch(query);
System.out.println("Found stations: " + result.size());
```

## 6. Complex request: paging through result stream

```java
radioBrowser.listStations(ListParameter.create()
    .country("Germany")
    .order(FieldName.POPULARITY))
    .limit(500)
    .forEach(station -> {
        // process station
    });
```

Use `.limit(...)` and `.skip(...)` on `Stream` or `ListParameter` for paging.

## 7. Extra operations

- `listCountries()` / `listCountryCodes()`
- `listCodecs()`
- `listLanguages()`
- `getServerStats()`

Examples in Javadoc: https://javadoc.io/doc/de.sfuhrm/radiobrowser4j/latest/de/sfuhrm/radiobrowser4j/package-summary.html

## 8. Error handling

API exceptions are thrown as `RadioBrowserException`:

```java
try {
    // request
} catch (RadioBrowserException e) {
    // handle network/format errors
}
```

## 9. Android notes

- Works on Android (Java 8+)
- Add ProGuard rules if code shrinking is enabled:

```
-dontwarn lombok**
-keep class de.sfuhrm.radiobrowser4j.** { *; }
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
```

---

## Further reading
- [`README.md`](README.md)
- Javadoc link above
- Existing tests in `src/test/java` for real request patterns
