package com.flecorre.webscraper.service.telegram;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.domain.Movie;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

public interface TelegramService {

    SendResponse sendMovieUpdate(List<Movie> movieList);

    void sendMangaUpdate(List<YAMLConfig.Manga> mangaList);

}
