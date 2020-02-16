package com.flecorre.webscraper.service.technical;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.domain.Movie;

import java.util.List;

public interface TelegramService {

    void sendMovieUpdate(List<Movie> movieList);

    void sendMangaUpdate(List<YAMLConfig.Manga> mangaList);

    void sendError(String errorMessage);

}
