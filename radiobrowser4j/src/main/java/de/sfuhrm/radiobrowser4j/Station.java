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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Representation of a Radio Station.
 * @author Stephan Fuhrmann
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {
    @Getter @Setter
    private String id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String ip;

    @Getter @Setter
    private String url;

    @Getter @Setter
    private String homepage;

    @Getter @Setter
    private String favicon;

    @Getter @Setter
    private String tags;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String state;

    @Getter @Setter
    private String language;

    @Getter @Setter
    private String votes;

    @Getter @Setter
    private String negativevotes;

    @Getter @Setter
    private String codec;

    @Getter @Setter
    private String bitrate;

    @Getter @Setter
    private String hls;

    @Getter @Setter
    private String lastcheckok;

    @Getter @Setter
    private String lastchecktime;

    @Getter @Setter
    private String lastcheckoktime;

    @Getter @Setter
    private String clicktimestamp;

    @Getter @Setter
    private String clickcount;

    @Getter @Setter
    private String clicktrend;

    @Getter @Setter
    private String lastchangetime;

    @Override
    public String toString() {
        return "Station{" + "name=" + name + ", url=" + url + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.id);
        hash = 31 * hash + Objects.hashCode(this.name);
        hash = 31 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Station other = (Station) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }



}
