package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.core.MultivaluedMap;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

/** Holder for an advanced search query.
* A builder can be created by calling
* {@code AdvancedSearch.builder()},
* and then when you are finished
* {@code AdvancedSearch.AdvancedSearchBuilder.build()}.
* @author Stephan Fuhrmann
 * */
@Builder
public final class AdvancedSearch extends ParameterProvider {
    /** Name of the station. */
    private String name;

    /** True: only exact matches, otherwise all matches. */
    private Boolean nameExact;

    /** Country of the station.
     * @deprecated Do NOT use the "country" fields anymore!
     * Use "countrycode" instead, which is standardized.
     * @see #countryCode
     * @see <a href="https://api.radio-browser.info/">api.radio-browser.info</a>
     * */
    @Deprecated
    private String country;

    /** True: only exact matches, otherwise all matches.
     * @deprecated Do NOT use the "country" fields anymore!
     * Use "countrycode" instead, which is standardized.
     * @see #countryCode
     * @see <a href="https://api.radio-browser.info/">api.radio-browser.info</a>
     * */
    @Deprecated
    private Boolean countryExact;

    /** 2-digit countrycode of the station (see ISO 3166-1 alpha-2). */
    private String countryCode;

    /** State of the station. */
    private String state;

    /** True: only exact matches, otherwise all matches. */
    private Boolean stateExact;

    /** Language of the station. */
    private String language;

    /** True: only exact matches, otherwise all matches. */
    private Boolean languageExact;

    /** A tag of the station. */
    private String tag;

    /** True: only exact matches, otherwise all matches. */
    private Boolean tagExact;

    /** A list of tag. All tags in list have to match. */
    private List<String> tagList;

    /** Codec of the station. */
    private String codec;

    /** Minimum of kbps for bitrate field of stations in result. */
    private Integer bitrateMin;

    /** Maximum of kbps for bitrate field of stations in result. */
    private Integer bitrateMax;

    /** Not set=display all,
     * true=show only stations with geo_info,
     * false=show only stations without geo_info. */
    private Boolean hasGeoInfo;

    /** Not set=display all, true=show only stations
     * which do provide extended information, false=show only
     * stations without extended information. */
    private Boolean hasExtendedInfo;

    /** Not set=display all, true=show only stations which have
     * https url, false=show only stations that do stream
     * unencrypted with http. */
    private Boolean isHttps;

    /** Name of the attribute the result list will be sorted by. */
    private FieldName order;

    /** Reverse the result list if set to true. */
    private Boolean reverse;

    /** Do list/not list broken stations. */
    private Boolean hideBroken;

    @Override
    protected void apply(final MultivaluedMap<String, String> requestParams) {
        if (name != null) {
            requestParams.putSingle("name", name);
        }
        if (nameExact != null) {
            requestParams.putSingle("nameExact", nameExact.toString());
        }
        if (country != null) {
            requestParams.putSingle("country", country);
        }
        if (countryExact != null) {
            requestParams.putSingle("countryExact", countryExact.toString());
        }
        if (countryCode != null) {
            requestParams.putSingle("countrycode", countryCode);
        }
        if (state != null) {
            requestParams.putSingle("state", state);
        }
        if (stateExact != null) {
            requestParams.putSingle("stateExact", stateExact.toString());
        }
        if (language != null) {
            requestParams.putSingle("language", language);
        }
        if (languageExact != null) {
            requestParams.putSingle("languageExact", languageExact.toString());
        }
        if (tag != null) {
            requestParams.putSingle("tag", tag);
        }
        if (tagExact != null) {
            requestParams.putSingle("tagExact", tagExact.toString());
        }
        if (tagList != null) {
            requestParams.putSingle("tagList",
                    tagList.stream().collect(Collectors.joining(",")));
        }
        if (codec != null) {
            requestParams.putSingle("codec", codec);
        }
        // TODO is the string mapping correct?
        if (bitrateMin != null) {
            requestParams.putSingle("bitrateMin", bitrateMin.toString());
        }
        if (bitrateMax != null) {
            requestParams.putSingle("bitrateMax", bitrateMax.toString());
        }
        if (hasGeoInfo != null) {
            requestParams.putSingle("has_geo_info", hasGeoInfo.toString());
        }
        if (hasExtendedInfo != null) {
            requestParams.putSingle("has_extended_info",
                    hasExtendedInfo.toString());
        }
        if (isHttps != null) {
            requestParams.putSingle("is_https", isHttps.toString());
        }
        if (order != null) {
            requestParams.putSingle("order", order.name());
        }
        if (reverse != null) {
            requestParams.putSingle("reverse", reverse.toString());
        }
        // limit and offset are left out for Paging
        if (hideBroken != null) {
            requestParams.putSingle("hidebroken", reverse.toString());
        }
    }
}
