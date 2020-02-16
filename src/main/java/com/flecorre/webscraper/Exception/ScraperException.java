package com.flecorre.webscraper.Exception;

public class ScraperException extends Exception {

    private ScraperError scraperError;
    private String message;

    public ScraperException(ScraperError scraperError, String message) {
        this.scraperError = scraperError;
        this.message = message;
    }

    public ScraperError getScraperError() {
        return scraperError;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public enum ScraperError {
        YGGTORRENT("Yggtorrent error");

        private String error;

        ScraperError(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

    }
}
