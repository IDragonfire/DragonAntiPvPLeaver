package com.github.idragonfire.DragonAntiPvPLeaver.npclib;

import net.minecraft.server.v1_6_R3.Entity;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HumanNPC {
    private Entity entity;
    private int _DroppedExp = 0;

    public HumanNPC(NPCEntity entity) {
        this.entity = entity;
    }

    public void removeFromWorld() {
        try {
            entity.world.removeEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CraftEntity getBukkitEntity() {
        return entity.getBukkitEntity();
    }

    public void setItemInHand(Material m) {
        setItemInHand(m, (short) 0);
    }

    public void setItemInHand(Material m, short damage) {
        ((HumanEntity) getEntity().getBukkitEntity())
                .setItemInHand(new ItemStack(m, 1, damage));
    }

    public String getName() {
        return ((NPCEntity) getEntity()).getName();
    }

    public PlayerInventory getInventory() {
        return ((HumanEntity) getEntity().getBukkitEntity()).getInventory();
    }

    public int getDroppedExp() {
        return _DroppedExp;
    }

    public void setDroppedExp(int exp) {
        _DroppedExp = exp;
    }

    public Entity getEntity() {
        return entity;
    }
}
