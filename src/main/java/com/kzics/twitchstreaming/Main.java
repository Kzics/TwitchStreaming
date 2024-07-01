package com.kzics.twitchstreaming;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.kzics.twitchstreaming.commands.RewardsTierCommand;
import com.kzics.twitchstreaming.commands.register.RegisterStreamer;
import com.kzics.twitchstreaming.commands.streamtime.StreamTimeCommand;
import com.kzics.twitchstreaming.listeners.InventoryClickListeners;
import com.kzics.twitchstreaming.manager.StreamersManager;
import com.kzics.twitchstreaming.manager.TwitchEventsListener;
import com.kzics.twitchstreaming.obj.RewardTier;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    private static TwitchClient twitchClient;
    private StreamersManager streamersManager;
    private static Main instance;
    private List<RewardTier> rewardTiers;

    @Override
    public void onEnable() {
        instance = this;

        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }

        if(!new File(getDataFolder(), "config.yml").exists()){
            try {
                copyStreamToFile(getResource("config.yml"), new File(getDataFolder(), "config.yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        streamersManager = new StreamersManager();
        File streamersFile = new File(getDataFolder(), "streamers.json");
        if (!streamersFile.exists()) {
            try {
                streamersFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        streamersManager.load(streamersFile);
        loadRewardTiers();

        TwitchEventsListener twitchEventsListener = new TwitchEventsListener(streamersManager);

        getCommand("streamtime").setExecutor(new StreamTimeCommand(streamersManager));
        getCommand("streamregister").setExecutor(new RegisterStreamer(streamersManager));
        getCommand("rewardtiers").setExecutor(new RewardsTierCommand(this));
        getServer().getPluginManager().registerEvents(new InventoryClickListeners(this), this);


        String clientId = getConfig().getString("clientId");
        String clientSecret = getConfig().getString("clientSecret");
        String oauth = getConfig().getString("oauthToken");

        OAuth2Credential credential = new OAuth2Credential("twitch", oauth);

        twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withChatAccount(credential)
                .withDefaultAuthToken(credential)
                .withEnableHelix(true)
                .withEnableEventSocket(true)
                .withEnableChat(true)
                .build();

        twitchClient.getClientHelper().enableStreamEventListener(streamersManager.getStreamerNames());

        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, twitchEventsListener::onStreamStart);
        twitchClient.getEventManager().onEvent(ChannelGoOfflineEvent.class, twitchEventsListener::onStreamEnd);
    }

    @Override
    public void onDisable() {
        streamersManager.save(new File(getDataFolder(), "streamers.json"));
    }
    private void loadRewardTiers() {
        rewardTiers = getConfig().getMapList("reward-tiers").stream()
                .map(map -> new RewardTier(
                        ((Number) map.get("time")).longValue(),
                        Material.valueOf((String) map.get("reward")),
                        ((Number) map.get("amount")).intValue()))
                .collect(Collectors.toList());
    }

    public List<RewardTier> getRewardTiers() {
        return rewardTiers;
    }

    public StreamersManager getStreamersManager() {
        return streamersManager;
    }

    public void copyStreamToFile(InputStream source, File destination) throws IOException {
        try (OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = source.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            if (source != null) {
                source.close();
            }
        }
    }

    public static TwitchClient getTwitchClient() {
        return twitchClient;
    }

    public static Main getInstance() {
        return instance;
    }
}