package fr.mael.fk.event.player;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit extends FKListener {
    public PlayerQuit(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " a quitter la partie " + ChatColor.GREEN + "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
        player.getInventory().clear();
        plugin.removePlayer(player);
    }
}
