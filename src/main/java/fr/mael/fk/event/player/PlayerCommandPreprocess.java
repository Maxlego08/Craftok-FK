package fr.mael.fk.event.player;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocess extends FKListener {
    public PlayerCommandPreprocess(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && event.getMessage().split(" ")[0].equalsIgnoreCase("/reload")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Cette fonctionnalité est désactivée par le plugin Fallen Kingdoms à cause de contraintes techniques (reset de map).");
        }
    }
}
