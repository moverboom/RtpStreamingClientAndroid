package com.matthijs.rtpstreamingclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by matthijs on 31-5-16.
 */
public class VideoListAdapter extends BaseAdapter {
    private ArrayList<Video> videoArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public VideoListAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Video> videoArrayList) {
        this.videoArrayList = videoArrayList;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return videoArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return videoArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VideoViewHolder videoViewHolder = new VideoViewHolder();

        if(view == null || view.getTag() == null) {
            view = layoutInflater.inflate(R.layout.row_layout, null);

            videoViewHolder.name = (TextView)view.findViewById(R.id.videoName);

            view.setTag(videoViewHolder);
        } else {
            videoViewHolder = (VideoViewHolder)view.getTag();
        }

        Video video = videoArrayList.get(i);
        videoViewHolder.name.setText(video.getName());

        return view;
    }

    private class VideoViewHolder {
        public TextView name;
    }
}
