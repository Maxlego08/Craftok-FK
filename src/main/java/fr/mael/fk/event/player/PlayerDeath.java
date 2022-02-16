package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Score;

public class PlayerDeath extends FKListener {
    public PlayerDeath(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team playerTeam = Team.getPlayerTeam(player);
        if (!Step.isStep(Step.IN_GAME) || playerTeam == Team.SPEC) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }
        event.setDeathMessage(Main.prefix + event.getDeathMessage().replace(playerTeam.getColor() + player.getName(), playerTeam.getColor() + player.getName() + ChatColor.WHITE));
        //event.getEntity().spigot().respawn();
        if (player.getKiller() != null) {
            Score score = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("kills").getScore(player.getKiller());
            score.setScore(score.getScore() + 1);
        }
    }
}
