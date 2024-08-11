package org.ef3d0c3e.sheepwars.teams;

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
import org.ef3d0c3e.sheepwars.events.TeamChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.npc.NPCFactory;
import org.ef3d0c3e.sheepwars.npc.PlayerNPC;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;

public class TeamNPC extends PlayerNPC {
    private static final int NETWORK_ID = 0xFF777720;
    private final Location location;

    public TeamNPC(Location location) {
        super(NETWORK_ID);
        this.location = location;
    }

    @Override
    protected @NonNull String getName() {
        return "team";
    }

    @Override
    protected @NonNull List<Component> getNametag(@NonNull CPlayer cp) {
        if (cp.getTeam() == null) // May not happen in lobby mode
            return Lists.newArrayList(
                    Component.text(cp.getLocale().TEAM_NPCNAME)
                            .color(TextColor.color(207, 50, 200))
                            .decorate(TextDecoration.BOLD));
        else
            return Lists.newArrayList(
                    Component.text(cp.getLocale().TEAM_NPCNAME)
                            .color(TextColor.color(207, 50, 200))
                            .decorate(TextDecoration.BOLD),
                    Component.text(cp.getLocale().TEAM_NPCCURRENT)
                            .color(TextColor.color(85, 85, 127))
                            .decorate(TextDecoration.UNDERLINED),
                    cp.getTeam().getColoredName(cp)
            );
    }

    @Override
    protected @NonNull Property getTextures(@NonNull CPlayer cp) {
        return new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTcwMTk1OTQ2MjQ4NSwKICAicHJvZmlsZUlkIiA6ICJmODY0ZjY3ZGJlN2Y0OTBlYTZlODQzMjg2M2NkZWMxOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb3lmcmllbmQ1MDY1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzMDgwZWZlMzdkNTk0ZjU0ZjNjMTgxYmRlNTBlMTI4ZWQyZjQyYmVkOGQzNjk3N2Y4ZTQwNmRmZDY0ZWZkZmQiCiAgICB9CiAgfQp9",
                "hB0+1tCYxTvdjYlYS7wtwqbf30CNa/8BmkrpWv7mA0MFiQIikInm+rZEZbyHFFOba8Dc5Ee9hC/pu4rHwBqXN4XjsFELFqUHymKnhOSNmUfY7aA0rn+CaNrJOP7LSrajSN+r7qgRsXUph/7yEaTpMhWwu+nfzbxtaS7e8WqjqHtMYdjWg30HSTqrSYzmobo9wh3twbuEFil8dCTdG3A9YkICfhYeuAgwDENGe220ThPC4HJsyPS1NCAkuwfGHKFyjqomUkPm0o6ijnb4I5naSvGFFLqvjJDFQ2dwui8TacykoLj+Mou3NnSTawcutBD10HiMF/mgssZTsINim1Da4uOZR9FsShiAk5Z4nq7unh0vPdH+lgCoTaN5tD0DCmrZt5OLSEqpzx62EoYRWM5nUXRISVHKKADpra424O9zSytOCwjGGvYVg6uB6lOOb+Gm1+VDEU+7QzwhpYiMFaqiofZDJw7LNQ0EZcbbbTUFfUE7/d/X4sb2AGQno7RGVAdWrz/Kszf6/ri+Wru4GRHZBaS3LVnxXU4FUL7P9yF3ZPrpNZIt14f1WSWmk1ltGnwNwK8HfSRfo7uWZtDVZZWOeJA1bqTYMDP9n1hIHYgrLdIEmXpGK3RVytYeKbQVLWbTtPKTzPMhDaDyQkwGint0HgFm3jS29sRejdAMF81Hjt4=");
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastInteracted(cp) < 1000)
                    return;

                if (!cp.isOnline()) return;

                if (cp.getTeam() == Team.RED) {
                    Team.setPlayerTeam(cp, Team.BLUE);
                } else {
                    Team.setPlayerTeam(cp, Team.RED);
                }

                setLastInteracted(cp);
            }
        }.runTask(SheepWars.getPlugin());
    }

    @Override
    protected void update(final @NonNull CPlayer cp) {
        // Resend nametag
        removeNametag(cp, 3);
        sendNametag(cp);

        // Resend skin
        sendInfo(cp, true);
    }

    @WantsListen(phase = WantsListen.Target.Lobby)
    public static class Events implements Listener {
        @EventHandler
        public void onTeamChange(final TeamChangeEvent ev) {
            ((TeamNPC) NPCFactory.get(NETWORK_ID)).update(ev.getPlayer());
        }
    }

}
