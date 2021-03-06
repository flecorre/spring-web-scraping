package com.flecorre.webscraper.service.technical;

import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.domain.Movie;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
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
    public void sendMovieUpdate(List<Movie> movieList) {
        for (Movie movie : movieList) {
            LOGGER.info("TELEGRAM: Preparing movie message for {}", movie.getTitle());
            SendMessage msg = new SendMessage(yamlConfig.getChatId(), formatMovieToHTML(movie))
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(false);
            this.bot.execute(msg);
            LOGGER.info("TELEGRAM: movie message sent for {} - Execution Time - {}", movie.getTitle(), dateTimeFormatter.format(LocalDateTime.now()));
        }
    }

    @Override
    public void sendMangaUpdate(List<YAMLConfig.Manga> mangaList) {
        for (YAMLConfig.Manga manga : mangaList) {
            LOGGER.info("TELEGRAM: preparing manga message for {}", manga.getTitle());
            SendMessage msg = new SendMessage(yamlConfig.getChatId(), formatMangaListToHTML(manga))
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(false);
            this.bot.execute(msg);
            LOGGER.info("TELEGRAM: manga message sent for {} - Execution Time - {}", manga.getTitle(), dateTimeFormatter.format(LocalDateTime.now()));
        }
    }

    @Override
    public void sendError(String errorMessage) {
        SendMessage msg = new SendMessage(yamlConfig.getChatId(), errorMessage);
        this.bot.execute(msg);
    }

    private String formatMangaListToHTML(YAMLConfig.Manga manga) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"")
                .append(manga.getFoundChapterUrl())
                .append("\"")
                .append(" target=\"_blank\">")
                .append(manga.getTitle())
                .append(" - ")
                .append(manga.getChapter())
                .append("</a>\n");
        return sb.toString();
    }

    private String formatMovieToHTML(Movie movie) {
        StringBuilder sb = new StringBuilder();
        if (movie.getPoster() != null) {
            sb.append("<a href=\"")
                    .append(movie.getPoster())
                    .append("\"")
                    .append(" target=\"_blank\">")
                    .append(movie.getTitle())
                    .append(" - ")
                    .append(movie.getYear())
                    .append("</a>\n")
                    .append("<i>")
                    .append(movie.getPlot())
                    .append("</i>\n")
                    .append("<a href=\"")
                    .append(movie.getUrl())
                    .append("\"")
                    .append(" target=\"_blank\">")
                    .append("<b>DOWNLOAD</b>")
                    .append("</a>\n");
        } else {
            sb.append("<a href=\"")
                    .append(movie.getUrl())
                    .append("\"")
                    .append(" target=\"_blank\">")
                    .append(movie.getTitle())
                    .append(" - ")
                    .append(movie.getYear())
                    .append("</a>\n");
        }
        return sb.toString();
    }

    @PostConstruct
    private void initBot() {
        this.bot = new TelegramBot(yamlConfig.getTelegramToken());
    }
}
