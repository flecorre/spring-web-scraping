package com.flecorre.webscraper.service.business.torrent;

import com.flecorre.webscraper.Exception.ScraperException;
import com.flecorre.webscraper.domain.Movie;

import java.util.List;

public interface TorrentScraperService {

    List<Movie> scrapeData() throws ScraperException;

    void fetchDataFromOMDB(Movie movie);
}
