package com.flecorre.webscraper.service;

import com.flecorre.webscraper.configuration.MangaProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("manga")
public class ScraperServiceMangaImpl implements ScraperService {

    private final MangaProperties mangaProperties;

    @Autowired
    public ScraperServiceMangaImpl(MangaProperties mangaProperties) {
        this.mangaProperties = mangaProperties;
    }

    @Override
    public Map<String, Integer> scrapeData() {
        final List<MangaProperties.Manga> mangas = new ArrayList<>(mangaProperties.getMangas());

        Map<String, Integer> newChapters = mangas.stream()
                .map(mg -> getLastChapter(mg))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(MangaProperties.Manga::getTitle, MangaProperties.Manga::getChapter));
        return newChapters;
    }

    private MangaProperties.Manga getLastChapter(MangaProperties.Manga manga) {
        try {
            Document document = Jsoup.connect(manga.getUrl()).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .timeout(5000)
                    .get();
            String fullChapterName = document.select("ul.row-content-chapter > li.a-h > a.chapter-name").first().attr("href");
            String[] chars = fullChapterName.split("_");
            String strChapterFound = chars[chars.length - 1];
            if (!strChapterFound.contains(".")) {
                int chapterFound = Integer.parseInt(strChapterFound);
                if (manga.getChapter() < chapterFound) {
                    updateChapterInList(manga, chapterFound);
                    return MangaProperties.Manga.builder().title(manga.getTitle()).chapter(chapterFound).build();
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private void updateChapterInList(MangaProperties.Manga manga, int chapterFound) {
        for (MangaProperties.Manga mg : mangaProperties.getMangas()) {
            if (mg.getTitle().equals(manga.getTitle()) && mg.getChapter() < chapterFound) {
                mg.setChapter(chapterFound);
            }
        }
    }
}
