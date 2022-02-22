package fr.mael.fk.event.entity;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChange extends FKListener {
    public FoodLevelChange(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(Step.isStep(Step.LOBBY));
    }
}
