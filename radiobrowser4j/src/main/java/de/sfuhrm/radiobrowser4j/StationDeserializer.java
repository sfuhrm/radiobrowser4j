package de.sfuhrm.radiobrowser4j;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/** Gson deserializer for {@linkplain Station}.
 * @see <a href="https://at1.api.radio-browser.info/">API documentation</a>
 * @author Stephan Fuhrmann
 * */
class StationDeserializer implements JsonDeserializer<Station> {

    /** The date format used in the JSON objects of radio browser. */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss");

    @Override
    public Station deserialize(
            final JsonElement jsonElement,
            final Type type,
            final JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {
        Station station = new Station();
        JsonObject jsonObject = jsonElement.getAsJsonObject();


        station.setLastcheckok(jsonObject.get("lastcheckok").getAsInt());
        try {
            transfer(jsonObject, "stationuuid", station, Station::setStationUUID, UUID.class);
            transfer(jsonObject, "changeuuid", station, Station::setChangeUUID, UUID.class);
            transfer(jsonObject, "url", station, Station::setUrl, String.class);
            transfer(jsonObject, "url_resolved", station, Station::setUrlResolved, String.class);
            transfer(jsonObject, "homepage", station, Station::setHomepage, String.class);
            transfer(jsonObject, "favicon", station, Station::setFavicon, String.class);
            transfer(jsonObject, "tags", station, Station::setTags, String.class);
            transfer(jsonObject, "countrycode", station, Station::setCountryCode, String.class);
            transfer(jsonObject, "state", station, Station::setState, String.class);
            transfer(jsonObject, "language", station, Station::setLanguage, String.class);
            transfer(jsonObject, "votes", station, Station::setVotes, Integer.class);
            transfer(jsonObject, "codec", station, Station::setCodec, String.class);
            transfer(jsonObject, "bitrate", station, Station::setBitrate, Integer.class);
            transfer(jsonObject, "hls", station, Station::setHls, String.class);
            transfer(jsonObject, "lastchecktime", station, Station::setLastchecktime, Date.class);
            transfer(jsonObject, "lastcheckoktime", station, Station::setLastcheckoktime, Date.class);
            transfer(jsonObject, "lastchangetime", station, Station::setLastchangetime, Date.class);
            transfer(jsonObject, "lastlocalchecktime", station, Station::setLastlocalchecktime, Date.class);
            transfer(jsonObject, "clicktimestamp", station, Station::setClicktimestamp, Date.class);
            transfer(jsonObject, "clickcount", station, Station::setClickcount, Integer.class);
            transfer(jsonObject, "clicktrend", station, Station::setClicktrend, Integer.class);
            transfer(jsonObject, "name", station, Station::setName, String.class);
            transfer(jsonObject, "geo_lat", station, Station::setGeoLatitude, Double.class);
            transfer(jsonObject, "geo_long", station, Station::setGeoLongitude, Double.class);
            transfer(jsonObject, "has_extended_info", station, Station::setHasExtendedInfo, Boolean.class);
        } catch (ParseException e) {
            throw new RadioBrowserException(e);
        }

        return station;
    }

    <T> void transfer(JsonObject jsonObject,
                      String key,
                      Station station,
                      BiConsumer<Station, T> setter,
                      Class<T> type) throws ParseException {
        JsonElement element = jsonObject.get(key);
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (type == String.class) {
            setter.accept(station, (T) element.getAsString());
            return;
        }
        if (type == Date.class) {
            if (element.getAsString().isEmpty()) {
                return;
            }
            setter.accept(station, (T) dateFormat.parse(element.getAsString()));
            return;
        }
        if (type == UUID.class) {
            if (element.getAsString().isEmpty()) {
                return;
            }
            setter.accept(station, (T) UUID.fromString(element.getAsString()));
            return;
        }
        if (type == Integer.class) {
            setter.accept(station, (T) (Integer)element.getAsInt());
            return;
        }
        if (type == Double.class) {
            setter.accept(station, (T) (Double)element.getAsDouble());
            return;
        }
        if (type == Boolean.class) {
            setter.accept(station, (T) (Boolean)element.getAsBoolean());
            return;
        }
        throw new IllegalArgumentException("Unsupported type: " + type + " for field " + key);
    }
}
