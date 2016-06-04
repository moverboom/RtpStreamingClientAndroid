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
            //.notifyDataSetChanged on videoListAdapter in onPostExecute()
            setVideoArrayList();

            videoListAdapter.notifyDataSetChanged();



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
        startActivity(videoViewIntent);
    }

    /**
     * Fills the list with videos
     *
     * @throws Exception
     */
    private void setVideoArrayList() throws Exception {
        videoArrayList.add(new Video(1, "video 1", InetAddress.getByName("127.0.0.1"), 1234));
        videoArrayList.add(new Video(2, "video 2", InetAddress.getByName("127.0.0.1"), 1234));
        videoArrayList.add(new Video(3, "video 3", InetAddress.getByName("127.0.0.1"), 1234));
        videoArrayList.add(new Video(4, "video 4", InetAddress.getByName("127.0.0.1"), 1234));
        videoArrayList.add(new Video(5, "video 5", InetAddress.getByName("127.0.0.1"), 1234));
        videoArrayList.add(new Video(6, "video 6", InetAddress.getByName("127.0.0.1"), 1234));
    }
}
