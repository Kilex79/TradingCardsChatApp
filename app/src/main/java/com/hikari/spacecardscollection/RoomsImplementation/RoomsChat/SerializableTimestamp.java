package com.hikari.spacecardscollection.RoomsImplementation.RoomsChat;

import java.io.Serializable;

public class SerializableTimestamp implements Serializable {
    private long seconds;
    private int nanoseconds;

    public SerializableTimestamp(long seconds, int nanoseconds) {
        this.seconds = seconds;
        this.nanoseconds = nanoseconds;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    public void setNanoseconds(int nanoseconds) {
        this.nanoseconds = nanoseconds;
    }
}
