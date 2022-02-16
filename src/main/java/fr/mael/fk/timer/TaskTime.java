package fr.mael.fk.timer;

import fr.mael.fk.Main;

import org.bukkit.scheduler.BukkitRunnable;

public class TaskTime extends BukkitRunnable {
    private Main plugin;

    public TaskTime(Main plugin) {
        this.plugin = plugin;
        this.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public void run() {
        long time = plugin.world.getTime();
        if (time < 6000 || time >= 23000) {
            plugin.world.setTime(time + 5);
        } else if (time >= 6000 && time < 11000) {
            plugin.world.setTime(time + 3);
        } else {
            plugin.world.setTime(time + 4);
        }
    }
}
