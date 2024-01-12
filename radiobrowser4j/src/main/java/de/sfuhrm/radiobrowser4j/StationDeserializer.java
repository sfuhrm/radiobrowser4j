package de.sfuhrm.radiobrowser4j;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

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
        station.setChangeUUID(
                UUID.fromString(jsonObject.get("changeuuid").getAsString()));
        station.setStationUUID(
                UUID.fromString(jsonObject.get("stationuuid").getAsString()));
        station.setUrl(
                jsonObject.get("url").getAsString());
        station.setUrlResolved(
                jsonObject.get("url_resolved").getAsString());
        station.setHomepage(
                jsonObject.get("homepage").getAsString());
        station.setFavicon(
                jsonObject.get("favicon").getAsString());
        station.setTags(
                jsonObject.get("tags").getAsString());
        station.setCountry(
                jsonObject.get("country").getAsString());
        station.setCountryCode(
                jsonObject.get("countrycode").getAsString());
        station.setState(
                jsonObject.get("state").getAsString());

        station.setLanguage(
                jsonObject.get("language").getAsString());
        station.setVotes(
                jsonObject.get("votes").getAsInt());
        station.setCodec(
                jsonObject.get("codec").getAsString());
        station.setBitrate(
                jsonObject.get("bitrate").getAsInt());
        station.setHls(
                jsonObject.get("hls").getAsString());

        station.setLastcheckok(jsonObject.get("lastcheckok").getAsInt());
        try {
            station.setLastchecktime(
                    dateFormat.parse(
                            jsonObject.get("lastchecktime").getAsString()));
            station.setLastcheckoktime(
                    dateFormat.parse(
                            jsonObject.get("lastcheckoktime").getAsString()));
            station.setLastchangetime(
                    dateFormat.parse(
                            jsonObject.get("lastchangetime").getAsString()));
            station.setLastlocalchecktime(
                    dateFormat.parse(
                            jsonObject.get("lastlocalchecktime")
                                    .getAsString()));
            station.setClicktimestamp(
                    dateFormat.parse(
                            jsonObject.get("clicktimestamp").getAsString()));
        } catch (ParseException e) {
            throw new RadioBrowserException(e);
        }

        station.setClickcount(jsonObject.get("clickcount").getAsInt());
        station.setClicktrend(jsonObject.get("clicktrend").getAsInt());
        station.setName(jsonObject.get("stationname").getAsString());

        station.setGeoLatitude(jsonObject.get("geo_lat").getAsDouble());
        station.setGeoLongitude(jsonObject.get("geo_long").getAsDouble());

        station.setHasExtendedInfo(jsonObject.get("has_extended_info")
                .getAsBoolean());

        return station;
    }
}
