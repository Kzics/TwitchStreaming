package com.kzics.twitchstreaming.obj;

import org.bukkit.Material;

public class RewardTier {
    private final long time; // in minutes
    private final Material reward;
    private final int amount;

    public RewardTier(long time, Material reward, int amount) {
        this.time = time;
        this.reward = reward;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public long getTime() {
        return time;
    }

    public Material getReward() {
        return reward;
    }


}
