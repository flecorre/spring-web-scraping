package com.flecorre.webscraper.service.business.torrent;

import com.flecorre.webscraper.Exception.ScraperException;
import com.flecorre.webscraper.configuration.YAMLConfig;
import com.flecorre.webscraper.domain.Movie;
import com.flecorre.webscraper.domain.ResponseOMDB;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YggScraperServiceImpl implements TorrentScraperService {

    private final YAMLConfig yamlConfig;
    private final RestTemplate restTemplate;
    private Set<String> movieTitleSet;

    private final static String VOSTA = "vosta";
    private final static String VOSTFR = "vostfr";
    private final static String RESOLUTION_1080 = "1080p";
    private final static String RESOLUTION_720 = "720p";
    private final static Logger LOGGER = LoggerFactory.getLogger(YggScraperServiceImpl.class);

    public YggScraperServiceImpl(RestTemplateBuilder restTemplateBuilder, YAMLConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<Movie> scrapeData() throws ScraperException {
        List<Movie> newMovieList = new ArrayList<>();
        try {
            LOGGER.info("YGGTORRENT: searching for new movies");
            Document doc = Jsoup.connect(yamlConfig.getYggtorrentExclus()).userAgent(yamlConfig.getUserAgent())
                    .timeout(5000)
                    .get();
            Element table = doc.select("table").get(0);
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size(); i++) {
                Elements cols = rows.get(i).select("td > a");
                String movieTitle = cols.text().toLowerCase();
                if (isMovieFormatCorrect(movieTitle)) {
                    String movieYear = getYearFromMovieTitle(movieTitle);
                    String formattedMovieTitle = formatMovieTitle(cols.text());
                    if (this.movieTitleSet.add(formattedMovieTitle)) {
                        Movie movie = Movie.builder().title(formattedMovieTitle).url(cols.get(1).attr("href")).year(movieYear).build();
                        fetchDataFromOMDB(movie);
                        newMovieList.add(movie);
                        LOGGER.info("YGGTORRENT: movie found: {} - {}", movie.getTitle(), movie.getYear());
                    }
                }
            }
            saveNewMovieTitleToFile(this.movieTitleSet);
            LOGGER.info("YGGTORRENT: done searching for new movies");
        } catch (IOException e) {
            LOGGER.error("YGGTORRENT: error when scrapping {}", e);
            throw new ScraperException(ScraperException.ScraperError.YGGTORRENT, e.getMessage());
        }
        return newMovieList;
    }

    @Override
    public void fetchDataFromOMDB(Movie movie) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://www.omdbapi.com/?apikey=")
                .append(yamlConfig.getOmdbKey())
                .append("&t=")
                .append(movie.getTitle().replaceAll(" ", "+"))
                .append("&y=")
                .append(movie.getYear());
        ResponseOMDB res = this.restTemplate.getForObject(sb.toString(), ResponseOMDB.class);
        if (res != null && res.isResponse()) {
            movie.setGenre(res.getGenre());
            movie.setActors(res.getActors());
            movie.setCountry(res.getCountry());
            movie.setDirector(res.getDirector());
            movie.setPlot(res.getPlot());
            movie.setPoster(res.getPoster());
        }
    }

    private String formatMovieTitle(String title) {
        String noSemiColons = title.replaceAll(":", "");
        String noDash = noSemiColons.replaceAll("-", "");
        String noParentheses = noDash.replaceAll("[()]", "");
        String noDots = noParentheses.replaceAll("\\.", " ");
        String noDoubleWhiteSpaces = noDots.trim().replaceAll(" +", " ");
        String titleWithLastWhiteSpace = noDoubleWhiteSpaces.split("20\\d{2}")[0];
        return titleWithLastWhiteSpace.replaceAll("\\s+$", "");
    }

    private boolean isMovieFormatCorrect(String title) {
        return !title.contains(VOSTA) && !title.contains(VOSTFR) && (title.contains(RESOLUTION_1080) || title.contains(RESOLUTION_720));
    }

    private String getYearFromMovieTitle(String title) {
        String year = null;
        Pattern MY_PATTERN = Pattern.compile("20\\d{2}");
        Matcher m = MY_PATTERN.matcher(title);
        while (m.find()) {
            year = m.group(0);
        }
        return year;
    }

    private void saveNewMovieTitleToFile(Set<String> movieTitleSet) throws ScraperException {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(yamlConfig.getMovieList()));
            for (String title : movieTitleSet) {
                pw.println(title);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("YGGTORRENT: error when writing to text file {}", e);
            throw new ScraperException(ScraperException.ScraperError.YGGTORRENT, e.getMessage());
        }

    }

    @PostConstruct
    private void initMovieTitleSet() throws ScraperException {
        try {
            File file = new File(yamlConfig.getMovieList());
            if (file.exists()) {
                this.movieTitleSet = new HashSet<>(FileUtils.readLines(new File(yamlConfig.getMovieList()), StandardCharsets.UTF_8));
            } else {
                this.movieTitleSet = new HashSet<>();
            }
        } catch (IOException e) {
            LOGGER.error("YGGTORRENT: error when initializing movie list file {}", e);
            throw new ScraperException(ScraperException.ScraperError.YGGTORRENT, e.getMessage());
        }
    }
}
