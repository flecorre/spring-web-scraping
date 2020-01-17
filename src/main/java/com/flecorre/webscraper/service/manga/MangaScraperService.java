package com.flecorre.webscraper.service.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;

import java.util.List;

public interface MangaScraperService {
    List<YAMLConfig.Manga> scrapeData();
}
