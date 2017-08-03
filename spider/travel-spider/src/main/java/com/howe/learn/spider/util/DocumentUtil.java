package com.howe.learn.spider.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author Karl
 * @Date 2017/1/4 14:34
 */
public class DocumentUtil {

    public static Document parse(String sourceUrl) throws Exception {
        URL url = new URL(sourceUrl);
        return Jsoup.parse(HttpUtils.get(sourceUrl), Constants.DEFAULT_CHARSET, url.getProtocol() + "://" + url.getHost());
    }
}
