package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Team;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove extends FKListener {

    public PlayerMove(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            if (to.getBlockY() <= 0) {
                Team team = Team.getPlayerTeam(player);
                if (Step.isStep(Step.LOBBY) || team == Team.SPEC) {
                    player.teleport(Step.isStep(Step.LOBBY) || team.getSpawnLocation() == null ? plugin.lobbyLocation : team.getSpawnLocation());
                }
            } else if (Step.isStep(Step.LOBBY) && plugin.lobbyLocation != null) {

                if(player.isOp()) {
                    return;
                }

                int x1 = plugin.lobbyLocation.getBlockX(), x2 = to.getBlockX();
                int z1 = plugin.lobbyLocation.getBlockZ(), z2 = to.getBlockZ();
                if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2)) > 25) {
                    player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez pas sortir de la salle d'attente.");
                    player.teleport(plugin.lobbyLocation);
                }
            }
        }
    }
}
