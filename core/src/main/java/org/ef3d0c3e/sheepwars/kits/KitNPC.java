package org.ef3d0c3e.sheepwars.kits;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import com.mojang.authlib.properties.Property;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.KitChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.npc.NPCFactory;
import org.ef3d0c3e.sheepwars.npc.PlayerNPC;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;

public class KitNPC extends PlayerNPC {
    private static final int NETWORK_ID = SheepWars.getNextEntityId();
    private final Location location;

    public KitNPC(Location location)
    {
        super(NETWORK_ID);
        this.location = location;
    }

    @Override
    protected @NonNull String getName() {
        return "kit";
    }

    @Override
    protected @NonNull List<Component> getNametag(@NonNull CPlayer cp) {
        if (cp.getKit() == null)
            return Lists.newArrayList(
                    Component.text(cp.getLocale().KIT_NPCNAME)
                            .color(TextColor.color(140, 240, 40))
                            .decorate(TextDecoration.BOLD));
        return Lists.newArrayList(
                Component.text(cp.getLocale().KIT_NPCNAME)
                        .color(TextColor.color(140, 240, 40))
                        .decorate(TextDecoration.BOLD),
                Component.text(cp.getLocale().KIT_NPCCURRENT)
                        .color(TextColor.color(85, 85, 127))
                        .decorate(TextDecoration.UNDERLINED),
                cp.getKit().getColoredName(cp)
        );

    }

    @Override
    protected @NonNull Property getTextures(@NonNull CPlayer cp) {
        return new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTY4MTQ2NTc2NTg5NiwKICAicHJvZmlsZUlkIiA6ICJkOGNkMTNjZGRmNGU0Y2IzODJmYWZiYWIwOGIyNzQ4OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJaYWNoeVphY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU1YTUyNTU4ZWU1ZDUyYzk2YmI0ZmUyOTZkNDNjMTEzMDExNWI5MmIxYWI1Y2RmNmJjZDhhMDdmZTRkZjljYSIKICAgIH0KICB9Cn0=",
                "nFM67iyF1oFEApe6/cjsYRgbf1rx0WKsQILYQxCZ6uGGhNp/leVo2Ew68m2Cygkt27io4pXFIe1zlCdXYh+lqWojCnwwzzXbyUvuHqm1TdRri86jgsm0p53NQyNVduC7AbETqHUvvD8dyk4R6iGsq1hlmQAnmmxkZIRDlIWngKaRoqpBwE8I6d94TqqLVWOSLDsjKoM0vPUkvDtghPE586nr07xZ4MSFaLn8Y8nBRJYKtaGekOk/L1gYhJm5mkLN9lrrzBzs5Qp2YIxTY0uvIZr5kASQdk2ShiPHV0SpT1ngkNPae5fysdJLZaK71iS7ulzhnThfYNRKUiMrLVpD7ks+iEmtY8o3Mfd/80uyqfBXWXrjWe0xFM8WWIU1YXt97XXwhCUBZgYQg9xRwG3cZnrkuey4UZUrPAY6/yxoDFaOaFeFjzx9/ZbhIqA1ohJGd0SbcIS+IZkAz7C5pGDDHG8+arksIF6Z2fZkv+l4n2q6oCYtYa2UjyUrhSLtSGlzlHi1sSzxlz6f9ANTEeh0103bnZIelwZwT1Z5tWMoBF8jy/oo5K8fbB1OgE428sZ+3ZHNYzMbsFP8GsMFa1O04fqC2ZeQ8ViVRhAZsn2AbAEdRVh04qg5p3iBJfnjmqZPmi4vDW2a93i/asWAmpA+fUM9q6cGII4UFFHE1nExVgo=");
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
                cp.getHandle().openInventory(new KitMenu(cp).getInventory());
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
        public void onKitChange(final KitChangeEvent ev)
        {
            ((KitNPC)NPCFactory.get(NETWORK_ID)).update(ev.getPlayer());
        }
    }
}