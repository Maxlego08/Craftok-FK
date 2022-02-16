package fr.mael.fk.event.block;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFadeEvent;

public class BlockFade extends FKListener {
    public BlockFade(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            event.setCancelled(true);
        }
    }
}
