package com.flecorre.webscraper.process;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.service.manga.MangaScraperService;
import com.flecorre.webscraper.service.telegram.TelegramService;
import com.flecorre.webscraper.service.torrent.TorrentScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ScheduleScrapingProcessImpl implements ScheduleScrapingProcess {

    private final MangaScraperService mangaService;
    private final TorrentScraperService torrentService;
    private final TelegramService telegramService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleScrapingProcessImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public ScheduleScrapingProcessImpl(MangaScraperService mangaService, TorrentScraperService torrentService, TelegramService telegramService) {
        this.mangaService = mangaService;
        this.torrentService = torrentService;
        this.telegramService = telegramService;
    }

    @Override
    @Scheduled(fixedRate = 100000000, initialDelay = 5000)
    public void scheduleScrappingWithFixedDelay() {
        List<YAMLConfig.Manga> mangaList = mangaService.scrapeData();
        if (!mangaList.isEmpty()) {
            telegramService.sendMangaUpdate(mangaList);
        }
        //torrentService.scrapeData();
        LOGGER.info("SCRAPPING DONE - Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
    }
}
