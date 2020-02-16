package com.flecorre.webscraper.service.business.manga;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("japscanService")
public class JapscanScraperServiceImpl implements MangaScraperService {

    private final YAMLConfig yamlConfig;

    private static final String RAW = "raw";
    private static final String SPOILER = "spoiler";
    private static final String CSS_SELECTOR_LAST_CHAPTER = "#collapse-1 > div:nth-child(1) > a";
    private static final Logger LOGGER = LoggerFactory.getLogger(JapscanScraperServiceImpl.class);

    @Autowired
    public JapscanScraperServiceImpl(YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public synchronized YAMLConfig.Manga getLastChapter(YAMLConfig.Manga manga) {
        if (manga.getJapscanUrl() == null) {
            return null;
        }
        YAMLConfig.Manga newManga = null;
        WebDriver driver = null;
        try {
            LOGGER.info("JAPSCAN: searching for {}", manga.getTitle());
            driver = getWebDriver();
            driver.get(manga.getJapscanUrl());
            WebDriverWait waitForCloudflare = new WebDriverWait(driver, 7000);
            waitForCloudflare.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(CSS_SELECTOR_LAST_CHAPTER)));
            WebElement we = driver.findElement(By.cssSelector(CSS_SELECTOR_LAST_CHAPTER));
            String fullChapterName = we.getText().toLowerCase();
            String fullChapterUrl = we.getAttribute("href");
            if (!fullChapterName.contains((RAW)) && !fullChapterName.contains(SPOILER)) {
                String[] chars = fullChapterUrl.split("/");
                String strChapterFound = chars[chars.length - 1];
                int chapterFound = Integer.parseInt(strChapterFound);
                if (manga.getChapter() < chapterFound) {
                    updateChapterInList(manga, chapterFound, this.yamlConfig.getMangas());
                    newManga = YAMLConfig.Manga.builder().title(manga.getTitle()).chapter(chapterFound).foundChapterUrl(fullChapterUrl).build();
                    LOGGER.info("JAPSCAN: new chapter found: " + newManga.getTitle() + " - " + newManga.getChapter());
                }
            }
            LOGGER.info("JAPSCAN: done searching for {}", manga.getTitle());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return newManga;
    }

    private WebDriver getWebDriver() {
        System.setProperty(yamlConfig.getChromedriver(), yamlConfig.getChromedriverPath());
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("user-agent='my user agent'");
        return new ChromeDriver(options);
    }
}
