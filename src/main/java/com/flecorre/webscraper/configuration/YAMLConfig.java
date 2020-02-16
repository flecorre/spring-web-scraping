package com.flecorre.webscraper.configuration;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties("app")
public class YAMLConfig {

    private String userAgent;
    private String chatId;
    private String telegramToken;
    private String omdbKey;
    private String yggtorrentExclus;
    private String chromedriverPath;
    private String chromedriver;
    private String movieList;
    private List<Manga> mangas = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manga {
        private String title;
        private String kakalotUrl;
        private String japscanUrl;
        private String jaiminiUrl;
        private int chapter;
        private String foundChapterUrl;
        private boolean found;
    }

}
