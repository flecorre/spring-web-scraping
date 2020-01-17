package com.flecorre.webscraper.service.telegram;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

public interface TelegramService {

    SendResponse sendTorrentUpdate(String message);

    void sendMangaUpdate(List<YAMLConfig.Manga> mangaList);

}
