package com.flecorre.webscraper;

import com.flecorre.webscraper.configuration.MangaProperties;
import com.flecorre.webscraper.process.ScheduleScrapingProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final ScheduleScrapingProcess process;

    @Autowired
    public CommandLineAppStartupRunner(ScheduleScrapingProcess process) {
        this.process = process;
    }

    @Override
    public void run(String... args) {
        process.scheduleWithFixedDelay();
    }
}
