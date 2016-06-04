package com.matthijs.rtpstreamingclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Matthijs Overboom on 31-5-16.
 */
public class VideoListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    VideoListAdapter videoListAdapter;
    ArrayList<Video> videoArrayList = new ArrayList<Video>();
    LayoutInflater layoutInflater;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceStage) {
        this.layoutInflater = layoutInflater;
        return layoutInflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        videoListAdapter = new VideoListAdapter(getContext(), layoutInflater, videoArrayList);

        try {

            //Call Async task here which fetches videos from REST API
            // TODO: check even of alles zo naar wens is meune freund.
            AsyncMovieLoaderTask asyncMovieLoaderTask = new AsyncMovieLoaderTask(videoListAdapter, videoArrayList);
            asyncMovieLoaderTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setListAdapter(videoListAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("RTP", "Item clicked: " + i);
        Intent videoViewIntent = new Intent(this.getContext(), VideoActivity.class);
        videoViewIntent.putExtra("VIDEO", (Video)videoListAdapter.getItem(i));
        startActivity(videoViewIntent);
    }
}
