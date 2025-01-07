package org.ef3d0c3e.sheepwars.player.skin;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.SkinChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.npc.NPCFactory;
import org.ef3d0c3e.sheepwars.npc.PlayerNPC;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;

public class SkinNPC extends PlayerNPC {
    private static final int NETWORK_ID = SheepWars.getNextEntityId();
    private final Location location;

    public SkinNPC(Location location)
    {
        super(NETWORK_ID);
        this.location = location;
    }

    @Override
    protected @NonNull String getName() {
        return "skin";
    }

    @Override
    protected @NonNull List<Component> getNametag(@NonNull CPlayer cp) {
        if (cp.getCosmetics().getCurrentSkin() == null)
            return Lists.newArrayList(
                    Component.text(cp.getLocale().SKIN_NPCNAME)
                            .color(TextColor.color(240, 127, 0))
                            .decorate(TextDecoration.BOLD));
        else
            return Lists.newArrayList(
                    Component.text(cp.getLocale().SKIN_NPCNAME)
                            .color(TextColor.color(240, 127, 0))
                            .decorate(TextDecoration.BOLD),
                    Component.text(cp.getLocale().SKIN_NPCCURRENT)
                            .color(TextColor.color(85, 85, 127))
                            .decorate(TextDecoration.UNDERLINED),
                    Component.text(cp.getCosmetics().getCurrentSkin().getName())
                                    .color(TextColor.color(180, 85, 120))
            );
    }

    @Override
    protected @NonNull Property getTextures(@NonNull CPlayer cp) {
        if (cp.getCosmetics().getCurrentSkin() != null)
            return cp.getCosmetics().getCurrentSkin().toProperty();

        final PropertyMap pm = Skin.WRAPPER.getProperties(cp);

        // TODO: May not work if player has cape...
        return pm.get("textures").iterator().next();
    }

    @Override
    protected @NonNull Location getLocation(@NonNull CPlayer cp) {
        return location;
    }

    @Override
    protected boolean sendPredicate(@NonNull CPlayer cp) {
        return cp.getHandle().getWorld() == location.getWorld();
    }

    @Override
    protected void onInteract(@NonNull CPlayer cp, EnumWrappers.Hand hand, boolean sneaking) {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!cp.isOnline()) return;
                cp.getHandle().openInventory(new SkinMenu(cp).getInventory());
            }
        }.runTask(SheepWars.getPlugin());

    }

    @Override
    protected void update(final @NonNull CPlayer cp)
    {
        // Resend nametag
        removeNametag(cp, 3);
        sendNametag(cp);

        // Resend skin
        sendInfo(cp, true);
    }

    @WantsListen(phase = WantsListen.Target.Lobby)
    public static class Events implements Listener
    {
        @EventHandler
        public void onSkinChange(final SkinChangeEvent ev)
        {
            ((SkinNPC) NPCFactory.get(NETWORK_ID)).update(ev.getPlayer());
        }
    }
}
