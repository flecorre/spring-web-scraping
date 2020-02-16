package com.flecorre.webscraper.service.technical;

public interface PingService {

    boolean isReachableWithJsoup(String url);

    boolean isReachableWithSelenium(String url);
}
