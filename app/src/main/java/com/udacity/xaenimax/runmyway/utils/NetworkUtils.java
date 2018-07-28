package com.udacity.xaenimax.runmyway.utils;

import android.net.Uri;

import com.udacity.xaenimax.runmyway.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String NYT_NEWS_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String QUERY_PARAM = "q";
    private static final String QUERY_VALUE = "fitness";
    private static final String API_KEY_PARAM = "api-key";

    public static URL buildUrl() {
        Uri builtUri = Uri.parse(NYT_NEWS_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, QUERY_VALUE)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
