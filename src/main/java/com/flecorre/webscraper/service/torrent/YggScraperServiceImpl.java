package com.flecorre.webscraper.service.torrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class YggScraperServiceImpl implements TorrentScraperService {

    private Set<String> movies;
    private static final Logger LOGGER = LoggerFactory.getLogger(YggScraperServiceImpl.class);

    @Override
    public String scrapeData() {
        if (movies == null) {
            movies = new HashSet<>();
        }



        return null;
    }
}
