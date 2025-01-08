package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.Block;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.EffectSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@LocalePath("sheeps.shield")
public class ShieldSheep extends EffectSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(ShieldSheep.class);
    final static TextColor COLOR = TextColor.color(173, 202, 254);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.CYAN_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public ShieldSheep(@NonNull CPlayer owner) {
        super(owner, 1000, 160);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }

    @Override
    public void onGrounded(int groundTime) {
        if (groundTime % 20 != 0) return;

        final var center = getLocation().add(0.0, 0.5, 0.0);

        // Particles
        Util.runInCircle(center, new Vector(0, 1, 0), 8.0, 32, (loc, t, i) ->
        {
            center.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0.0, 0.0, 0.0, 0.0);
        });

        // Effect
        CPlayer.forEach(cp -> cp.isAlive() && cp.isOnline() && cp.getTeam() == getOwner().getTeam(), cp ->
        {
            if (cp.getHandle().getLocation().distance(center) >= 8.5)
                return;

            final PotionEffect effect = cp.getHandle().getPotionEffect(PotionEffectType.RESISTANCE);
            if (effect != null && effect.getDuration() > 10) // Another shield
                cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 1));
            else
                cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 0));
        });
    }

    /**
     * Events for the Shield Sheep
     */
    @WantsListen(phase = WantsListen.Target.Always)
    public static class Events implements Listener
    {
        /**
         * Prevents shield to take explosion damage
         */
        @EventHandler
        public void onShieldDamage(final EntityDamageEvent ev)
        {
            if (BaseSheep.getInstance(ev.getEntity()) == null
                    || ev.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                return;
            ev.setCancelled(true);
        }

        @EventHandler
        public void onEntityExplode(final EntityExplodeEvent ev)
        {
            int amt = 0;

            ArrayList<ShieldSheep> shields = new ArrayList<>();
            for (final Entity ent : ev.getEntity().getWorld().getEntities())
            {
                final var sheep = BaseSheep.getInstance(ent);
                if (!(sheep instanceof ShieldSheep))
                    continue;

                shields.add((ShieldSheep)sheep);
            }

            Iterator<Block> it =  ev.blockList().iterator();
            while (it.hasNext())
            {
                final Block block = it.next();
                if (!shields.isEmpty())
                {
                    boolean removed = false;
                    for (final ShieldSheep shield : shields)
                    {
                        if (block.getLocation().distanceSquared(shield.getLocation()) >= 64.0)
                            continue;
                        it.remove();
                        removed = true;
                        break;
                    }
                    if (!removed && block.getType() != Material.TNT && Game.nextInt(3) == 0 && amt != 50)
                    {
                        ++amt;

                        FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                        fb.setDropItem(false);
                        fb.setVelocity(new Vector(
                                (Math.random() - Math.random()) / 1.5,
                                Math.random(),
                                (Math.random() - Math.random()) / 1.5));
                    }
                }
                else if (block.getType() != Material.TNT && Game.nextInt(3) == 0 && amt != 50)
                {
                    ++amt;

                    FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                    fb.setDropItem(false);
                    fb.setVelocity(new Vector(
                            (Math.random() - Math.random()) / 1.5,
                            Math.random(),
                            (Math.random() - Math.random()) / 1.5));
                }
            }
        }

        @EventHandler
        public void onFireSpread(final BlockIgniteEvent ev)
        {
            ev.getBlock().getLocation();
            for(final Entity ent : ev.getBlock().getWorld().getEntities())
            {
                final var sheep = BaseSheep.getInstance(ent);
                if (!(sheep instanceof ShieldSheep))
                    continue;

                if (ent.getLocation().distanceSquared(ev.getBlock().getLocation()) <= 64.0)
                    ev.setCancelled(true);
            }
        }

        @EventHandler
        public void onFireBurn(final BlockBurnEvent ev)
        {
            ev.getBlock().getLocation();
            for(final Entity ent : ev.getBlock().getWorld().getEntities())
            {
                final var sheep = BaseSheep.getInstance(ent);
                if (!(sheep instanceof ShieldSheep))
                    continue;

                if (ent.getLocation().distanceSquared(ev.getBlock().getLocation()) <= 64.0)
                {
                    if (ev.getIgnitingBlock().getType() == Material.FIRE)
                        ev.getIgnitingBlock().setType(Material.AIR);
                    ev.setCancelled(true);
                }
            }
        }
    }
}
