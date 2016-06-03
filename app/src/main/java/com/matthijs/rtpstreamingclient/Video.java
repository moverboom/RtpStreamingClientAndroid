package com.matthijs.rtpstreamingclient;

import java.net.InetAddress;

/**
 * Created by matthijs on 31-5-16.
 */
public class Video {
    private int id;
    private String name;
    private InetAddress ip;

    public Video(int id, String name, InetAddress ip) {
        this.id = id;
        this.name = name;
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
}
