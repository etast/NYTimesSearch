package com.codepath.nytimessearch.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by etast on 1/30/17.
 */

public class Article implements Serializable {
    private String mWebUrl;
    private String mHeadline;
    private String mThumbnail;

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getThumbnail() {
        return mThumbnail;
    }


    public Article(JSONObject articleJson) throws JSONException {
        try {
            mHeadline = articleJson.getJSONObject("headline").optString("main", "name");
            mWebUrl = articleJson.getString("web_url");
            JSONArray multimedia = articleJson.getJSONArray("multimedia");
            if (multimedia != null && multimedia.length() > 0) {
                JSONObject multimediaFirst = multimedia.getJSONObject(new Random().nextInt(multimedia.length()));
                mThumbnail = "http://www.nytimes.com/" + multimediaFirst.optString("url");
            } else {
                mThumbnail = "";
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
