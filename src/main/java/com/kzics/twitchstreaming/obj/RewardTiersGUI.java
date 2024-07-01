package com.kzics.twitchstreaming.obj;

import com.kzics.twitchstreaming.Main;
import com.kzics.twitchstreaming.listeners.RewardsHolder;
import com.kzics.twitchstreaming.obj.impl.Streamer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RewardTiersGUI extends RewardsHolder {
    private final Main plugin;
    private final Inventory gui;

    public RewardTiersGUI(Main plugin) {
        this.plugin = plugin;
         this.gui = Bukkit.createInventory(this, 27, Component.text("Reward Tiers"));
    }

    public void openGUI(Player player, Streamer streamer) {
        List<RewardTier> rewardTiers = plugin.getRewardTiers();
        String displayName = plugin.getConfig().getString("tier-items.display-name");
        List<String> loreTemplate = plugin.getConfig().getStringList("tier-items.lore");

        for (int i = 0; i < rewardTiers.size(); i++) {
            RewardTier tier = rewardTiers.get(i);
            long timeRequired = tier.getTime();
            Material reward = tier.getReward();
            int amount = tier.getAmount();

            ItemStack item;
            if (streamer.getClaimedTiers().contains(i)) {
                item = createItem(Material.GREEN_STAINED_GLASS_PANE, displayName, loreTemplate, i + 1, RewardStatus.ALREADY_CLAIMED, reward, amount, timeRequired);
            } else if (streamer.getTotalStreamingTime() >= timeRequired) {
                item = createItem(Material.YELLOW_STAINED_GLASS_PANE, displayName, loreTemplate, i + 1, RewardStatus.UNLOCKED, reward, amount, timeRequired);
            } else {
                item = createItem(Material.RED_STAINED_GLASS_PANE, displayName, loreTemplate, i + 1, RewardStatus.LOCKED, reward, amount, timeRequired);
            }
            gui.setItem(i, item);
        }

        player.openInventory(gui);
    }

    private ItemStack createItem(Material material, String displayNameTemplate, List<String> loreTemplate, int tier, RewardStatus status, Material reward, int amount, long time) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&',displayNameTemplate.replace("{tier}", String.valueOf(tier))
                .replace("{status}", status.toString()));
        meta.displayName(Component.text(displayName));

        List<TextComponent> lore = loreTemplate.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&',line.replace("{tier}", String.valueOf(tier))
                        .replace("{status}", status.asString())
                        .replace("{reward}", reward.name())
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{time}", formatTime(time))))
                .map(Component::text)
                .toList();

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private String formatTime(long totalMinutes) {
        long minutes = totalMinutes % 60;
        long hours = (totalMinutes / 60) % 24;
        long days = totalMinutes / (60 * 24);

        return String.format("%02d:%02d:%02d", days, hours, minutes);
    }

    public void handle(InventoryClickEvent event){
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;

        Streamer streamer = plugin.getStreamersManager().getStreamer(player.getUniqueId());
        List<RewardTier> rewardTiers = plugin.getRewardTiers();

        int slot = event.getSlot();
        if (slot < 0 || slot >= rewardTiers.size()) return;

        RewardTier tier = rewardTiers.get(slot);
        if (streamer.getClaimedTiers().contains(slot)) {
            player.sendMessage(Component.text("You have already claimed this reward.")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
        } else if (streamer.getTotalStreamingTime() >= tier.getTime()) {
            streamer.claimTier(slot);
            player.sendMessage(Component.text("You have claimed the reward: " + tier.getReward())
                    .color(net.kyori.adventure.text.format.NamedTextColor.GREEN));
            player.getInventory().addItem(new ItemStack(tier.getReward(),tier.getAmount()));
            new RewardTiersGUI(plugin).openGUI(player, streamer);
        } else {
            player.sendMessage(Component.text("You have not yet unlocked this tier.")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui;
    }
}
