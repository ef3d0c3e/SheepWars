package org.ef3d0c3e.sheepwars.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import fr.mrmicky.fastboard.FastBoard;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.events.*;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.LocalizeAs;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.maps.MapManager;
import org.ef3d0c3e.sheepwars.player.skin.Skin;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Manages cosmetics for the player
 */
public class CosmeticManager {
    @LocalePath("cosmetic.scoreboard.lobby")
    private static class LobbyScoreboard {
        static Localized<String> FOOTER;

        @LocalizeAs("team.name")
        static Localized<String> TEAM_NAME;

        @LocalizeAs("kit.name")
        static Localized<String> KIT_NAME;
        @LocalizeAs("kit.none")
        static Localized<String> KIT_NONE;


        @LocalizeAs("vote.name")
        static Localized<String> VOTE_NAME;
        @LocalizeAs("vote.none")
        static Localized<String> VOTE_NONE;
    }

    @LocalePath("cosmetic.scoreboard.game")
    private static class GameScoreboard {
        static Localized<String> ALIVE;
        static Localized<String> TIMER;
    }


    private final CPlayer cp;

    @Getter @Setter
    private Skin originalSkin = null;
    @Getter
    private Skin currentSkin = null;

    private FastBoard board = null;

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

    /**
     * Updates the scoreboard of a player
     */
    public void updateScoreboard()
    {
        board = new FastBoard(cp.getHandle());

        final var ser = LegacyComponentSerializer.legacy('§');
        final ArrayList<String> lines = new ArrayList<>();

        if (Game.getPhase() == WantsListen.Target.Lobby)
        {
            board.updateTitle(ser.serialize(Component.text("SheepWars").color(TextColor.color(127, 200, 80))));
            lines.add("");

            // Team
            lines.add(ser.serialize(
                    Component.text(" * ").color(TextColor.color(180, 180, 255))
                            .append(Component.text(LobbyScoreboard.TEAM_NAME.localize(cp) + ": ")
                            .color(TextColor.color(200, 200, 220)))
                .append(cp.getTeam().getColoredName(cp))
            ));
            // Kit
            if (cp.getKit() != null)
                lines.add(ser.serialize(
                        Component.text(" * ").color(TextColor.color(180, 180, 255))
                                .append(Component.text(LobbyScoreboard.KIT_NAME.localize(cp) + ": ")
                                .color(TextColor.color(200, 200, 210)))
                                .append(cp.getKit().getColoredName(cp))
                ));
            else
                lines.add(ser.serialize(
                        Component.text(" * ").color(TextColor.color(180, 180, 255))
                                .append(Component.text(LobbyScoreboard.KIT_NONE.localize(cp))
                                .color(TextColor.color(200, 200, 210)))
                ));
            // Vote
            final var map = MapManager.getPlayerVote(cp);
            if (map != null)
                lines.add(ser.serialize(
                        Component.text(" * ").color(TextColor.color(180, 180, 255))
                                .append(Component.text(LobbyScoreboard.VOTE_NAME.localize(cp) + ": ")
                                .color(TextColor.color(200, 200, 200)))
                                .append(Component.text(map.getDisplayName()).color(TextColor.color(80, 200, 110)))
                ));
            else
                lines.add(ser.serialize(
                        Component.text(" * ").color(TextColor.color(180, 180, 255))
                                .append(Component.text(LobbyScoreboard.VOTE_NONE.localize(cp))
                                .color(TextColor.color(200, 200, 200)))
                ));

            lines.add("");
            lines.add(ser.serialize(
                    Component.text(LobbyScoreboard.FOOTER.localize(cp)).color(TextColor.color(200, 120, 60))
            ));

        }
        else if (cp.isAlive())
        {
            board.updateTitle(ser.serialize(Component.text("SheepWars").color(TextColor.color(200, 80, 120)).decorate(TextDecoration.BOLD)));

            // Alive count
            lines.add("");
            lines.add(ser.serialize(
                    Component.text("| ").color(TextColor.color(250, 110, 210)).decorate(TextDecoration.BOLD)
                            .append(Component.text(GameScoreboard.ALIVE.localize(cp) + ": ").color(TextColor.color(250, 110, 210)).decoration(TextDecoration.BOLD, false))
            ));
            lines.add(ser.serialize(
                    Component.text(String.valueOf(cp.getTeam().getAliveCount()))
                            .color(TextColor.color(215, 170, 55))));

            // Timer
            lines.add("");
            lines.add(ser.serialize(
                    Component.text("| ").color(TextColor.color(110, 120, 250)).decorate(TextDecoration.BOLD)
                    .append(Component.text(GameScoreboard.TIMER.localize(cp) + ": ").color(TextColor.color(110, 120, 250)).decoration(TextDecoration.BOLD, false))
            ));
            lines.add(ser.serialize(
                    Component.text(String.format("%d:%02d",
                        Game.getTimer().getMinutes(), Game.getTimer().getSeconds() % 60
                    ))
                            .color(TextColor.color(240, 230, 30))));
        }
        else
        {
            board.updateTitle(ser.serialize(Component.text("SheepWars").color(TextColor.color(200, 80, 120)).decorate(TextDecoration.BOLD)));
            //TODO
        }
        board.updateLines(lines);
    }

    /**
     * @brief Updates tab name for a player (and send it to other players)
     */
    public void updateTabNames()
    {
        final var data = new WrapperPlayServerPlayerInfo.PlayerData(
            Component.text("aaa"),
                new UserProfile(cp.getHandle().getUniqueId(), cp.getHandle().getName()),
            null,
            cp.getHandle().getPing()
        );
        final WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME, data);

        CPlayer.forEachOnline((o) -> {
            PacketEvents.getAPI().getPlayerManager().sendPacket(o.getHandle(), info);
        });
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

        @EventHandler(priority = EventPriority.HIGH)
        public void onPlayerJoin(final CPlayerJoinEvent ev)
        {
            final CPlayer cp = ev.getPlayer();
            final CosmeticManager cosmetics = cp.getCosmetics();
            if (cosmetics.getOriginalSkin() == null) cosmetics.setOriginalSkin(Skin.fromPlayer(cp));
            if (cosmetics.getCurrentSkin() != null) Skin.updateSkin(cp);

            cp.getCosmetics().updateScoreboard();
            cp.getCosmetics().updateTabNames();
            //cp.getCosmetics().updateNametags();
        }

        /**
         * Removes player's board
         * @param ev Event
         */
        @EventHandler(priority = EventPriority.HIGH)
        public void onPlayerQuit(final CPlayerQuitEvent ev) {
            final CPlayer cp = ev.getPlayer();
            cp.getCosmetics().board = null;
        }

        @EventHandler
        public void onTeamChange(final TeamChangeEvent ev)
        {
            ev.getPlayer().getCosmetics().updateScoreboard();
            ev.getPlayer().getCosmetics().updateTabNames();
        }

        @EventHandler
        public void onVote(final MapVoteEvent ev)
        {
            ev.getPlayer().getCosmetics().updateScoreboard();
        }

        @EventHandler
        public void onKitChange(final KitChangeEvent ev)
        {
            ev.getPlayer().getCosmetics().updateScoreboard();
            ev.getPlayer().getCosmetics().updateTabNames();
        }
    }
}
