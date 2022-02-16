package fr.mael.fk.event.server;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerCommand extends FKListener {

    public ServerCommand(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().split(" ")[0].contains("reload")) {
            event.setCommand("/reload");
            event.getSender().sendMessage(ChatColor.RED + "Cette fonctionnalité est désactivée par le plugin Fallen Kingdoms à cause de contraintes techniques (reset de map).");
        }
    }
}
