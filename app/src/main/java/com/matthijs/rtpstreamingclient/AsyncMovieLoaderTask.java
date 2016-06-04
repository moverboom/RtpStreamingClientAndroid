package com.matthijs.rtpstreamingclient;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Bram on 4-6-2016.
 */
public class AsyncMovieLoaderTask extends AsyncTask<Void, String,Void>  {

    // Attributes
    private static final String TAG = "AsyncMovieLoaderTask";
    private static final String DISCOVERY_SERVER_URL = "http://192.168.178.16/discoveryserver/movie/list/";
    private HttpURLConnection urlConnection;
    private ArrayList<Video> videoList;
    private VideoListAdapter videoListAdapter;

    // Constructor
    public AsyncMovieLoaderTask(VideoListAdapter adapter, ArrayList<Video> videoList) {
        this.videoListAdapter = adapter;
        this.videoList = videoList;
    }

    // Methods

    @Override
    protected Void doInBackground(Void... params) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(DISCOVERY_SERVER_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        Log.i("Result: " , result.toString());

        try {
            // Get all movies from json array
            JSONArray jsonMovies = new JSONArray(result.toString());

            // Loop through all movies and create a movie object from it
            for(int i = 0; i < jsonMovies.length(); i++ ) {
                JSONObject jsonMovieObj = jsonMovies.getJSONObject(i);
                int id = jsonMovieObj.getInt("movieID");
                String name = jsonMovieObj.getString("movieName");
                String ip = jsonMovieObj.getString("ipAddress");
                int port = jsonMovieObj.getInt("portNumber");

                // Initialize movie object
                Video video = new Video(id, name,  InetAddress.getByName(ip), port);

                // Add movie to list
                videoList.add(video);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // Update video list adapter
        videoListAdapter.notifyDataSetChanged();
    }
}
