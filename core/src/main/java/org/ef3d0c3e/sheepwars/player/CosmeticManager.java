package org.ef3d0c3e.sheepwars.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.SkinChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.skin.Skin;

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
        public void onChat(final AsyncPlayerChatEvent ev)
        {
            if (ev.isCancelled()) return;

            ev.setCancelled(true);

            final CPlayer cp = CPlayer.get(ev.getPlayer());
            String message;
            //if (cp.getTeam() == null)
                message = MessageFormat.format("§f{0}§8:§7 {1}", cp.getHandle().getName(), ev.getMessage());
            //else
                //message = MessageFormat.format("{0} | {1}§8:§7 {2}", cp.getTeam().getColoredName(), cp.getHandle().getName(), ev.getMessage());

            Bukkit.broadcastMessage(message);
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
