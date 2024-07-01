package com.kzics.twitchstreaming.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kzics.twitchstreaming.LocalDateTimeAdapter;
import com.kzics.twitchstreaming.obj.impl.Streamer;
import com.kzics.twitchstreaming.obj.StreamingSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StreamersManager {
    private Map<String, Streamer> streamers;
    private final Gson gson;

    public StreamersManager() {
        this.streamers = new HashMap<>();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void addStreamer(Streamer streamer) {
        streamers.put(streamer.getChannelName(), streamer);
    }

    public Streamer getStreamer(String name) {
        return streamers.get(name);
    }

    public Streamer getStreamer(UUID uuid) {
        for (Streamer streamer : streamers.values()) {
            if (streamer.getUUID().equals(uuid)) {
                return streamer;
            }
        }
        return null;
    }

    public Component getStreamTime(String username) {
        Streamer streamer = this.getStreamer(username);
        if (streamer == null) {
            return Component.text("Streamer not found");
        }
        return formatStreamingTime(streamer);
    }

    public List<String> getStreamerNames() {
        return List.copyOf(streamers.keySet());
    }
    private Component formatStreamingTime(Streamer streamer) {
        long totalStreamingTime = streamer.getTotalStreamingTime();
        long days = totalStreamingTime / (60 * 24);
        long hours = (totalStreamingTime % (60 * 24)) / 60;
        long minutes = totalStreamingTime % 60;

        StreamingSession lastSession = !streamer.getStreamingSessions().isEmpty() ?
                streamer.getStreamingSessions().get(streamer.getStreamingSessions().size() - 1) : null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        TextComponent.Builder timeComponent = Component.text()
                .append(Component.text("Total streaming time: ")
                        .color(NamedTextColor.GOLD))
                .append(Component.text(days + " days " + hours + " hours and " + minutes + " minutes\n")
                        .color(NamedTextColor.WHITE))
                .append(Component.text("Status: ")
                        .color(NamedTextColor.GOLD))
                .append(Component.text(!streamer.isStreaming() ? "❌" : "✔")
                        .color(streamer.isStreaming() ? NamedTextColor.GREEN : NamedTextColor.RED))
                .append(Component.text("\n"));

        if (streamer.isStreaming()) {
            long currentStreamingTime = streamer.getCurrentStreamingTime();
            long currentHours = currentStreamingTime / 60;
            long currentMinutes = currentStreamingTime % 60;

            timeComponent.append(Component.text("Current live session: ")
                            .color(NamedTextColor.GOLD))
                    .append(Component.text(currentHours + " hours and " + currentMinutes + " minutes\n")
                            .color(NamedTextColor.WHITE));
        }

        if (lastSession != null) {
            timeComponent.append(Component.text("Last session start: ")
                            .color(NamedTextColor.GOLD))
                    .append(Component.text(lastSession.getStartTime().format(formatter) + "\n")
                            .color(NamedTextColor.WHITE))
                    .append(Component.text("Last session end: ")
                            .color(NamedTextColor.GOLD))
                    .append(Component.text(lastSession.getEndTime().format(formatter))
                            .color(NamedTextColor.WHITE));
        }

        return timeComponent.build();
    }

    public void load(File file) {
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Streamer>>() {}.getType();
            Map<String, Streamer> loadedStreamers = gson.fromJson(reader, type);
            this.streamers = (loadedStreamers != null) ? loadedStreamers : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(File file) {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(this.streamers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}