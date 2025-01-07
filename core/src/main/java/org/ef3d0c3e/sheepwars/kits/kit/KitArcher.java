package org.ef3d0c3e.sheepwars.kits.kit;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.KitData;
import org.ef3d0c3e.sheepwars.loadouts.Loadout;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;

@LocalePath("kits.archer")
public class KitArcher extends Kit {
    private static Localized<String> NAME;
    private static Localized<List<String>> DESC;

    @Override
    public @NonNull String getName() {
        return "archer";
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
        final var item = new ItemStack(Material.BOW);
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
        return new Loadout(cp) {
            @Override
            public int arrowCount() { return 5; }
        };
    }
}
