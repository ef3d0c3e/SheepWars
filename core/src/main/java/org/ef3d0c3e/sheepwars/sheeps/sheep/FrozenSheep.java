package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.EffectSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

import java.util.List;

@LocalePath("sheeps.frozen")
public class FrozenSheep extends EffectSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(FrozenSheep.class);
    final static TextColor COLOR = TextColor.color(110, 173, 231);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.LIGHT_BLUE_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public FrozenSheep(@NonNull CPlayer owner) {
        super(owner, 1000, 400);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }

    @Override
    public void onGrounded(int groundTime) {
        // Particles & potion effect
        if (groundTime % 10 == 0)
        {
            // Particles
            final var center = getLocation().add(0.0, 0.5, 0.0);
            Util.runInCircle(center, new Vector(0, 1, 0), 8.0, 32, (loc, t, i) ->
            {
                center.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 1, 0.0, 0.0, 0.0, 0.0);
            });

            // Effect
            CPlayer.forEach(cp -> cp.isOnline() && cp.isAlive() && cp.getTeam() != getOwner().getTeam(), cp ->
            {
                if (cp.getHandle().getLocation().distance(center) >= 8.5)
                    return;

                final PotionEffect effect = cp.getHandle().getPotionEffect(PotionEffectType.SLOWNESS);
                if (effect != null && effect.getDuration() > 20) // Another frozen
                    cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 2));
                else
                    cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 1));
            });
        }

        // Blocks
        if (groundTime % 100 == 0)
        {
            final var center = getLocation().add(0.0, 0.5, 0.0);
            Util.runInSphereBlock(center, 8.0, (block) ->
            {
                if (block.getType() == Material.WATER)
                    block.setType(Material.ICE);
                else if (block.getType() == Material.SNOW)
                {
                    final Snow snow = (Snow)block.getBlockData();
                    if (snow.getLayers() < snow.getMaximumLayers())
                    {
                        if (Game.nextInt(2) == 0)
                        {
                            snow.setLayers(snow.getLayers() + 1);
                            block.setBlockData(snow, false);
                        }
                    }
                    else
                        block.setType(Material.SNOW_BLOCK);
                }
                else if (block.getType() == Material.AIR && block.canPlace(Material.SNOW.createBlockData()))
                {
                    block.setType(Material.SNOW, false);
                }
                else if (block.getType() != Material.AIR && !block.getType().isSolid())
                {
                    final Block below = center.getWorld().getBlockAt(block.getLocation().clone().add(0, -1, 0));
                    if (below.getType().isSolid())
                        block.setType(Material.SNOW, false);

                }
            });
        }
    }
}
