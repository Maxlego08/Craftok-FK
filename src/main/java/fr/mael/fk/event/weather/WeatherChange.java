package fr.mael.fk.event.weather;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChange extends FKListener {
    public WeatherChange(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        World world = event.getWorld();
        if (!world.isThundering() && !world.hasStorm()) {
            event.setCancelled(true);
        }
    }
}
