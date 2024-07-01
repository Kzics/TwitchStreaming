package com.kzics.twitchstreaming.commands.streamtime;

import com.kzics.twitchstreaming.commands.CommandBase;
import com.kzics.twitchstreaming.manager.StreamersManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StreamTimeCommand extends CommandBase {

    private StreamersManager streamerManager;
    public StreamTimeCommand(StreamersManager streamersManager){
        this.streamerManager = streamersManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /streamtime <username>")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return false;
        }

        String username = args[0];
        Component result = streamerManager.getStreamTime(username);

        if (sender instanceof Player) {
            sender.sendMessage(result);
        } else {
            System.out.println(result);
        }

        return true;
    }
}
