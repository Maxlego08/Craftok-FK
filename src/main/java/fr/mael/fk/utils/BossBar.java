package fr.mael.fk.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BossBar {

    private Player player;
    private String message;
    private EntityWither wither;
    private float live;

    public BossBar(Player player, String message, float live){
        this.player = player;
        this.message = message;
        this.live = live;
        update(player, message, live);
    }

    public void update(Player player, String message, float live) {
        Vector vector = player.getLocation().getDirection();
        Location location = player.getLocation().add(vector.multiply(20));
        removeWither();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        wither = new EntityWither(world);
        int y = -15;
        if (player.getLocation().getPitch() < 0){
            y = 15;
        }
        wither.setLocation(location.getX(), player.getLocation().getY() + y, location.getZ(), location.getPitch(), location.getYaw());
        wither.setCustomName(message);
        wither.setCustomNameVisible(true);
        wither.setHealth(live * wither.getMaxHealth() / 100);
        wither.setInvisible(true);
        wither.r(10000000);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removeWither() {
        if (wither != null){
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}