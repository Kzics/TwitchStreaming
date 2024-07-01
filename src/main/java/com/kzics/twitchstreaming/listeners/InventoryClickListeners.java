package com.kzics.twitchstreaming.listeners;

import com.kzics.twitchstreaming.Main;
import com.kzics.twitchstreaming.obj.RewardTier;
import com.kzics.twitchstreaming.obj.RewardTiersGUI;
import com.kzics.twitchstreaming.obj.impl.Streamer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListeners implements org.bukkit.event.Listener{

    private final Main plugin;
    public InventoryClickListeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof RewardsHolder)) return;

        RewardTiersGUI handler = (RewardTiersGUI) event.getClickedInventory().getHolder();
        handler.handle(event);
    }
}
