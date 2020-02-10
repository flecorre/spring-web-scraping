package com.flecorre.webscraper.process;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.domain.Movie;
import com.flecorre.webscraper.service.manga.MangaScraperService;
import com.flecorre.webscraper.service.telegram.TelegramService;
import com.flecorre.webscraper.service.torrent.TorrentScraperService;
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
    private final TorrentScraperService torrentService;
    private final TelegramService telegramService;
    private final YAMLConfig yamlConfig;

    private int count;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleScrapingProcessImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public ScheduleScrapingProcessImpl(YAMLConfig yamlConfig, @Qualifier("kakalotService") MangaScraperService kakalotService, @Qualifier("japscanService") MangaScraperService japscanService, TorrentScraperService torrentService, TelegramService telegramService) {
        this.kakalotService = kakalotService;
        this.torrentService = torrentService;
        this.telegramService = telegramService;
        this.japscanService = japscanService;
        this.yamlConfig = yamlConfig;
    }

    @Override
    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduleScrappingWithFixedDelay() {
        List<YAMLConfig.Manga> newMangas = new ArrayList<>();
        newMangas.addAll(kakalotService.scrapeData(yamlConfig.getMangas()));
        newMangas.addAll(japscanService.scrapeData(yamlConfig.getMangas()));
        if (!newMangas.isEmpty()) {
            telegramService.sendMangaUpdate(newMangas);
        }

        List<Movie> movieList = torrentService.scrapeData();
        if (!movieList.isEmpty()) {
            telegramService.sendMovieUpdate(movieList);
        }
        LOGGER.info("SCRAPPING DONE - {} - Execution Time - {}", ++count, dateTimeFormatter.format(LocalDateTime.now()) );
    }
}
