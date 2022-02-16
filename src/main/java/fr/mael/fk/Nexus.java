package fr.mael.fk;

import fr.mael.fk.handler.Team;
import net.minecraft.server.v1_8_R3.EntityEnderCrystal;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;

public class Nexus
{
    private final Team team;
    private final Location location;
    private int health;

    public Nexus(Team team, Location location, int health)
    {
        this.team = team;
        this.location = location;
        this.health = health;

        EntityEnderCrystal entityNexus = new EntityEnderCrystal(((CraftWorld) location.getWorld()).getHandle());
        entityNexus.setPosition(location.getX(), location.getY(), location.getZ());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entityNexus);
        EnderCrystal nexus = (EnderCrystal) entityNexus.getBukkitEntity();
        nexus.setCustomName(team.getColor() + "§c§lNEXUS §r" + team.getColor() + team.getDisplayName() + " §7- §cVie » " + health);
        nexus.setCustomNameVisible(true);


    }

    public Team getTeam()
    {
        return this.team;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public int getHealth()
    {
        return this.health;
    }

    public void damage(int amount, EnderCrystal enderCrystal, boolean message) {
        this.health -= amount;
        if (this.health <= 0) {
            team.loose(enderCrystal);
        } else {
            for (Player online : team.getOnlinePlayers()) {
                online.playSound(online.getLocation(), Sound.WITHER_SPAWN, 1.0F, 0.5F);

                enderCrystal.setCustomName(team.getColor() + "§c§lNEXUS §r" + team.getColor() + team.getDisplayName() + " §7- §cVie » " + health);

                if(message == true) {
                    online.sendMessage(ChatColor.RED + "!!! Le nexus est attaqué !!! (" + health + "/" + 1000 + ")");
                }
            }
        }
    }

    public boolean isAlive()
    {
        return this.health > 0;
    }
}
