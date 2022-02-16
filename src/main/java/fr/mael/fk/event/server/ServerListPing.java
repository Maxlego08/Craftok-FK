package fr.mael.fk.event.server;

import fr.mael.fk.handler.Step;
import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.State;

import fr.mael.fk.timer.TaskGame;
import fr.mael.fk.timer.TaskStart;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPing extends FKListener {
    public ServerListPing(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            if (TaskStart.started) {
                int remainingMins = TaskStart.timeUntilStart / 60 % 60;
                int remainingSecs = TaskStart.timeUntilStart % 60;
                event.setMotd(ChatColor.GREEN + "Début : " + (remainingMins > 0 ? remainingMins + "m" : remainingSecs + "s"));
            } else {
                event.setMotd(Step.getMOTD());
            }
        } else if (Step.isStep(Step.IN_GAME)) {
            event.setMotd(ChatColor.YELLOW + "Jours" + TaskGame.day + ">" + State.getState().getName());
        } else {
            event.setMotd(Step.getMOTD());
        }
    }
}
