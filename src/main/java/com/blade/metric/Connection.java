package com.blade.metric;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class Connection implements Serializable {
    private long count = 1;
    private final long connectionId = 0;
    private LocalDateTime established;
    private LocalDateTime closed;
    private String ip;

    private Set<String> uris = new HashSet<>();

    private long bytesSent;
    private long bytesReceived;

    public synchronized String getUrisAsString() {
        StringBuilder sb = new StringBuilder();
        uris.forEach((uri) -> sb.append(uri).append(", "));
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    public synchronized double getSpeed() {
        double connectionDuration = ChronoUnit.MILLIS.between(established, closed);
        connectionDuration /= 1000; // to seconds

        /* Round to 3 decimal places. */
        return Math.round((((double) bytesSent + (double) bytesReceived) / connectionDuration) * 1000.0) / 1000.0;
    }

    public synchronized long getConnectionId() {
        return connectionId;
    }

    public synchronized long getBytesReceived() {
        return bytesReceived;
    }

    public synchronized void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public synchronized LocalDateTime getEstablished() {
        return established;
    }

    public synchronized void setEstablished(LocalDateTime established) {
        this.established = established;
    }

    public synchronized LocalDateTime getClosed() {
        return closed;
    }

    public synchronized void setClosed(LocalDateTime closed) {
        this.closed = closed;
    }

    public synchronized String getIp() {
        return ip;
    }

    public synchronized void setIp(String ip) {
        this.ip = ip;
    }

    public synchronized Set<String> getUris() {
        return uris;
    }

    public synchronized void addUri(String uri) {
        if (uri != null) {
            uris.add(uri);
        }
    }

    public synchronized long getBytesSent() {
        return bytesSent;
    }

    public synchronized void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (bytesReceived != that.bytesReceived) return false;
        if (bytesSent != that.bytesSent) return false;
        if (connectionId != that.connectionId) return false;
        if (closed != null ? !closed.equals(that.closed) : that.closed != null) return false;
        if (established != null ? !established.equals(that.established) : that.established != null) return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (uris != null ? !uris.equals(that.uris) : that.uris != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (connectionId ^ (connectionId >>> 32));
        result = 31 * result + (established != null ? established.hashCode() : 0);
        result = 31 * result + (closed != null ? closed.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (uris != null ? uris.hashCode() : 0);
        result = 31 * result + (int) (bytesSent ^ (bytesSent >>> 32));
        result = 31 * result + (int) (bytesReceived ^ (bytesReceived >>> 32));
        return result;
    }
}
