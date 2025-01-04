package org.ef3d0c3e.sheepwars.maps;

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
import org.ef3d0c3e.sheepwars.events.MapVoteEvent;
import org.ef3d0c3e.sheepwars.events.SkinChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.npc.NPCFactory;
import org.ef3d0c3e.sheepwars.npc.PlayerNPC;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.player.skin.SkinMenu;
import org.ef3d0c3e.sheepwars.player.skin.SkinNPC;

import java.text.MessageFormat;
import java.util.List;

public class VoteNPC extends PlayerNPC {
    private static final int NETWORK_ID = 0xFF777730;
    private final Location location;

    public VoteNPC(Location location)
    {
        super(NETWORK_ID);
        this.location = location;
    }

    @Override
    protected @NonNull String getName() {
        return "vote";
    }

    @Override
    protected @NonNull List<Component> getNametag(@NonNull CPlayer cp) {
        var vote = MapManager.getPlayerVote(cp);
        if (vote == null)
            return Lists.newArrayList(
                    Component.text(cp.getLocale().VOTE_NPCNAME)
                            .color(TextColor.color(240, 127, 0))
                            .decorate(TextDecoration.BOLD));
        return Lists.newArrayList(
                Component.text(cp.getLocale().VOTE_NPCNAME)
                        .color(TextColor.color(240, 127, 0))
                        .decorate(TextDecoration.BOLD),
                Component.text(cp.getLocale().VOTE_NPCCURRENT)
                        .color(TextColor.color(85, 85, 127))
                        .decorate(TextDecoration.UNDERLINED),
                Component.text(vote.getDisplayName())
                        .color(TextColor.color(70, 185, 100))
        );
    }

    @Override
    protected @NonNull Property getTextures(@NonNull CPlayer cp) {
        return new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTcxOTAxMDI4Mzc1MiwKICAicHJvZmlsZUlkIiA6ICJkOTcwYzEzZTM4YWI0NzlhOTY1OGM1ZDQ1MjZkMTM0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcmltcHlMYWNlODUxMjciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJmNTExMDNjOGE0NTJiYTI2YTZkYjljMzMxNmE2NzFiY2JkMTlhZDQ2MTJiOTYwNmI3YzMwYzMwMGIxMjkyZiIKICAgIH0KICB9Cn0=",
                "rAVdU0APvsYwlA5SFTflwseqBgj6MvJMuBd+FER4DJKQm3OSq6olRSDBGCJCQNvo6mm7xP0EAaat66+lgXLS3dtCgIbnjfIjexvWhlFUrIYpL88yfMXSNuv/Ba+797LNonenA2vzDcko4HNdx6AdUu6BwAQgQrzW7oa2A8g8GRvUZW1pFwSIxCRbIckT4TsQKNiC/pZerJuv2eDPtLTu9YItea67+7XxN3bpGFz6BQLFkIqpkjD3UCWUdq2JcT+5wJAG6hJoF/7r53YcwcASPo+0Yap6DTiL7BVkFDDXB+d9417mv083hFT7Jb2T+BdgcnVRsBRA2KMzOPKp7nY0k6P1fSC6krZWe8/x+0jhVDurMdmuFWVgKBapkdMw3MvbIUZLZ7JD5jV5aM6C934N6tTTXRTpnKjRa+3nucC3WyB59UYN1yBNADoO9/itf17m9Hw1EeyqMFIyNaN+m+uCyDMNDsRM2oC+PZtuK4U4VKFVADf5FEgaQlLypdP7/By3T6/g6zFRbDGp0szzt2xeHnRyfnCfdIfSPmxp6H5cdwmtFgQoe9EWtHWJ8lqIKV71EDxuO6auNu325DurZVVpxZtGtdNw50nvUh2nRrIKu6jZf/gptkbA+ISHRW6TCHZHRBlpG9X2Lk3YbjHqSla3yB1Thhw8epL5EPaTA+C9z+8=");
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
                cp.getHandle().openInventory(new VoteMenu(cp).getInventory());
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
        public void onVote(final MapVoteEvent ev)
        {
            ((VoteNPC) NPCFactory.get(NETWORK_ID)).update(ev.getPlayer());
        }
    }
}