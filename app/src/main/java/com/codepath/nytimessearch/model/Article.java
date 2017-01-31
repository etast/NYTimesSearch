package com.codepath.nytimessearch.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by etast on 1/30/17.
 */

public class Article implements Serializable {
    private String webUrl;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    private String headline;
    private String thumbnail;

    public Article(JSONObject articleJson) throws JSONException {
        try {
            this.headline = articleJson.getJSONObject("headline").optString("main", "name");
            this.webUrl = articleJson.getString("web_url");
            JSONArray multimedia = articleJson.getJSONArray("multimedia");
            if (multimedia != null && multimedia.length() > 0) {
                JSONObject multimediaFirst = multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/" + multimediaFirst.optString("url");
            } else {
                this.thumbnail = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray articles) {
        ArrayList <Article> results = new ArrayList<>();

        for (int x = 0; x < articles.length(); x++) {
            try {
                results.add(new Article(articles.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
