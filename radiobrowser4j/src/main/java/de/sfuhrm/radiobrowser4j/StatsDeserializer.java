package de.sfuhrm.radiobrowser4j;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/** Gson deserializer for {@linkplain Stats}.
 * @see <a href="https://at1.api.radio-browser.info/">API documentation</a>
 * @author Stephan Fuhrmann
 * */
class StatsDeserializer implements JsonDeserializer<Stats> {
    @Override
    public Stats deserialize(
            final JsonElement jsonElement,
            final Type type,
            final JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        Stats stats = new Stats();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        stats.setSupportedVersion(jsonObject.get("supported_version").getAsInt());
        stats.setSoftwareVersion(jsonObject.get("software_version").getAsString());
        stats.setStatus(jsonObject.get("status").getAsString());
        stats.setStations(jsonObject.get("stations").getAsInt());
        stats.setStationsBroken(jsonObject.get("stations_broken").getAsInt());
        stats.setTags(jsonObject.get("tags").getAsInt());
        stats.setClicksLastHour(jsonObject.get("clicks_last_hour").getAsInt());
        stats.setClicksLastDay(jsonObject.get("clicks_last_day").getAsInt());
        stats.setLanguages(jsonObject.get("languages").getAsInt());
        stats.setCountries(jsonObject.get("countries").getAsInt());
        return stats;
    }
}
