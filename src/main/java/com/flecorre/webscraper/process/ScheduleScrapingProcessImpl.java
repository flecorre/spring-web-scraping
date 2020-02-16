package com.flecorre.webscraper.process;

import com.flecorre.webscraper.Exception.ScraperException;
import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.domain.Movie;
import com.flecorre.webscraper.service.business.manga.MangaScraperService;
import com.flecorre.webscraper.service.technical.PingService;
import com.flecorre.webscraper.service.technical.TelegramService;
import com.flecorre.webscraper.service.business.torrent.TorrentScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleScrapingProcessImpl implements ScheduleScrapingProcess {

    private final MangaScraperService kakalotService;
    private final MangaScraperService japscanService;
    private final MangaScraperService jaiminiService;
    private final TorrentScraperService torrentService;
    private final TelegramService telegramService;
    private final PingService pingService;
    private final YAMLConfig yamlConfig;

    private int count = 0;

    private static final String URL_JAPSCAN = "https://www.japscan.co";
    private static final String URL_KAKALOT = "https://manganelo.com";
    private static final String URL_JAIMINI = "https://jaiminisbox.com";
    private static final String URL_YGGTORRENT= "https://www2.yggtorrent.se";
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleScrapingProcessImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public ScheduleScrapingProcessImpl(YAMLConfig yamlConfig, @Qualifier("kakalotService") MangaScraperService kakalotService, @Qualifier("japscanService") MangaScraperService japscanService, TorrentScraperService torrentService, TelegramService telegramService, @Qualifier("jaiminiService") MangaScraperService jaiminiService, PingService pingService) {
        this.kakalotService = kakalotService;
        this.torrentService = torrentService;
        this.telegramService = telegramService;
        this.japscanService = japscanService;
        this.yamlConfig = yamlConfig;
        this.jaiminiService = jaiminiService;
        this.pingService = pingService;
    }

    @Override
    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduleScrappingWithFixedDelay() {
        try {
            scrapeMangas();
            scrapeMovies();
        } catch (ScraperException e) {
            telegramService.sendError(e.getMessage());
        }
        LOGGER.info("SCRAPPING DONE - {} - Execution Time - {}", ++count, dateTimeFormatter.format(LocalDateTime.now()) );
    }

    private void scrapeMangas() {
        List<YAMLConfig.Manga> newMangas = new ArrayList<>();
        if (pingService.isReachableWithJsoup(URL_JAIMINI)) {
            newMangas.addAll(jaiminiService.scrapeData(yamlConfig.getMangas()));
        } else {
            telegramService.sendError("Jaimini is not reachable");
        }
        if (pingService.isReachableWithJsoup(URL_KAKALOT)) {
            newMangas.addAll(kakalotService.scrapeData(yamlConfig.getMangas()));
        } else {
            telegramService.sendError("Kakalot is not reachable");
        }
        if (pingService.isReachableWithSelenium(URL_JAPSCAN)) {
            newMangas.addAll(japscanService.scrapeData(yamlConfig.getMangas()));
        } else {
            telegramService.sendError("Japscan is not reachable");
        }
        if (!newMangas.isEmpty()) {
            telegramService.sendMangaUpdate(newMangas);
        }
    }

    private void scrapeMovies() throws ScraperException {
        if (pingService.isReachableWithJsoup(URL_YGGTORRENT)) {
            List<Movie> movieList = torrentService.scrapeData();
            if (!movieList.isEmpty()) {
                telegramService.sendMovieUpdate(movieList);
            }
        } else {
            telegramService.sendError("Yggtorrent is not reachable");
        }
    }
}
