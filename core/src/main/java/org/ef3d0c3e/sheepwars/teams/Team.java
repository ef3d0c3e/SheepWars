package org.ef3d0c3e.sheepwars.teams;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import jline.internal.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.TeamChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.game.Game;

import java.util.HashSet;

@AllArgsConstructor
public abstract class Team {
    @Getter
    private final ChatColor chatColor;
    @Getter
    private final TextColor color;
    @Getter
    private final Material banner;

    private HashSet<CPlayer> players;

    private Team(final ChatColor chatColor, final TextColor color, final Material banner)
    {
        this.chatColor = chatColor;
        this.color = color;
        this.banner = banner;
        players = new HashSet<>();
    }


    public abstract String getName(final CPlayer cp);

    public String getItemColoredName(final CPlayer cp) {
        if (this == RED)
            return chatColor + cp.getLocale().TEAM_RED;
        else if (this == BLUE)
            return chatColor + cp.getLocale().TEAM_BLUE;
        return null;
    }

    public Component getColoredName(final CPlayer cp)
    {
        return Component.text(getName(cp))
                .color(TextColor.color(color));
    }

    /**
     * Executes lambda for each member of the team
     * @param f Lambda to execute for each member
     */
    public void forEachMember(final CPlayer.ForEachPlayer f)
    {
        for (final CPlayer cp : players)
            f.operation(cp);
    }

    /**
     * Executes lambda for each member of the team
     * @param pre Player predicate
     * @param f Lambda to execute for each member
     */
    public void forEachMember(final CPlayer.PlayerPredicate pre, final CPlayer.ForEachPlayer f)
    {
        for (final CPlayer cp : players)
            if (pre.operation(cp)) f.operation(cp);
    }

    public int count() {
        return players.size();
    }

    /**
     * Sets the player's team
     * @param cp The player to change the team of
     * @param team The new team
     */
    public static void setPlayerTeam(final @NonNull CPlayer cp, final @Nullable Team team) {
        final Team oldTeam = cp.getTeam();
        if (oldTeam != null) oldTeam.players.remove(cp);

        cp.setTeam(team);
        if (team != null) team.players.add(cp);
        if (team != oldTeam)
            Bukkit.getPluginManager().callEvent(new TeamChangeEvent(cp, oldTeam, team));
    }

    public static Team RED = new Team(ChatColor.RED, TextColor.color(255, 0, 0), Material.RED_BANNER) {
        @Override
        public String getName(CPlayer cp) {
            return cp.getLocale().TEAM_RED;
        }
    };

    public static Team BLUE = new Team(ChatColor.BLUE, TextColor.color(0, 0, 255), Material.BLUE_BANNER) {
        @Override
        public String getName(CPlayer cp) {
            return cp.getLocale().TEAM_BLUE;
        }
    };

    @WantsListen(phase = WantsListen.Target.Lobby)
    public static class Events implements Listener
    {
        @EventHandler(priority = EventPriority.LOW)
        public void onJoin(final CPlayerJoinEvent ev)
        {
            if (RED.count() < BLUE.count())
            {
                Team.setPlayerTeam(ev.getPlayer(), RED);
            }
            else if (BLUE.count() < RED.count()) {
                Team.setPlayerTeam(ev.getPlayer(), BLUE);
            }
            // Random team
            else {
                Team.setPlayerTeam(ev.getPlayer(), Game.nextInt(2) == 0 ? RED : BLUE);
            }
        }
    }
}
