package com.flecorre.webscraper.process;

import com.flecorre.webscraper.service.manga.MangaScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduleScrapingProcessImpl implements ScheduleScrapingProcess {

    private final MangaScraperService manga;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleScrapingProcessImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public ScheduleScrapingProcessImpl(@Qualifier("manga") MangaScraperService manga) {
        this.manga = manga;
    }

    @Override
    @Scheduled(fixedRate = 10000, initialDelay = 5000)
    public void scheduleWithFixedDelay() {
        manga.scrapeData();
        LOGGER.info("Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
    }
}
