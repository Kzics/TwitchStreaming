package com.kzics.twitchstreaming.obj;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum RewardStatus {

    LOCKED(NamedTextColor.RED,"Locked"),
    UNLOCKED(NamedTextColor.GREEN,"Unlocked"),
    ALREADY_CLAIMED(NamedTextColor.GRAY,"Already claimed");

    private NamedTextColor color;
    private String status;
    RewardStatus(NamedTextColor color, String status){
        this.color = color;
        this.status = status;
    }

    public Component toComponent() {
        return Component.text(status).color(color);
    }

    public String asString() {
        return status;
    }

    public static RewardStatus fromString(String status) {
        switch (status) {
            case "LOCKED":
                return LOCKED;
            case "UNLOCKED":
                return UNLOCKED;
            default:
                return null;
        }
    }
}
