package org.ef3d0c3e.sheepwars.player;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.SkinChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.skin.Skin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.text.MessageFormat;

/**
 * Manages cosmetics for the player
 */
public class CosmeticManager {
    private final CPlayer cp;

    @Getter @Setter
    private Skin originalSkin = null;
    @Getter
    private Skin currentSkin = null;

    protected CosmeticManager(final @NonNull CPlayer cp)
    {
        this.cp = cp;
    }

    public void setSkin(final @Nullable Skin skin)
    {
        if (skin == null)
        {
            currentSkin = getOriginalSkin();
            Skin.updateSkin(cp);
            currentSkin = null;
        }
        else
        {
            currentSkin = skin;
            Skin.updateSkin(cp);
        }

        Bukkit.getServer().getPluginManager().callEvent(new SkinChangeEvent(cp, currentSkin, skin));
    }

    @WantsListen(phase = WantsListen.Target.Always)
    public static class Events implements Listener
    {
        @EventHandler(priority = EventPriority.HIGH)
        public void onChat(final AsyncChatEvent ev)
        {
            ev.renderer((player, displayName, message, viewer) -> {
               final CPlayer cp =  CPlayer.get(ev.getPlayer());

               if (cp.getTeam() == null) {
                   return displayName.color(TextColor.color(220, 220, 240))
                           .append(Component.text(": ").color(TextColor.color(95, 95, 95)))
                           .append(message.color(TextColor.color(187, 187, 187)));
               } else {
                   return cp.getTeam().getColoredName(cp)
                           .append(Component.text(" | ").color(cp.getTeam().getColor()))
                           .append(displayName.color(TextColor.color(220, 220, 240)))
                           .append(Component.text(": ").color(TextColor.color(95, 95, 95)))
                           .append(message.color(TextColor.color(187, 187, 187)));
               }
            });
            /*
            if (ev.isCancelled()) return;

            ev.setCancelled(true);

            final CPlayer cp = CPlayer.get(ev.getPlayer());
            String message;
            if (cp.getTeam() == null)
                message = MessageFormat.format("§f{0}§8:§7 {1}", cp.getHandle().getName(), ev.getMessage());
            else {
                message = MessageFormat.format("{0} | {1}§8:§7 {2}", cp.getTeam().getName(cp), cp.getHandle().getName(), ev.getMessage());
            }

            Bukkit.spigot().bro
            Bukkit.broadcastMessage(message);
            */
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerJoin(final CPlayerJoinEvent ev)
        {
            final CPlayer cp = ev.getPlayer();
            final CosmeticManager cosmetics = cp.getCosmetics();
            if (cosmetics.getOriginalSkin() == null) cosmetics.setOriginalSkin(Skin.fromPlayer(cp));
            if (cosmetics.getCurrentSkin() != null) Skin.updateSkin(cp);

            /*
            CPlayer.forEachOnline(o -> {
                o.getCosmetics().updateTabNames();
                o.getCosmetics().updateNametags();
                o.getCosmetics().updateScoreboard();
            });
            */
        }

        /*
        @EventHandler
        public void onTeamChange(final TeamChangeEvent ev)
        {
            CPlayer.forEachOnline(cp ->
            {
                cp.getCosmetics().updateTabNames();
                cp.getCosmetics().updateNametags();
            });

            ev.getPlayer().getCosmetics().updateScoreboard();
        }

        @EventHandler
        public void onKitChange(final KitChangeEvent ev)
        {
            CPlayer.forEachOnline(cp ->
            {
                cp.getCosmetics().updateTabNames();
                cp.getCosmetics().updateNametags();
            });

            ev.getPlayer().getCosmetics().updateScoreboard();
        }
         */
    }
}
