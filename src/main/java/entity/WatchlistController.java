package entity;

import view.WatchlistView;

import javax.json.JsonReader;
import javax.swing.*;
import java.awt.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WatchlistController {
    private WatchlistModel watchlistModel;
    private int currentpage = 0;
    private int moviesperpage = 12;

    //API Stuff -------
    private String apiKey;
    private String sessionId;
    private String watchlistId;


    public void forward () {
        if ((currentpage + 1) * moviesperpage < (watchlistModel.getWatchlistPosters().length)) {
            currentpage++;
            loadpage();
        }
    }

    public void back () {
        if (currentpage > 0) {
            currentpage--;
            loadpage();
        }
    }

    public List<String> watchlistAPI() {
        List<String> posters = new ArrayList<>();
        try {
            URL url = new URL("https://api.themoviedb.org/3/account/" + watchlistId +
                    "/watchlist/movies?language=en-US&page=1&sort_by=created_at.asc&session_id=" + sessionId);

            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            connect.setRequestProperty("Accept", "application/json");
            connect.setRequestProperty("Authorization", "Bearer " + apiKey);

            InputStream is = connect.getInputStream();
            JsonReader reader = Json.createReader(is);
            JsonObject json = reader.readObject();
            reader.close();

            JsonArray results = json.getJsonArray("results");

            for (int i = 0; i < results.size(); i++) {
                JsonObject result = results.getJsonObject(i);

                if (result.containsKey("poster_path") && !result.isNull("poster_path")) {
                    String path = result.getString("poster_path");
                    String fullUrl = "https://image.tmdb.org/t/p/w500" + path;
                    posters.add(fullUrl);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }        return posters;}

    public void loadpage() {
        String [] allPosters = watchlistModel.getWatchlistPosters();
        ArrayList <String> currentposters = new ArrayList<>();
        if ((watchlistModel.getWatchlistPosters().length - currentpage) < moviesperpage) {
            moviesperpage = (watchlistModel.getWatchlistPosters().length - currentpage);
        }
        for(int i = currentpage; i < (currentpage + moviesperpage); i++) {
            currentposters.add(allPosters[i]);
        }
    }

    public WatchlistController(WatchlistModel watchlistModel, String apiKey, String sessionId, String watchlistId) {
        this.watchlistModel = watchlistModel;
        this.apiKey = apiKey;
        this.sessionId = sessionId;
        this.watchlistId = watchlistId;

    }
}