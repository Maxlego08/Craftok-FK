package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;

import fr.mael.fk.handler.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage extends FKListener {
    public PlayerDamage(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!Step.isStep(Step.IN_GAME) || Team.getPlayerTeam((Player) event.getEntity()) == Team.SPEC) {
                event.setCancelled(true);
            }
        }
    }
}
