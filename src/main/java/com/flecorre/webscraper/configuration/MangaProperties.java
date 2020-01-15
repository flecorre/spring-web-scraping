package com.flecorre.webscraper.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("app")
public class MangaProperties {

    private List<Manga> mangas = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manga {
        private String title;
        private String url;
        private int chapter;
    }

    public List<Manga> getMangas() {
        return mangas;
    }
}
