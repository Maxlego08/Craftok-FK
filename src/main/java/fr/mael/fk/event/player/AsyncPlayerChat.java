package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Team;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChat extends FKListener {
    public AsyncPlayerChat(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
        event.setFormat(playerTeam.getColor() + player.getName() + ChatColor.WHITE + ": " + event.getMessage());
        if (Step.isStep(Step.IN_GAME)) {
            if (playerTeam == Team.SPEC || !event.getMessage().startsWith("@")|| !event.getMessage().startsWith("!")) {
                if (playerTeam != Team.SPEC) {
                    event.setFormat("[" + playerTeam.getColor() + StringUtils.capitalize(playerTeam.getDisplayName()) + ChatColor.WHITE + "] " + ChatColor.RESET + event.getFormat());
                }
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Team team = Team.getPlayerTeam(online);
                    if (team != null && team != playerTeam) {
                        event.getRecipients().remove(online);
                    }
                }
            } else {
                event.setFormat("(Tous) " + ChatColor.RESET + event.getFormat().replaceFirst("@", "").replaceFirst("!", ""));
            }
        }
    }
}
