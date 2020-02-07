package com.flecorre.webscraper.service.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("kakalotService")
public class KakalotScraperServiceImpl implements MangaScraperService {

    private final YAMLConfig yamlConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(KakalotScraperServiceImpl.class);

    @Autowired
    public KakalotScraperServiceImpl(YAMLConfig YAMLConfig) {
        this.yamlConfig = YAMLConfig;
    }

    @Override
    public YAMLConfig.Manga getLastChapter(YAMLConfig.Manga manga) {
        if (manga.getKakalotUrl() == null) {
            return null;
        }

        YAMLConfig.Manga newManga = null;
        try {
            Document document = Jsoup.connect(manga.getKakalotUrl()).userAgent(yamlConfig.getUserAgent())
                    .timeout(5000)
                    .get();
            String fullChapterUrl = document.select("ul.row-content-chapter > li.a-h > a.chapter-name").first().attr("href");
            String[] chars = fullChapterUrl.split("_");
            String strChapterFound = chars[chars.length - 1];
            if (!strChapterFound.contains(".")) {
                int chapterFound = Integer.parseInt(strChapterFound);
                if (manga.getChapter() < chapterFound) {
                    updateChapterInList(manga, chapterFound, this.yamlConfig.getMangas());
                    newManga = YAMLConfig.Manga.builder().title(manga.getTitle()).chapter(chapterFound).foundChapterUrl(fullChapterUrl).build();
                }
            }
        } catch (IOException e) {
            LOGGER.error(e + " " + manga.getKakalotUrl());
        }
        return newManga;
    }
}
