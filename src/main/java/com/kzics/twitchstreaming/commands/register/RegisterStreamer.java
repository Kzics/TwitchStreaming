package com.kzics.twitchstreaming.commands.register;

import com.kzics.twitchstreaming.Main;
import com.kzics.twitchstreaming.commands.CommandBase;
import com.kzics.twitchstreaming.manager.StreamersManager;
import com.kzics.twitchstreaming.obj.impl.Streamer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterStreamer extends CommandBase {

    private StreamersManager streamersManager;
    public RegisterStreamer(StreamersManager streamersManager) {
        this.streamersManager = streamersManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /register <username>")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return false;
        }
        Player player = (Player) sender;

        if(streamersManager.getStreamer(player.getUniqueId()) != null){
            sender.sendMessage(Component.text("You are already registered as a streamer!")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return false;
        }

        String username = args[0];

        if(streamersManager.getStreamer(username) != null){
            sender.sendMessage(Component.text("Streamer already registered!")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return false;
        }

        Streamer streamer = new Streamer(player.getUniqueId(), username);
        streamersManager.addStreamer(streamer);

        sender.sendMessage(Component.text("Streamer registered successfully!")
                .color(net.kyori.adventure.text.format.NamedTextColor.GREEN));

        Main.getTwitchClient().getClientHelper().enableStreamEventListener(streamersManager.getStreamerNames());

        return false;
    }
}
