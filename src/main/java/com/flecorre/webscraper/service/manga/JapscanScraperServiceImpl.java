package com.flecorre.webscraper.service.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("japscanService")
public class JapscanScraperServiceImpl implements MangaScraperService {

    private final YAMLConfig yamlConfig;

    private static final String CHROME_DRIVER = "webdriver.chrome.driver";
    private static final String CHROME_DRIVER_PATH = "../chromedriver.exe";
    private static final String CSS_SELECTOR_LAST_CHAPTER = "#collapse-1 > div:nth-child(1) > a";

    @Autowired
    public JapscanScraperServiceImpl(YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public YAMLConfig.Manga getLastChapter(YAMLConfig.Manga manga) {
        if (manga.getJapscanUrl() == null) {
            return null;
        }

        YAMLConfig.Manga newManga = null;
        WebDriver driver = null;
        try {
            System.setProperty(CHROME_DRIVER, CHROME_DRIVER_PATH);
            driver = new ChromeDriver();
            driver.get(manga.getJapscanUrl());
            Thread.sleep(7000);
            WebElement we = driver.findElement(By.cssSelector(CSS_SELECTOR_LAST_CHAPTER));
            String fullChapterName = we.getText();
            String fullChapterUrl = we.getAttribute("href");
            if (!fullChapterName.contains(("raw")) && !fullChapterName.contains("spoiler")) {
                String[] chars = fullChapterUrl.split("/");
                String strChapterFound = chars[chars.length - 1];
                int chapterFound = Integer.parseInt(strChapterFound);
                if (manga.getChapter() < chapterFound) {
                    updateChapterInList(manga, chapterFound, this.yamlConfig.getMangas());
                    newManga = YAMLConfig.Manga.builder().title(manga.getTitle()).chapter(chapterFound).foundChapterUrl(fullChapterUrl).build();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.close();
            }
        }
        return newManga;
    }
}
