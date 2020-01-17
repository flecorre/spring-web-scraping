package com.flecorre.webscraper.service.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("manga")
public class KakalotScraperServiceImpl implements MangaScraperService {

    private final YAMLConfig yamlConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(KakalotScraperServiceImpl.class);

    @Autowired
    public KakalotScraperServiceImpl(YAMLConfig YAMLConfig) {
        this.yamlConfig = YAMLConfig;
    }

    @Override
    public Map<String, Integer> scrapeData() {
        final List<YAMLConfig.Manga> mangas = new ArrayList<>(yamlConfig.getMangas());

        Map<String, Integer> newChapters = mangas.stream()
                .map(mg -> getLastChapter(mg))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(YAMLConfig.Manga::getTitle, YAMLConfig.Manga::getChapter));
        LOGGER.info(newChapters.toString());
        return newChapters;
    }

    private YAMLConfig.Manga getLastChapter(YAMLConfig.Manga manga) {
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
                    return YAMLConfig.Manga.builder().title(manga.getTitle()).chapter(chapterFound).build();
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private void updateChapterInList(YAMLConfig.Manga manga, int chapterFound) {
        for (YAMLConfig.Manga mg : yamlConfig.getMangas()) {
            if (mg.getTitle().equals(manga.getTitle()) && mg.getChapter() < chapterFound) {
                mg.setChapter(chapterFound);
            }
        }
    }
}
