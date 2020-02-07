package com.flecorre.webscraper.service.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;

import java.util.List;

public interface MangaScraperService {

    List<YAMLConfig.Manga> scrapeData(List<YAMLConfig.Manga> mangaList);

    default void updateChapterInList(YAMLConfig.Manga manga, int chapterFound, List<YAMLConfig.Manga> mangaList) {
        for (YAMLConfig.Manga mg : mangaList) {
            if (mg.getTitle().equals(manga.getTitle()) && mg.getChapter() < chapterFound) {
                mg.setChapter(chapterFound);
                mg.setFound(true);
            }
        }
    }
}
