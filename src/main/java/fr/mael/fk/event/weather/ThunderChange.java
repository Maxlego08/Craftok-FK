package fr.mael.fk.event.weather;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.ThunderChangeEvent;

public class ThunderChange extends FKListener {
    public ThunderChange(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        event.setCancelled(event.toThunderState());
    }
}
