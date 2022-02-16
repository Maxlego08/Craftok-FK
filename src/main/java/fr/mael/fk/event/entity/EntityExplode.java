package fr.mael.fk.event.entity;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.State;
import fr.mael.fk.handler.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;

public class EntityExplode extends FKListener {
    private long lastCheck = 0;

    public EntityExplode(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Step.isStep(Step.LOBBY) || event.getEntity() instanceof WitherSkull) {
            event.setCancelled(true);
            return;
        } else if (Step.isStep(Step.IN_GAME)) {
            if (!State.isState(State.ASSAULT) && !State.isState(State.DEATHMATCH)) {
                for (Block block : new ArrayList<>(event.blockList())) {
                    for (Team team : Team.values()) {
                        if (team == Team.SPEC || team.getCuboid() == null) {
                            continue;
                        }
                        Location loc = block.getLocation();
                        if (!team.getCuboid().contains(loc)) {
                            continue;
                        }
                        event.blockList().remove(block);
                        if (System.currentTimeMillis() - lastCheck >= 60000) {
                            lastCheck = System.currentTimeMillis();
                            Bukkit.broadcastMessage(Main.prefix + ChatColor.GOLD + "Rappel : " + ChatColor.GRAY + "Les assauts ne sont autorisés qu'à partir du " + ChatColor.YELLOW + "2ème jour à 3h.");
                        }
                    }
                }
            }
        }
    }
}
