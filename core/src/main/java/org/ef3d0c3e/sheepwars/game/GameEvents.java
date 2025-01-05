package org.ef3d0c3e.sheepwars.game;

import io.papermc.paper.event.player.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;

@WantsListen(phase = WantsListen.Target.Game)
public class GameEvents implements Listener {
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent ev) {
        ev.setFoodLevel(20);
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBedFailEnter(final PlayerBedFailEnterEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onFlowerPot(final PlayerFlowerPotManipulateEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onHarvest(final PlayerHarvestBlockEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onLectern(final PlayerInsertLecternBookEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameInteract(final PlayerItemFrameChangeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onRedstone(final BlockRedstoneEvent ev)
    {
        ev.setNewCurrent(0);
    }

    @EventHandler
    public void onInventoryInteract(final PlayerInteractEvent ev) {
        if (ev.getClickedBlock() == null) return;
        ev.setCancelled(true);
    }

    @EventHandler
    public void onOpenSign(final PlayerOpenSignEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onCraft(final CraftItemEvent ev) {
        ev.setCancelled(true);
    }
}
