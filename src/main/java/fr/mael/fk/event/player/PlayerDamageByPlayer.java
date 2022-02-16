package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.State;
import fr.mael.fk.handler.Team;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageByPlayer extends FKListener {

    public PlayerDamageByPlayer(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) {
            Team playerTeam = Team.getPlayerTeam((Player) event.getEntity());
            Player damager = (Player) (event.getDamager() instanceof Projectile ? ((Projectile) event.getDamager()).getShooter() : event.getDamager());
            Team damagerTeam = Team.getPlayerTeam(damager);
            if (!Step.isStep(Step.IN_GAME) || playerTeam == Team.SPEC || damagerTeam == Team.SPEC || damagerTeam == playerTeam) {
                event.setCancelled(true);
            } else {
                if (!State.getState().isPvp()) {
                    event.setCancelled(true);
                    damager.playSound(damager.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                    damager.sendMessage(Main.prefix + "Vous devez attendre 12h avant de pouvoir vous battre.");
                }
            }
        }
    }
}
