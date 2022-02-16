package fr.mael.fk.event.player;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class PlayerAchievementAwarded extends FKListener {
    public PlayerAchievementAwarded(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerAchievementArwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
