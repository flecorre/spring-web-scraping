package com.flecorre.webscraper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Movie {

    private String title;
    private String year;
    private String url;
    private String genre;
    private String country;
    private String plot;
    private String director;
    private String actors;
    private String poster;

}
