package org.ef3d0c3e.sheepwars.kits.kit;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.items.IItem;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.KitData;
import org.ef3d0c3e.sheepwars.loadouts.Loadout;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.LocalizeAs;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;

@LocalePath("kits.mage")
public class KitMage extends Kit {
    @LocalizeAs("wand.name")
    private static Localized<String> WAND_NAME;
    @LocalizeAs("wand.lore")
    private static Localized<List<String>> WAND_LORE;

    public static class WandItem extends IItem {
        private int nextUsable = 0;

        private void setCooldown(final @NonNull Player p, final @NonNull ItemStack item, final int cooldown) {
            p.setCooldown(item.getType(), cooldown);
            nextUsable = Game.getTimer().getElapsed() + cooldown;
        }

        private static void drawLine(final Location start, final Vector direction, final double distance)
        {
            final var w = start.getWorld();
            for (int i = 1; i <= distance; ++i)
            {
                w.spawnParticle(Particle.BUBBLE_POP,
                        start.clone().add(direction.normalize().multiply(i)),
                        1, 0, 0, 0
                );
                w.spawnParticle(Particle.END_ROD,
                        start.clone().add(direction.normalize().multiply(i)),
                        1, 0, 0, 0
                );
            }
        }


        @Override
        protected boolean onDrop(Player p, ItemStack item) {
            return true;
        }

        @Override
        protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace) {
            if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return true;

            if (Game.getTimer().getElapsed() < nextUsable || Game.getPhase() == WantsListen.Target.Game)
                return true;

            final RayTraceResult trace = p.getWorld().rayTrace(
                    p.getEyeLocation(), p.getLocation().getDirection(),
                    128.0, FluidCollisionMode.NEVER,
                    true, 0.1,
                    (ent) -> ent instanceof LivingEntity && ent != p
            );


            // Make block levitate
            if (trace != null && trace.getHitBlock() != null)
            {
                final Block b = trace.getHitBlock();

                final FallingBlock fb = p.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
                fb.setVelocity(new Vector(Math.random()*0.1 - 0.05, 0.5, Math.random()*0.1 - 0.05));
                fb.setDropItem(false);
                b.setType(Material.AIR);
                setCooldown(p, item, 10);
            }
            else if (trace != null && trace.getHitEntity() != null) // Make entity levitate
            {
                final LivingEntity ent = (LivingEntity)trace.getHitEntity();
                ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0, false, true, true));
                setCooldown(p, item, 80);
            }

            // Particle & cooldown
            if (trace == null)
            {
                drawLine(p.getEyeLocation(), p.getLocation().getDirection(), 128.0);
                setCooldown(p, item, 10);
            }
            else
                drawLine(p.getEyeLocation(), p.getLocation().getDirection(), trace.getHitPosition().distance(p.getEyeLocation().toVector()));

            // Sound
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 12.f, 1.f);

            return true;
        }
    }

    private static class MageLoadout extends Loadout {
        @Override
        public List<@NonNull ItemStack> items() {
            final var ser = LegacyComponentSerializer.legacy('§');
            final var wand = new ItemStack(Material.BLAZE_ROD);
            final var meta = wand.getItemMeta();
            meta.setDisplayName(ser.serialize(Component.text(WAND_NAME.localize(getPlayer()), TextColor.color(127, 20, 240))));
            meta.setLore(Util.coloredLore("§7", DESC.localize(getPlayer())));
            wand.setItemMeta(meta);
            final var wandItem = new WandItem();
            ItemFactory.registerItem(wandItem);
            return List.of(wandItem.apply(wand));

        }

        public MageLoadout(@NonNull CPlayer player) {
            super(player);
        }
    }


    private static Localized<String> NAME;
    private static Localized<List<String>> DESC;

    @Override
    public @NonNull String getName() {
        return "mage";
    }

    @Override
    public @NonNull String getDisplayName(@NonNull CPlayer cp) {
        return NAME.localize(cp);
    }

    @Override
    public @NonNull Component getColoredName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(TextColor.color(60, 120, 200));
    }

    @Override
    public @NonNull ItemStack getIcon(@NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.BLAZE_ROD);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(getColoredName(cp)));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);
        return item;
    }

    public static class Data extends KitData
    {

    }

    @Override
    public @NonNull KitData createData(@NonNull CPlayer cp) {
        return new Data();
    }

    @Override
    public @NonNull Loadout loadout(@NonNull CPlayer cp) {
        return new MageLoadout(cp) {};
    }
}
