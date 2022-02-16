package fr.mael.fk.event.player;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Step;
import fr.mael.fk.handler.Team;
import fr.mael.fk.scoreboard.Scoreboard;
import fr.mael.fk.timer.TaskStart;
import fr.mael.fk.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoin extends FKListener {

    public PlayerJoin(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Scoreboard.createScoreBoard(player);

        player.getInventory().clear();
        if (!Step.isStep(Step.LOBBY) && player.hasPermission("games.join")) {
            plugin.setSpectator(player, false);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            player.teleport(Team.SPEC.getSpawnLocation() == null ? plugin.lobbyLocation : Team.SPEC.getSpawnLocation());
            player.setFlying(true);
        } else if (Step.isStep(Step.LOBBY)) {
            event.setJoinMessage(Main.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " a rejoint la partie " + ChatColor.GREEN + "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
            for (Team team : Team.values()) {
                if (team != Team.SPEC && team.getSpawnLocation() != null && team.getCuboid() != null) {
                    player.getInventory().addItem(team.getIcon());
                }
            }
            plugin.loadData(player);
            player.getInventory().setItem(8, new ItemBuilder(Material.NAME_TAG).setTitle(ChatColor.GOLD + "Kits " + ChatColor.GRAY + "(Clic-droit)").build());
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(plugin.lobbyLocation);

            // TODO
            if (Bukkit.getOnlinePlayers().size() >= 7 && !TaskStart.started) {
                for (Team team : Team.values()) {
                    if (team != Team.SPEC && (team.getSpawnLocation() == null || team.getCuboid() == null)) {
                        TaskStart.started = true;
                        return;
                    }
                }
                new TaskStart(plugin);
            }
        }
    }
}
