package de.sfuhrm.radiobrowser4j;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Holder for an advanced search query.
* A builder can be created by calling
* {@code AdvancedSearch.builder()},
* and then when you are finished
* {@code AdvancedSearch.AdvancedSearchBuilder.build()}.
 * Please note that this
* @author Stephan Fuhrmann
 * */
@Builder
public final class AdvancedSearch extends InternalParameter {
    /** Name of the station. */
    private String name;

    /** True: only exact matches, otherwise all matches. */
    private Boolean nameExact;

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
    protected void apply(final Map<String, String> requestParams) {
        if (name != null) {
            requestParams.put("name", name);
        }
        if (nameExact != null) {
            requestParams.put("nameExact", nameExact.toString());
        }
        if (countryCode != null) {
            requestParams.put("countrycode", countryCode);
        }
        if (state != null) {
            requestParams.put("state", state);
        }
        if (stateExact != null) {
            requestParams.put("stateExact", stateExact.toString());
        }
        if (language != null) {
            requestParams.put("language", language);
        }
        if (languageExact != null) {
            requestParams.put("languageExact", languageExact.toString());
        }
        if (tag != null) {
            requestParams.put("tag", tag);
        }
        if (tagExact != null) {
            requestParams.put("tagExact", tagExact.toString());
        }
        if (tagList != null) {
            requestParams.put("tagList",
                    tagList.stream().collect(Collectors.joining(",")));
        }
        if (codec != null) {
            requestParams.put("codec", codec);
        }
        if (bitrateMin != null) {
            requestParams.put("bitrateMin", bitrateMin.toString());
        }
        if (bitrateMax != null) {
            requestParams.put("bitrateMax", bitrateMax.toString());
        }
        if (hasGeoInfo != null) {
            requestParams.put("has_geo_info", hasGeoInfo.toString());
        }
        if (hasExtendedInfo != null) {
            requestParams.put("has_extended_info",
                    hasExtendedInfo.toString());
        }
        if (isHttps != null) {
            requestParams.put("is_https", isHttps.toString());
        }
        if (order != null) {
            requestParams.put("order", order.name().toLowerCase());
        }
        if (reverse != null) {
            requestParams.put("reverse", reverse.toString());
        }
        // limit and offset are left out for Paging
        if (hideBroken != null) {
            requestParams.put("hidebroken", hideBroken.toString());
        }
    }
}
