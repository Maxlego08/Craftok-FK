package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;

import fr.mael.fk.handler.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerRespawn extends FKListener {
    public PlayerRespawn(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
        if (!Step.isStep(Step.LOBBY)) {
            if (playerTeam == Team.SPEC) {
                event.setRespawnLocation(playerTeam.getSpawnLocation() == null ? plugin.lobbyLocation : playerTeam.getSpawnLocation());
                player.setMaxHealth(20);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setFlying(true);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                    }
                }.runTaskLater(plugin, 1);
            } else {
                event.setRespawnLocation(playerTeam.getSpawnLocation());
                player.setMaxHealth(20);
            }
        } else {
            event.setRespawnLocation(plugin.lobbyLocation);
            player.setMaxHealth(20);
        }
    }
}
