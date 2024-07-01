package com.kzics.twitchstreaming.commands;

import com.kzics.twitchstreaming.Main;
import com.kzics.twitchstreaming.obj.RewardTiersGUI;
import com.kzics.twitchstreaming.obj.impl.Streamer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardsTierCommand extends CommandBase{

    private final Main plugin;
    public RewardsTierCommand(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {
            Streamer streamer = plugin.getStreamersManager().getStreamer(player.getUniqueId());

            if (streamer != null) {
                new RewardTiersGUI(plugin).openGUI(player, streamer);
            } else {
                player.sendMessage(Component.text("You are not a registered streamer.")
                        .color(NamedTextColor.RED));
            }
        }
        return true;
    }
}
