package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerLogin extends FKListener {
    public PlayerLogin(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (Step.canJoin() && event.getResult() == Result.KICK_FULL && player.hasPermission("games.vip")) {
            event.allow();
        } else if (!Step.canJoin() && !player.hasPermission("games.join")) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Step.getMOTD());
        }
    }
}
