package fr.mael.fk.event;

import fr.mael.fk.Main;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import org.bukkit.event.Listener;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FKListener implements Listener {
    protected Main plugin;
}
