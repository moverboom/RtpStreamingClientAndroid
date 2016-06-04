package com.matthijs.rtpstreamingclient;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Represents a video
 * Contains all data needed to request a video from a server
 *
 * Created by Matthijs Overboom on 31-5-16.
 */
public class Video implements Serializable {
    /**
     * Video id
     */
    private int id;

    /**
     * Video name
     */
    private String name;

    /**
     * Server ip
     */
    private InetAddress ip;

    /**
     * Server port for RTSP connection
     */
    private int port;

    public Video(int id, String name, InetAddress ip, int port) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    /**
     * Returns id
     *
     * @return int id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id
     *
     * @param id int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns video name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets video name
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns server IP
     *
     * @return InetAddress ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * Sets server IP
     *
     * @param ip InetAddress
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    /**
     * Returns port number for RTSP connection
     *
     * @return int port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets port number for RTSP connection
     *
     * @param port int
     */
    public void setPort(int port) {
        this.port = port;
    }
}
