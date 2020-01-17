package com.flecorre.webscraper.service.telegram;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TelegramServiceImpl implements TelegramService {

    private TelegramBot bot;
    private final YAMLConfig yamlConfig;
    private final static Logger LOGGER = LoggerFactory.getLogger(TelegramServiceImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TelegramServiceImpl(YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public SendResponse sendTorrentUpdate(String message) {

        return null;
    }

    @Override
    public void sendMangaUpdate(List<YAMLConfig.Manga> mangaList) {
        String mangaListAsHTML = getMangaListAsHTML(mangaList);
        SendMessage msg = new SendMessage(yamlConfig.getChatId(), mangaListAsHTML).parseMode(ParseMode.HTML).disableWebPagePreview(true);
        bot.execute(msg);
        LOGGER.info("SEND MANGA UPDATE - Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
    }

    private String getMangaListAsHTML(List<YAMLConfig.Manga> mangaList) {
        StringBuilder sb = new StringBuilder();
        for (YAMLConfig.Manga manga : mangaList) {
            sb.append("<a href=")
                    .append("\"")
                    .append(manga.getChapterUrl())
                    .append("\"")
                    .append(" target=\"_blank\">")
                    .append(manga.getTitle())
                    .append(" - ")
                    .append(manga.getChapter())
                    .append("</a>")
                    .append("\n");
        }
        return sb.toString();
    }

    @PostConstruct
    private void initBot() {
        this.bot = new TelegramBot(yamlConfig.getTelegramToken());
    }
}