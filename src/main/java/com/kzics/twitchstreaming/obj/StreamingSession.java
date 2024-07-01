package com.kzics.twitchstreaming.obj;

import java.time.LocalDateTime;

public class StreamingSession {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public StreamingSession(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "StreamingSession{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}