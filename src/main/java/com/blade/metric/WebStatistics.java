package com.blade.metric;

import com.blade.kit.DateKit;
import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WebStatistics implements Serializable {

    private ConcurrentHashMap<String, Metric> ipRequests = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> redirects = new ConcurrentHashMap<>();
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

    private transient DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private WebStatistics() {
    }

    private static final class WebStatisticsHolder {
        private static final WebStatistics INSTANCE = new WebStatistics();
    }

    public static WebStatistics me() {
        return WebStatisticsHolder.INSTANCE;
    }

    public synchronized long getNumberOfRequests() {
        return ipRequests != null && ipRequests.size() != 0 ? ipRequests.reduceValues(1, Metric::count, Long::sum) : 0;
    }

    public synchronized void registerRequestFromIp(String ip, LocalDateTime localDateTime) {
        if (ipRequests.containsKey(ip)) {
            ipRequests.put(ip, ipRequests.get(ip).plus(1).time(localDateTime));
        } else {
            ipRequests.put(ip, new Metric(1L, localDateTime));
        }
    }

    public synchronized void registerRedirect(String destinationUrl) {
        if (redirects.containsKey(destinationUrl)) {
            redirects.put(destinationUrl, redirects.get(destinationUrl) + 1);
        } else {
            redirects.put(destinationUrl, 1L);
        }
    }

    public static String getIpFromChannel(Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().replaceFirst("^/", "");
    }

    public List<List<String>> getIpRequestsAsStrings() {
        List<List<String>> list = new ArrayList<>();
        ipRequests.forEach((ip, info) -> {
            List<String> s = Arrays.asList(ip, info.count() + "", DateKit.toString(info.time()));
            list.add(s);
        });
        return list;
    }

    public List<List<String>> getRedirectsAsStrings() {
        List<List<String>> list = new ArrayList<>();
        redirects.forEach((url, count) -> {
            List<String> s = Arrays.asList(url, count.toString());
            list.add(s);
        });
        return list;
    }

    public List<List<String>> getConnectionsAsStrings() {
        List<List<String>> list = new ArrayList<>();
        connections.forEach((ci) -> {
            List<String> s = new ArrayList<>();
            s.add(ci.getIp());
            s.add(ci.getUrisAsString());
            s.add(DateKit.toString(ci.getEstablished()));
            if (ci.getClosed() != null) {
                s.add(DateKit.toString(ci.getClosed()));
                s.add("" + ci.getBytesSent());
                s.add("" + ci.getBytesReceived());
                s.add(String.format("%.3f", ci.getSpeed()));
            } else {
                s.add("–");
                s.add("–");
                s.add("–");
                s.add("–");
            }
            list.add(s);
        });
        return list;
    }

    public long getNumberOfUniqueRequests() {
        return ipRequests.size();
    }

    public void addChannel(Channel c) {
        channels.add(c);
    }

    public int getConnectionCount() {
        return channels.size();
    }

    public synchronized void addConnectionInfo(Connection ci) {
        /* Store no more than 16 entries */
        if (connections.size() == 16) {
            connections.remove(0);
        }
        connections.add(ci);
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void serialize() {
        try (FileOutputStream fileOut = new FileOutputStream("WEB_STATISTICS.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebStatistics that = (WebStatistics) o;

        if (ipRequests != null ? !ipRequests.equals(that.ipRequests) : that.ipRequests != null) return false;
        if (redirects != null ? !redirects.equals(that.redirects) : that.redirects != null) return false;
        if (connections != null ? !connections.equals(that.connections) : that.connections != null) return false;
        if (channels != null ? !channels.equals(that.channels) : that.channels != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ipRequests != null ? ipRequests.hashCode() : 0;
        result = 31 * result + (redirects != null ? redirects.hashCode() : 0);
        result = 31 * result + (connections != null ? connections.hashCode() : 0);
        result = 31 * result + (channels != null ? channels.hashCode() : 0);
        return result;
    }

}
