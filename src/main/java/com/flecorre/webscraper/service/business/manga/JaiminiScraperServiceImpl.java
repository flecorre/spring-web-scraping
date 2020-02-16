package com.flecorre.webscraper.service.business.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("jaiminiService")
public class JaiminiScraperServiceImpl implements MangaScraperService {

    private final YAMLConfig yamlConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(JaiminiScraperServiceImpl.class);

    @Autowired
    public JaiminiScraperServiceImpl(YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public YAMLConfig.Manga getLastChapter(YAMLConfig.Manga manga) {
        if (manga.getJaiminiUrl() == null) {
            return null;
        }
        YAMLConfig.Manga newManga = null;
        try {
            LOGGER.info("JAIMINI: searching for {}", manga.getTitle());
            Document document = Jsoup.connect(manga.getJaiminiUrl()).userAgent(yamlConfig.getUserAgent())
                    .timeout(5000)
                    .get();
            String fullChapterUrl = document.select("#content > div > div.list > div.group > div:nth-child(2) > div.title > a").attr("href");
            String[] chars = fullChapterUrl.split("/");
            String strChapterFound = chars[chars.length - 1];
            if (!strChapterFound.contains(".")) {
                int chapterFound = Integer.parseInt(strChapterFound);
                if (manga.getChapter() < chapterFound) {
                    updateChapterInList(manga, chapterFound, this.yamlConfig.getMangas());
                    newManga = YAMLConfig.Manga.builder().title(manga.getTitle()).chapter(chapterFound).foundChapterUrl(fullChapterUrl).build();
                    LOGGER.info("JAIMINI: new chapter found: " + newManga.getTitle() + " - " + newManga.getChapter());
                }
            }
            LOGGER.info("JAIMINI: done searching for {}", manga.getTitle());
        } catch (IOException e) {
            LOGGER.error("JAIMINI: error with {}", manga.getJapscanUrl());
        }
        return newManga;
    }

}
