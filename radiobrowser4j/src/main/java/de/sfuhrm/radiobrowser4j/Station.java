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
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of a Radio Station.
 * @author Stephan Fuhrmann
 */
@EqualsAndHashCode(of = {"id", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Station {
    /** The station id. The ID is usually numeric. */
    @Getter @Setter
    private String id;

    /** The name of the station. */
    @Getter @Setter
    private String name;

    /** The URL of the stream. */
    @Getter @Setter
    private String url;

    /** The URL of the stations homepage. */
    @Getter @Setter
    private String homepage;

    /** The URL of the stations favicon. */
    @Getter @Setter
    private String favicon;

    /** The tags for this station. */
    @Getter @Setter @JsonIgnore
    private List<String> tags;

    /** The country this station is located at. */
    @Getter @Setter
    private String country;

    /** The state this station is located at. */
    @Getter @Setter
    private String state;

    /** The language of this station. */
    @Getter @Setter
    private String language;

    /** The votes for this station. */
    @Getter @Setter
    private String votes;

    /** The negative votes for this station. */
    @Getter @Setter
    private String negativevotes;

    /** The codec used for the stream. */
    @Getter @Setter
    private String codec;

    /** The bitrate used for the stream. */
    @Getter @Setter
    private String bitrate;

    /** Dunno. */
    @Getter @Setter
    private String hls;

    /** Was the last stream check ok? */
    @Getter @Setter
    private int lastcheckok;

    /** When was the last stream check? */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastchecktime;

    /** When was the last stream check ok? */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastcheckoktime;

    /** When was the last stream click? */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date clicktimestamp;

    /** Number of clicks. */
    @Getter @Setter
    private String clickcount;

    /** Click trend. */
    @Getter @Setter
    private String clicktrend;

    /** Timestamp of the last change of the stations data. */
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss")
    private Date lastchangetime;

    /** JSON getter for the {@link #tags}.
     * You'll probably prefer the {@link #tags} property.
     * @return comma separated tag names.
     * @see #setTagsCommaSeparated(String)
     * */
    @JsonGetter("tags")
    public String getTagsCommaSeparated() {
        return tags.stream().collect(Collectors.joining(","));
    }

    /** JSON setter for the {@link #tags}.
     * You'll probably prefer the {@link #tags} property.
     * @param commaTags comma separated tag names.
     * @see #getTagsCommaSeparated()
     * */
    @JsonSetter("tags")
    public void setTagsCommaSeparated(String commaTags) {
        tags = Arrays.asList(commaTags.split(","));
    }

    @Override
    public String toString() {
        return "Station{" + "name=" + name + ", url=" + url + '}';
    }
}
