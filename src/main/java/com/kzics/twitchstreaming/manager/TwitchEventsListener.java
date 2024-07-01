package com.kzics.twitchstreaming.manager;

import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.kzics.twitchstreaming.obj.impl.Streamer;

public class TwitchEventsListener {

    private final StreamersManager streamersManager;
    public TwitchEventsListener(final StreamersManager streamersManager){
        this.streamersManager = streamersManager;

    }


    public void onStreamStart(ChannelGoLiveEvent event){
        Streamer streamer = streamersManager.getStreamer(event.getChannel().getName());
        if(streamer == null) return;
        streamer.startStreaming();

    }

    public void onStreamEnd(ChannelGoOfflineEvent event){
        Streamer streamer = streamersManager.getStreamer(event.getChannel().getName());
        if(streamer == null) return;
        streamer.stopStreaming();
    }
}
