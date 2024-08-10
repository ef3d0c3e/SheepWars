package org.ef3d0c3e.sheepwars.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;

public class NPCFactory
{
    private static HashMap<Integer, PlayerNPC> registry = new HashMap<>();

    /**
     * Registers NPC into the registry
     * @param npc NPC to register (does nothing if NPC's networkId is already registered)
     */
    public static void register(final @NonNull PlayerNPC npc)
    {
        registry.put(npc.getNetworkId(), npc);
    }

    /**
     * Unregisters NPC from the registry
     * @param npc NPC to unregister
     * @return npc on successful unregister, null otherwise
     */
    public static @Nullable
    PlayerNPC unregsiter(final @NonNull PlayerNPC npc)
    {
        return registry.remove(npc.getNetworkId());
    }

    /**
     * Gets NPC from registry using network id
     * @param eid's NPC network id
     * @return NPC if found, null otherwise
     */
    public static @Nullable PlayerNPC get(int eid)
    {
        return registry.get(eid);
    }

    @WantsListen(phase = WantsListen.Target.Always)
    public static class NPCListener implements Listener
    {
        // TODO..., on chunk load request [something...]
        @EventHandler
        public void onJoin(final CPlayerJoinEvent ev)
        {
            final CPlayer cp = ev.getPlayer();
            for (final PlayerNPC npc : registry.values())
            {
                if (!npc.sendPredicate(cp)) continue;

                npc.sendInfo(cp, false);
                npc.send(cp);
                npc.sendNametag(cp);
            }
        }
    }

    @WantsListen(phase = WantsListen.Target.Always)
    public static class NPCPacketListener implements PacketListener
    {
        @Override
        public void onPacketSending(final PacketEvent packetEvent)
        {
        }

        @Override
        public void onPacketReceiving(final PacketEvent packetEvent)
        {
            final PacketContainer interact = packetEvent.getPacket();
            final PlayerNPC npc = get(interact.getIntegers().read(0));
            if (npc == null) return;

            final CPlayer cp = CPlayer.get(packetEvent.getPlayer());
            final Location npcLoc = npc.getLocation(cp);
            if (!npcLoc.getWorld().equals(cp.getHandle().getWorld())) return; // Fake packet check

            final double distSq = cp.getHandle().getLocation().distanceSquared(npcLoc);
            if (distSq >= 49.0) return; // Reach check


            final WrappedEnumEntityUseAction action = interact.getEnumEntityUseActions().read(0);
            if (action.getAction().equals(EnumWrappers.EntityUseAction.INTERACT))
            {
                npc.onInteract(
                        cp,
                        action.getHand(),
                        interact.getBooleans().read(0)
                );
            }
        }

        @Override
        public ListeningWhitelist getSendingWhitelist()
        {
            return ListeningWhitelist.EMPTY_WHITELIST;
        }

        private final ListeningWhitelist listeningWhitelist = ListeningWhitelist.newBuilder()
                .gamePhase(GamePhase.PLAYING)
                .types(PacketType.Play.Client.USE_ENTITY)
                .build();

        @Override
        public ListeningWhitelist getReceivingWhitelist()
        {
            return listeningWhitelist;
        }

        @Override
        public Plugin getPlugin()
        {
            return SheepWars.getPlugin();
        }
    }
}
