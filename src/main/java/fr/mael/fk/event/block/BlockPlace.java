package fr.mael.fk.event.block;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.State;
import fr.mael.fk.handler.Team;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace extends FKListener {

    public BlockPlace(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
        if (Step.isStep(Step.LOBBY) || playerTeam == Team.SPEC) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        } else if (Step.isStep(Step.IN_GAME)) {
            Material mat = event.getBlock().getType();
            Location loc = event.getBlock().getLocation();
            int x1 = plugin.lobbyLocation.getBlockX(), x2 = loc.getBlockX();
            int z1 = plugin.lobbyLocation.getBlockZ(), z2 = loc.getBlockZ();
            if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2)) <= 25) {
                player.damage(0.5);
                event.setCancelled(true);
                return;
            }
            boolean canPlaceOutdoor = mat == Material.TNT || mat.name().contains("TORCH") || mat == Material.SIGN || mat == Material.YELLOW_FLOWER || mat == Material.RED_ROSE || mat == Material.FIRE;
            if (playerTeam.getCuboid().contains(loc)) {
                if (playerTeam.getNexusLocation().distanceSquared(event.getBlock().getLocation()) < 8) {
                    event.setCancelled(true);
                    player.damage(0.5D);
                    player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez pas poser à moins de 3 blocs de votre nexus.");
                    return;
                }

                if (mat == Material.TNT) {
                    event.setCancelled(true);
                    player.damage(0.5D);
                    player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez pas poser des TNT dans votre nexus.");
                    return;
                }


            } else if (!canPlaceOutdoor) {
                event.setCancelled(true);
                player.damage(0.5D);
                player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez poser des blocs qu'à l'intérieur de votre base.");
            } else if (State.getState() != State.ASSAULT && State.getState() != State.DEATHMATCH) {
                if (canPlaceOutdoor && mat == Material.TNT) {
                    event.setCancelled(true);
                    player.damage(0.5D);
                    player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez pas placer de TNT avant l'assaut.");
                } else if (canPlaceOutdoor) {
                    for (Team team : Team.values()) {
                        if (team == Team.SPEC || team == playerTeam || team.getCuboid() == null) {
                            continue;
                        } else if (team.getCuboid().contains(loc)) {
                            event.setCancelled(true);
                            player.damage(0.5D);
                            player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez pas poser de blocs dans les bases ennemies.");
                            break;
                        }
                    }
                }
            }
        }
    }
}
