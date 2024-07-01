package com.kzics.twitchstreaming.obj.impl;

import com.kzics.twitchstreaming.Main;
import com.kzics.twitchstreaming.obj.IStreamer;
import com.kzics.twitchstreaming.obj.StreamingSession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Streamer implements IStreamer {
    private final UUID uuid;
    private String channelName;
    private boolean streaming;
    private LocalDateTime streamingStartTime;
    private long totalStreamingTime;
    private final Set<Integer> claimedTiers;
    private final List<StreamingSession> streamingSessions;

    public Streamer(UUID uuid, String channelName) {
        this.uuid = uuid;
        this.channelName = channelName;
        this.streaming = false;
        this.totalStreamingTime = 0;
        this.streamingSessions = new ArrayList<>();
        this.claimedTiers = new HashSet<>();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public void claimTier(int tier) {
        claimedTiers.add(tier);
    }

    public Set<Integer> getClaimedTiers() {
        return claimedTiers;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public boolean isStreaming() {
        return streaming;
    }


    @Override
    public void startStreaming() {
        this.streaming = true;
        this.streamingStartTime = LocalDateTime.now();

        String message = ChatColor.translateAlternateColorCodes('&',Main.getInstance().getConfig().getString("start-stream")
                .replace("{channel}", channelName)
                .replace("{url}", "twitch.tv/" + channelName));
        Bukkit.broadcastMessage(message);
    }

    @Override
    public void stopStreaming() {
        this.streaming = false;
        long sessionDuration = Duration.between(streamingStartTime, LocalDateTime.now()).toMinutes();
        totalStreamingTime += sessionDuration;
        streamingSessions.add(new StreamingSession(streamingStartTime, LocalDateTime.now()));
        String message = ChatColor.translateAlternateColorCodes('&',Main.getInstance().getConfig().getString("stop-stream")
                .replace("{channel}", channelName)
                .replace("{url}", "twitch.tv/" + channelName));
        Bukkit.broadcastMessage(message);
    }

    @Override
    public long getTotalStreamingTime() {
        return totalStreamingTime;
    }

    @Override
    public List<StreamingSession> getStreamingSessions() {
        return streamingSessions;
    }

    public String getCurrentStreamingTimeFormatted() {
        long currentStreamingTime = getCurrentStreamingTime();
        long hours = currentStreamingTime / 60;
        long minutes = currentStreamingTime % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
    public long getCurrentStreamingTime() {
        if (streaming && streamingStartTime != null) {
            return Duration.between(streamingStartTime, LocalDateTime.now()).toMinutes();
        }
        return 0;
    }
}