package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;

import fr.mael.fk.handler.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItem extends FKListener {
    public PlayerPickupItem(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (Step.isStep(Step.LOBBY) || Team.getPlayerTeam(event.getPlayer()) == Team.SPEC) {
            event.setCancelled(true);
        }
    }
}
