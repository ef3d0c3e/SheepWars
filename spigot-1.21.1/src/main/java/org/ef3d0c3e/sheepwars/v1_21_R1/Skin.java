package org.ef3d0c3e.sheepwars.v1_21_R1;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.versions.SkinVersionWrapper;

import java.util.Arrays;

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
        final PacketContainer infoRemove = SheepWars.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        infoRemove.getUUIDLists().write(0, Arrays.asList(cp.getHandle().getUniqueId()));

        /* TODO: Port the ProtocolWrapper code to raw Protocollib
        // Update info packet
        final WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        info.setActions(Sets.newHashSet(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED));

        final PlayerInfoData data = new PlayerInfoData(
                cp.getHandle().getUniqueId(),
                0,
                true,
                EnumWrappers.NativeGameMode.fromBukkit(cp.getHandle().getGameMode()),
                WrappedGameProfile.fromPlayer(cp.getHandle()),
                WrappedChatComponent.fromText(cp.getHandle().getName())
        );
        info.setEntries(Collections.singletonList(data));

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
        p.connection.send(new ClientboundRespawnPacket(respawn, (byte)0x2));

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
                final Player p = cp.getHandle();

                p.getInventory().setHeldItemSlot(slot);
                p.addPotionEffects(effects);
                p.setExp(exp);
                p.setHealth(health-0.00001);

                // Inventory
                p.openInventory(cp.getHandle().getEnderChest());
                p.closeInventory();


            }
        }.runTask(Hunt.getPlugin());
         */
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

