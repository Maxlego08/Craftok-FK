package fr.mael.fk.event.entity;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityTarget extends FKListener {

    public EntityTarget(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityTargetByEntity(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && Team.getPlayerTeam((Player) event.getTarget()) == Team.SPEC) {
            event.setCancelled(true);
        }
    }
}
