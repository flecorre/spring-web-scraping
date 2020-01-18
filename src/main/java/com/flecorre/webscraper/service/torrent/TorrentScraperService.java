package com.flecorre.webscraper.service.torrent;

import com.flecorre.webscraper.domain.Movie;

import java.util.List;

public interface TorrentScraperService {

    List<Movie> scrapeData();

    void fetchDataFromOMDB(Movie movie);
}
