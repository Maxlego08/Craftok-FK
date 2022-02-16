package fr.mael.fk.event.player;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKick extends FKListener {
    public PlayerKick(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
        Player player = event.getPlayer();
        player.getInventory().clear();
        plugin.removePlayer(player);
    }
}
