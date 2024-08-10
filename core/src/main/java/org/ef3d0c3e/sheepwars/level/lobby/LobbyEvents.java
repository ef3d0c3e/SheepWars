package org.ef3d0c3e.sheepwars.level.lobby;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.npc.NPC;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.ef3d0c3e.sheepwars.events.*;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.items.IItem;
import org.ef3d0c3e.sheepwars.player.skin.SkinItem;

import java.util.UUID;

@WantsListen(phase = WantsListen.Target.Lobby)
public class LobbyEvents implements Listener
{
    @EventHandler
    public void onPlayerJoin(final CPlayerJoinEvent ev)
    {
        if (Game.getLobby().getSpawn() != null) // For /reload only since lobby won't be null after server restart
            ev.getPlayer().getHandle().teleport(Game.getLobby().getSpawn());

        final PlayerInventory inv = ev.getPlayer().getHandle().getInventory();
        inv.clear();
        //inv.setItem(0, TeamItem.getItem(ev.getPlayer()));
        //inv.setItem(1, KitItem.getItem(ev.getPlayer()));
        inv.setItem(4, RocketItem.getItem(ev.getPlayer()));
        inv.setItem(7, SkinItem.getItem(ev.getPlayer()));
    }

    /*
    @EventHandler
    public void onTeamChange(final TeamChangeEvent ev)
    {
        final ItemStack replace = TeamItem.getItem(ev.getPlayer());
        if (!ItemBase.replace(ev.getPlayer().getHandle().getInventory(), TeamItem.ITEM, replace))
            ev.getPlayer().getHandle().getInventory().setItem(0, replace);
    }

    @EventHandler
    public void onKitChange(final KitChangeEvent ev)
    {
        final ItemStack replace = KitItem.getItem(ev.getPlayer());
        if (!ItemBase.replace(ev.getPlayer().getHandle().getInventory(), KitItem.ITEM, replace))
            ev.getPlayer().getHandle().getInventory().setItem(1, replace);
    }*/

    @EventHandler
    public void onSkinChange(final SkinChangeEvent ev)
    {
        final ItemStack replace = SkinItem.getItem(ev.getPlayer());
        if (!IItem.replace(ev.getPlayer().getHandle().getInventory(), SkinItem.ITEM, replace))
            ev.getPlayer().getHandle().getInventory().setItem(7, replace);
    }

    // Cancel all unwanted events
    @EventHandler
    public void onEntityInteract(final EntityInteractEvent ev)
    {
        if (ev.getEntity() instanceof Player && ((Player)ev.getEntity()).isOp())
            return;

        ev.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent ev)
    {
        if (ev.getInventory().equals(ev.getWhoClicked().getInventory()))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent ev)
    {
        if (ev.getInventory().equals(ev.getWhoClicked().getInventory()))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityAirChange(final EntityAirChangeEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityMount(final EntityMountEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityCombust(final EntityCombustEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent ev)
    {
        if (ev instanceof EntityDamageByEntityEvent)
            return;
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent ev)
    {
        if (ev.getDamager() instanceof Player && (ev.getDamager().isOp()))
            return;
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityDismount(final EntityDismountEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityDropItem(final EntityDropItemEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent ev)
    {
        if (ev.getPlayer().isOp())
            return;

        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityExhaust(final EntityExhaustionEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityPickupItem(final EntityPickupItemEvent ev)
    {
        if (ev.getEntity() instanceof Player && (ev.getEntity()).isOp())
            return;

        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntityPlace(final EntityPlaceEvent ev)
    {
        if (ev.getEntity() instanceof Player && (ev.getEntity()).isOp())
            return;

        ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent ev)
    {
        if ((ev.getPlayer()).isOp())
            return;

        ev.setCancelled(true);
    }

    @EventHandler
    public void onCraftItem(final CraftItemEvent ev)
    {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent ev)
    {
        if (ev.getTo().getY() > Game.getLobby().getConfig().LIMBO)
            return;

        ev.setTo(Game.getLobby().getSpawn());
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent ev)
    {
        ev.setCancelled(true);
    }

}
