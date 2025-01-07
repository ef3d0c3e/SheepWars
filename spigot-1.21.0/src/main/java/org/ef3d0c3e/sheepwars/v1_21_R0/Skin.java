package org.ef3d0c3e.sheepwars.v1_21_R0;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.RemoteChatSession;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.PublicProfileKey;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.player.skin.SkinVersionWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Skin implements SkinVersionWrapper
{
    @Override
    public void updateSkin(final @NonNull CPlayer cp)
    {
        // FIXME: May not work in offline mode, may work with SkinRestorer
        final org.ef3d0c3e.sheepwars.player.skin.Skin skin = cp.getCosmetics().getCurrentSkin();
        final PropertyMap pm = ((CraftPlayer)cp.getHandle()).getHandle().getGameProfile().getProperties();
        Property text = pm.get("textures").iterator().next();

        pm.remove("textures", text);
        pm.put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));

        for (final Player p : Bukkit.getOnlinePlayers())
        {
            p.hidePlayer(SheepWars.getPlugin(), cp.getHandle());
            p.showPlayer(SheepWars.getPlugin(), cp.getHandle());
        }

        final Location loc = cp.getHandle().getLocation();

        // Remove info packet
        final WrapperPlayServerPlayerInfoRemove infoRemove = new WrapperPlayServerPlayerInfoRemove(
                cp.getHandle().getUniqueId()
        );

        final ServerPlayer p = ((CraftPlayer)cp.getHandle()).getHandle();
        final net.minecraft.network.chat.RemoteChatSession chatSession = p.getChatSession();

        // Add info packet
        final WrapperPlayServerPlayerInfoUpdate info = new WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(
                                cp.getHandle().getUniqueId(),
                                cp.getHandle().getName(),
                                List.of(
                                        new TextureProperty(
                                                "textures",
                                                skin.getTexture(),
                                                skin.getSignature()
                                        )
                                )
                        ),
                        true,
                        cp.getHandle().getPing(),
                        GameMode.getById(cp.getHandle().getGameMode().getValue()),
                        Component.text(cp.getHandle().getName()),
                        // FIXME: This is not correct as the player chat session is still invalid
                        new RemoteChatSession(chatSession.sessionId(), new PublicProfileKey(
                                chatSession.profilePublicKey().data().expiresAt(),
                                chatSession.profilePublicKey().data().key(),
                                chatSession.profilePublicKey().data().keySignature()
                                )
                        )
                )
        );

        // Respawn packet
        final ServerLevel level = ((CraftWorld)loc.getWorld()).getHandle();

        final WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn(
                new Dimension(level.getLevel().dimensionType().hashCode()),
                null,
                Difficulty.getById(level.getDifficulty().getId()),
                0,
                GameMode.getById(cp.getHandle().getGameMode().ordinal()),
                null,
                false,
                true,
                false,
                null,
                null,
                null
        );

        final CommonPlayerSpawnInfo respInfo = new CommonPlayerSpawnInfo(
                level.getLevel().dimensionTypeRegistration(),
                level.getLevel().dimension(),
                level.getSeed(),
                p.gameMode.getGameModeForPlayer(),
                null,
                false, true,
                Optional.empty(),
                0
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), infoRemove);
        PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), info);
        p.connection.send(new ClientboundRespawnPacket(respInfo, (byte)0x2));


        /* TODO: Port the ProtocolWrapper code to raw Protocollib
        // Update info packet

        // Respawn packet
        final ServerLevel level = ((CraftWorld)loc.getWorld()).getHandle();
        final ServerPlayer p = ((CraftPlayer)cp.getHandle()).getHandle();

        final CommonPlayerSpawnInfo respawn = new CommonPlayerSpawnInfo(
                level.getLevel().dimensionTypeId(),
                level.dimension(),
                level.getSeed(),
                p.gameMode.getGameModeForPlayer(),
                p.gameMode.getGameModeForPlayer(),
                false,
                level.isFlat(),
                Optional.of(
                        GlobalPos.of(level.dimension(), new BlockPos(
                                p.getBlockX(),
                                p.getBlockY(),
                                p.getBlockZ())
                        )),
                0
        );

        Hunt.getProtocolManager().sendServerPacket(cp.getHandle(), infoRemove);
        Hunt.getProtocolManager().sendServerPacket(cp.getHandle(), info.getHandle());
        p.connection.send(new ClientboundRespawnPacket(respawn, (byte)0x2));*/

        final int slot = cp.getHandle().getInventory().getHeldItemSlot();
        final Collection<PotionEffect> effects = cp.getHandle().getActivePotionEffects();
        final float exp = cp.getHandle().getExp();
        final double health = cp.getHandle().getHealth();

        // Resend data
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!cp.isOnline()) return;
                final Player p = cp.getHandle();

                p.getInventory().setHeldItemSlot(slot);
                p.addPotionEffects(effects);
                p.setExp(exp);
                p.setHealth(health-0.00001);

                // Inventory
                p.openInventory(cp.getHandle().getEnderChest());
                p.closeInventory();


            }
        }.runTask(SheepWars.getPlugin());
    }

    @Override
    public @NonNull org.ef3d0c3e.sheepwars.player.skin.Skin fromPlayer(final @NonNull CPlayer cp)
    {
        final PropertyMap pm = ((CraftPlayer)cp.getHandle()).getHandle().getGameProfile().getProperties();
        final Property text = pm.get("textures").iterator().next();

        return new org.ef3d0c3e.sheepwars.player.skin.Skin(text.value(), text.signature(), "");
    }

    @Override
    public @NonNull PropertyMap getProperties(final @NonNull CPlayer cp)
    {
        return ((CraftPlayer)cp.getHandle()).getHandle().getGameProfile().getProperties();
    }
}

