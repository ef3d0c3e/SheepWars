package org.ef3d0c3e.sheepwars.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.*;
import org.ef3d0c3e.sheepwars.game.Combat;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.KitData;
import org.ef3d0c3e.sheepwars.locale.*;
import org.ef3d0c3e.sheepwars.teams.Team;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * The wrapper class for the player
 */
public class CPlayer implements LocaleSubscriber {
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
     * The locale configured for the player
     */
    @Getter @Setter
    private @NonNull Locale locale;

    /**
     * The player's cosmetics
     */
    @Getter
    private CosmeticManager cosmetics = new CosmeticManager(this);

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
     * Whether the player is alive
     */
    @Getter @Setter
    private boolean alive = false;

    /**
     * Updates the player handle
     * @param handle New handle
     */
    public void setHandle(final @NonNull Player handle)
    {
        this.handle = handle;
        this.offlinePlayer = Bukkit.getOfflinePlayer(handle.getUniqueId());
        setLocale(SheepWars.getLocaleManager().getDefaultLocale());
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
     * The player's team
     * May not be null in lobby phase, null in game phase means spectator i.e. joined after the game started
     */
    @Getter
    private Team team = null;

    /**
     * @note Don't call! Use {@link Team.setPlayerTeam(cp, team)}
     * @param team New team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * The player's kit
     */
    @Getter
    private Kit kit = null;

    /**
     * Sets the player's kit and fires a {@link KitChangeEvent}
     * @param kit The player's new kit
     */
    public void setKit(final @Nullable Kit kit) {
        final var oldKit = this.kit;
        this.kit = kit;
        Bukkit.getPluginManager().callEvent(new KitChangeEvent(this, oldKit, kit));
    }

    /**
     * Data for the player's kit
     * May not be null if kit is not null!
     */
    @Getter
    private KitData kitData = null;

    /**
     * Combat data for the player
     */
    @Getter
    private Combat.Data combatData = new Combat.Data();

    /**
     * Events for the player wrapper
     * When a player joins or quits
     */
    @WantsListen(phase = WantsListen.Target.Always)
    @LocalePath("system")
    public static class Events implements Listener
    {
        private static Localized<String> JOIN;
        private static Localized<String> QUIT;

        @EventHandler
        public void onJoin(final CPlayerJoinEvent ev)
        {
            CPlayer.forEachOnline(cp ->
                    cp.getHandle().sendMessage(MessageFormat.format(JOIN.localize(cp), ev.getPlayer().getHandle().getName()))
            );
        }

        @EventHandler
        public void onQuit(final CPlayerQuitEvent ev)
        {
            // FIXME: After some time (only in lobby mode & for alive players in the game mode)
            // We need to set the handle to null to allow GC (and remove from the hashmap)
            // Send message
            CPlayer.forEachOnline(cp ->
                    cp.getHandle().sendMessage(MessageFormat.format(QUIT.localize(cp), ev.getPlayer().getHandle().getName()))
            );
        }

        /**
         * Sets players as alive when the game starts
         * @param ev Event
         */
        @EventHandler
        public void onPhaseChange(final PhaseChangeEvent ev)
        {
            if (ev.getNewPhase() != WantsListen.Target.Game) return;

            CPlayer.forEachOnline(cp -> cp.alive = true);
        }
    }

}
