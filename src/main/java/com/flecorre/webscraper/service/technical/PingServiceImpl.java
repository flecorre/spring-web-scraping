package com.flecorre.webscraper.service.technical;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PingServiceImpl implements PingService {

    private final YAMLConfig yamlConfig;

    @Autowired
    public PingServiceImpl(YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public boolean isReachableWithJsoup(String url) {
        Connection.Response res;
        boolean isReachable = false;
        try {
            res = Jsoup.connect(url).userAgent(yamlConfig.getUserAgent())
                    .timeout(5000)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .execute();
            isReachable = res.statusCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isReachable;
    }

    @Override
    public boolean isReachableWithSelenium(String url) {
        boolean isReachable = false;
        WebDriver driver = null;
        try {
            driver = getWebDriver();
            driver.get(url);
            Thread.sleep(7000);
            isReachable = driver.findElements(By.cssSelector("body > div.container > nav > a")).size() > 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return isReachable;
    }

    private WebDriver getWebDriver() {
        System.setProperty(yamlConfig.getChromedriver(), yamlConfig.getChromedriverPath());
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("user-agent='my user agent'");
        return new ChromeDriver(options);
    }
}
