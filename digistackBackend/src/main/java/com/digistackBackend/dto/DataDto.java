package com.digistackBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataDto {

    @JsonProperty("location_code")
    private Integer locationCode;

    @JsonProperty("language_code")
    private String languageCode;

    private List<String> keyword;

    @JsonProperty("date_from")
    private String dateFrom;

    @JsonProperty("search_partners")
    private boolean searchPartners;
}
