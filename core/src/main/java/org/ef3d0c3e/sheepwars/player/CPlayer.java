package org.ef3d0c3e.sheepwars.player;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.units.qual.C;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.locale.Locale;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * The wrapper class for the player
 */
public class CPlayer {
    /**
     * Holds a list of all players that have connected on this session
     */
    private static HashMap<String, CPlayer> playerList = new HashMap<>();

    /**
     * Gets CPlayer from player list
     * @param p Player
     * @return CPlayer or null
     */
    static public @Nullable CPlayer get(final Player p)
    {
        return playerList.get(p.getName());
    }

    /**
     * Adds player to player list
     * @param p Player to add
     * @return Newly created player or existing player
     */
    static public CPlayer add(final @NonNull Player p)
    {
        CPlayer cp = get(p);
        if (cp != null) return cp;

        // Add
        cp = new CPlayer(p);
        playerList.put(p.getName(), cp);

        return cp;
    }

    public interface ForEachPlayer
    {
        public void operation(final CPlayer cp);
    }

    public interface PlayerPredicate
    {
        public boolean operation(final CPlayer cp);
    }

    /**
     * Executes lambda for all players
     * @param f Lambda expression to execute on all players
     */
    public static void forEach(final ForEachPlayer f)
    {
        for (final CPlayer cp : playerList.values())
            f.operation(cp);
    }

    /**
     * Executes lambda for all players
     * @param pre Predicate
     * @param f Lambda expression to execute on all players
     */
    public static void forEach(final PlayerPredicate pre, final ForEachPlayer f)
    {
        for (final CPlayer cp : playerList.values())
            if (pre.operation(cp)) f.operation(cp);
    }

    /**
     * Executes lambda for all online players
     * @param f Lambda expression to execute for all lambda players
     */
    public static void forEachOnline(final ForEachPlayer f)
    {
        for (final CPlayer cp : playerList.values())
            if (cp.isOnline()) f.operation(cp);
    }

    /**
     * The player handle
     */
    @Getter
    private Player handle;
    /**
     * The OfflinePlayer handle
     */
    @Getter
    private OfflinePlayer offlinePlayer;

    /**
     * The locale configured for the player
     */
    @Getter
    private Locale locale;
    /**
     * The player's cosmetics
     */
    @Getter
    private CosmeticManager cosmetics = new CosmeticManager(this);

    /**
     * Updates the player handle
     * @param handle New handle
     */
    public void setHandle(final @NonNull Player handle)
    {
        this.handle = handle;
        this.offlinePlayer = Bukkit.getOfflinePlayer(handle.getUniqueId());
        this.locale = SheepWars.getLocaleManager().getDefaultLocale();
    }


    private CPlayer(@NonNull final Player handle)
    {
        setHandle(handle);
    }

    /**
     * Check if the player is online
     * @return Whether player is online or not
     */
    public boolean isOnline()
    {
        return offlinePlayer.isOnline();
    }

    /**
     * Events for the player wrapper
     * When a player joins or quits
     */
    @WantsListen(phase = WantsListen.Target.Always)
    public static class Events implements Listener
    {
        @EventHandler
        public void onJoin(final CPlayerJoinEvent ev)
        {
            CPlayer.forEachOnline(cp ->
                    cp.getHandle().sendMessage(MessageFormat.format(cp.getLocale().SYSTEM_JOIN, ev.getPlayer().getHandle().getName()))
            );
        }

        @EventHandler
        public void onQuit(final CPlayerQuitEvent ev)
        {
            // FIXME: After some time (only in lobby mode & for alive players in the game mode)
            // We need to set the handle to null to allow GC (and remove from the hashmap)
            // Send message
            CPlayer.forEachOnline(cp ->
                    cp.getHandle().sendMessage(MessageFormat.format(cp.getLocale().SYSTEM_QUIT, ev.getPlayer().getHandle().getName()))
            );
        }
    }

}
