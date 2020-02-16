package com.flecorre.webscraper.service.business.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface MangaScraperService {

    default List<YAMLConfig.Manga> scrapeData(List<YAMLConfig.Manga> mangaList) {
        List<YAMLConfig.Manga> newChapters = mangaList.stream()
                .filter(mg -> !mg.isFound())
                .map(this::getLastChapter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return newChapters;
    }

    YAMLConfig.Manga getLastChapter(YAMLConfig.Manga manga);

    default void updateChapterInList(YAMLConfig.Manga manga, int chapterFound, List<YAMLConfig.Manga> mangaList) {
        for (YAMLConfig.Manga mg : mangaList) {
            if (mg.getTitle().equals(manga.getTitle()) && mg.getChapter() < chapterFound) {
                mg.setChapter(chapterFound);
                mg.setFound(true);
            }
        }
    }
}
