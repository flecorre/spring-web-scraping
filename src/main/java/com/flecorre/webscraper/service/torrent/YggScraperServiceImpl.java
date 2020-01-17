package com.flecorre.webscraper.service.torrent;

import com.flecorre.webscraper.configuration.YAMLConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class YggScraperServiceImpl implements TorrentScraperService {

    private final YAMLConfig yamlConfig;
    private Set<String> movieSet;
    private final static Logger LOGGER = LoggerFactory.getLogger(YggScraperServiceImpl.class);

    public YggScraperServiceImpl(YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    @Override
    public List<String> scrapeData() {
        if (movieSet == null) {
            movieSet = new HashSet<>();
        }
        List<String> newMovieList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www2.yggtorrent.ws/torrents/exclus").userAgent(yamlConfig.getUserAgent())
                    .timeout(5000)
                    .get();
            Element table = doc.select("table").get(0);
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size(); i++) {
                Elements cols = rows.get(i).select("td > a");
                String formattedMovieTitle = formatMovieTitle(cols.text());
                if (!formattedMovieTitle.contains("vosta") && !formattedMovieTitle.contains("vostfr") && this.movieSet.add(formattedMovieTitle)) {
                    newMovieList.add(formattedMovieTitle);
                }
            }
            LOGGER.info(newMovieList.toString());
            return newMovieList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatMovieTitle(String movieTitle) {
        String noSemiColons = movieTitle.replaceAll(":", "");
        String noDash = noSemiColons.replaceAll("-", "");
        String noParentheses = noDash.replaceAll("[()]", "");
        String noDots = noParentheses.replaceAll("\\.", " ");
        String noDoubleWhiteSpaces = noDots.trim().replaceAll(" +", " ");
        String lowerCaseTitle = noDoubleWhiteSpaces.toLowerCase();
        return lowerCaseTitle.split("20\\d{2}")[0];
    }

}
