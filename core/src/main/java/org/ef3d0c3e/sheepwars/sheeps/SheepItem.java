package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.items.IItem;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.lang.reflect.InvocationTargetException;

public class SheepItem extends IItem {
    final Class<? extends BaseSheep> sheepClass;

    public SheepItem(Class<? extends BaseSheep> sheepClass) {
        super();
        this.sheepClass = sheepClass;
    }

    private @NonNull BaseSheep createSheep(final @NonNull Player p) {
        final var cp = CPlayer.get(p);
        try {
            final var sheep = sheepClass.getDeclaredConstructor(CPlayer.class).newInstance(cp);
            sheep.spawn(p.getLocation().add(0.0, 0.8, 0.0), false);
            return sheep;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean onDrop(Player p, ItemStack item) {
        item.setAmount(item.getAmount() - 1);
        createSheep(p);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 1, 1.f);
        return true;
    }

    @Override
    protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace) {
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return true;
        item.setAmount(item.getAmount() - 1);
        final var sheep = createSheep(p);
        sheep.launch(p.getLocation().getDirection(), 1.5, 30);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 1, 1.f);
        return true;
    }
}
