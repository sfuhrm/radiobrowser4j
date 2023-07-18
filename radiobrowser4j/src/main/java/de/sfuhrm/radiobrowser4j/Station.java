/*
* Copyright 2017 Stephan Fuhrmann
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.sfuhrm.radiobrowser4j;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Representation of a Radio Station.
 * @author Stephan Fuhrmann
 */
@EqualsAndHashCode(of = {"stationUUID", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Station extends ParameterProvider {
    /** A globally unique identifier for the change
     * of the station information. */
    @Getter @Setter
    @JsonProperty("changeuuid")
    private UUID changeUUID;

    /** A globally unique identifier for the station. */
    @Getter @Setter
    @JsonProperty("stationuuid")
    private UUID stationUUID;

    /** The name of the station. */
    @Getter @Setter
    private String name;

    /** The URL of the stream. */
    @Getter @Setter
    private String url;

    /** An automatically "resolved" stream URL. */
    @Getter @Setter
    @JsonProperty("url_resolved")
    private String urlResolved;

    /** The URL of the stations' homepage. */
    @Getter @Setter
    private String homepage;

    /** The URL of the stations' favicon. */
    @Getter @Setter
    private String favicon;

    /** The tags for this station as a list.
     * The comma separated version can be obtained using
     * {@link #getTags()}.
     * @see #getTags()
     * */
    @Getter @Setter @JsonIgnore
    private List<String> tagList = new ArrayList<>();

    /** The country this station is located at.
     * @deprecated Do NOT use the "country" fields anymore!
     * Use "countrycode" instead, which is standardized.
     * @see #countryCode
     * @see <a href="https://api.radio-browser.info/">api.radio-browser.info</a>
     * */
    @Deprecated
    @Getter @Setter
    private String country;

    /** Official country codes according to ISO 3166-1 alpha-2. */
    @Getter @Setter
    @JsonProperty("countrycode")
    private String countryCode;

    /** The state this station is located at. */
    @Getter @Setter
    private String state;

    /** The languages of this station as a list.
     * The comma separated version can be obtained using
     * {@link #getLanguage()}.
     * @see #getLanguage()
     */
    @Getter @Setter @JsonIgnore
    private List<String> languageList = new ArrayList<>();

    /** The votes for this station. */
    @Getter @Setter
    private Integer votes;

    /** The codec used for the stream. */
    @Getter @Setter
    private String codec;

    /** The bitrate used for the stream. */
    @Getter @Setter
    private Integer bitrate;

    /** Mark if this stream is using HLS distribution or non-HLS. */
    @Getter @Setter
    private String hls;

    /** The current online/offline state of this stream. */
    @Getter @Setter
    private Integer lastcheckok;

    /** The last time when any Radio-Browser server checked
     * the online state of this stream. */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastchecktime;

    /** The last time when the stream was checked for
     * the online status with a positive result. */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastcheckoktime;

    /** The last time when this server checked the online state
     * and the metadata of this stream. */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastlocalchecktime;

    /** The time of the last click recorded for this stream. */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date clicktimestamp;

    /** Clicks within the last 24 hours. */
    @Getter @Setter
    private Integer clickcount;

    /** The difference of the click counts within the last 2 days. */
    @Getter @Setter
    private Integer clicktrend;

    /** Timestamp of the last change of the stations' data. */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastchangetime;

    /** Geo latitude of the station. */
    @Getter
    @Setter
    @JsonProperty("geo_lat")
    private Double geoLatitude;

    /** Geo longitude of the station. */
    @Getter
    @Setter
    @JsonProperty("geo_long")
    private Double geoLongitude;

    /** Is true, if the stream owner does
     * provide extended information as HTTP headers
     * which override the information in the database. */
    @Getter
    @Setter
    @JsonProperty("has_extended_info")
    private Boolean hasExtendedInfo;

    /** JSON getter for the {@link #tagList}.
     * You would probably prefer using the {@link #tagList} property.
     * @return comma separated tag names.
     * @see #setTags(String)
     * */
    @JsonGetter("tags")
    public String getTags() {
        return String.join(",", tagList);
    }

    /** JSON setter for the {@link #tagList}.
     * You would probably prefer using the {@link #tagList} property.
     * @param commaTags comma separated tag names.
     * @see #getTags()
     * */
    @JsonSetter("tags")
    public void setTags(final String commaTags) {
        tagList = Arrays.asList(commaTags.split(","));
    }

    /** JSON setter for {@link #languageList}.
     * You would probably prefer using the {@link #languageList} property.
     * @return comma separated languages.
     * @see #setLanguage(String)
     * */
    @JsonGetter("language")
    public String getLanguage() {
        return String.join(",", languageList);
    }

    /** JSON setter for {@link #languageList}.
     * You would probably prefer using the {@link #languageList} property.
     * @param commaLanguages comma separated languages.
     * @see #getLanguage()
     * */
    @JsonSetter("language")
    public void setLanguage(final String commaLanguages) {
        languageList = Arrays.asList(commaLanguages.split(","));
    }

    @Override
    public String toString() {
        return "Station{" + "name=" + name + ", url=" + url + '}';
    }

    @Override
    protected void apply(final MultivaluedMap<String, String> requestParams) {
        requestParams.putSingle("name",
                getName());
        requestParams.putSingle("url",
                getUrl());
        if (getHomepage() != null) {
            requestParams.putSingle("homepage",
                    getHomepage());
        }
        if (getFavicon() != null) {
            requestParams.putSingle("favicon",
                    getFavicon());
        }
        if (getCountryCode() != null) {
            requestParams.putSingle("countrycode",
                    getCountryCode());
        }
        if (getState() != null) {
            requestParams.putSingle("state",
                    getState());
        }
        if (getLanguage() != null) {
            requestParams.putSingle("language",
                    getLanguage());
        }
        if (getTagList() != null) {
            requestParams.putSingle("tagList", getTags());
        }
        if (getGeoLatitude() != null) {
            requestParams.putSingle("geo_lat", getGeoLatitude().toString());
        }
        if (getGeoLongitude() != null) {
            requestParams.putSingle("geo_long", getGeoLongitude().toString());
        }
    }
}
