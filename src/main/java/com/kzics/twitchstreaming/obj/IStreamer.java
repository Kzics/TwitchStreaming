package com.kzics.twitchstreaming.obj;

import java.util.List;
import java.util.UUID;

public interface IStreamer {
    UUID getUUID();
    String getChannelName();
    void setChannelName(String channelName);
    boolean isStreaming();
    void startStreaming();
    void stopStreaming();
    long getTotalStreamingTime();
    List<StreamingSession> getStreamingSessions();
}
