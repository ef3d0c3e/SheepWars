package org.ef3d0c3e.sheepwars.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.packets.ArmorStandMetadata;
import org.ef3d0c3e.sheepwars.packets.EntityMetadata;
import org.ef3d0c3e.sheepwars.packets.PlayerMetadata;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;
import java.util.*;

public abstract class PlayerNPC
{
	/**
	 * The NPC's unique id
	 */
	@Getter
	private final UUID uniqueId;
	@Getter
	private final int networkId;
	@Getter
	private final HashMap<CPlayer, Long> lastInteracted;
	@Getter
	private final ArrayList<Integer> availableNetworkIds = new ArrayList<>();

	/**
	 * Reserves network ids for later use
	 * @param amount Amount to reserve
	 */
	private void reserveNetworkIds(int amount)
	{
		while (availableNetworkIds.size() < amount)
			availableNetworkIds.add(SheepWars.getNextEntityId());
	}

	public long lastInteracted(@NonNull CPlayer cp) {
		final Object last = lastInteracted.get(cp);
		if (last == null)
			return 0;
		else
			return (long)last;
	}

	public void setLastInteracted(@NonNull  CPlayer cp) {
		lastInteracted.put(cp, System.currentTimeMillis());
	}

	/**
	 * Constructor
	 * @param networkId NPC (unique) network id
	 */
	public PlayerNPC(final int networkId)
	{
		uniqueId = UUID.randomUUID();
		this.networkId = networkId;
		lastInteracted = new HashMap<>();
	}

	protected abstract @NonNull String getName();
	protected abstract @NonNull List<Component> getNametag(final @NonNull CPlayer cp);
	protected abstract @NonNull Property getTextures(final @NonNull CPlayer cp);
	protected abstract @NonNull Location getLocation(final @NonNull CPlayer cp);
	protected abstract boolean sendPredicate(final @NonNull CPlayer cp);
	protected @NonNull List<EntityData> getMetadata(final @NonNull CPlayer cp)
	{
		return List.of(
				new PlayerMetadata.SkinParts()
						.all()
						.into()
		);
	}

	/**
	 * Updates the NPC (resend metadata, skin profile etc...)
	 * @param cp Player to update NPC for
	 */
	protected void update(final @NonNull CPlayer cp) {}
	// TODO: add more methods, such as inventory, potion effects...

	/**
	 * Called when NPC is interacted with (Right click)
	 * @param cp Player that interacted
	 * @param hand Interacted hand
	 * @param sneaking Whether player is sneaking
	 */
	protected abstract void onInteract(final @NonNull CPlayer cp, final EnumWrappers.Hand hand, boolean sneaking);

	protected void send(final @NonNull CPlayer cp)
	{
		// Spawn packet
		final Location loc = getLocation(cp);
		final WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
				networkId, Optional.of(uniqueId),
				EntityTypes.PLAYER,
				new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
				loc.getPitch(), loc.getYaw(), loc.getYaw(),
				0,
				Optional.empty()
		);

		// metadata
		final WrapperPlayServerEntityMetadata playerMeta = new WrapperPlayServerEntityMetadata(
				networkId,
				getMetadata(cp)
		);

		// Team packet (to hide the nametag)
		final WrapperPlayServerTeams team = new WrapperPlayServerTeams(
				"NPC-" + getName(),
				WrapperPlayServerTeams.TeamMode.CREATE,
				new WrapperPlayServerTeams.ScoreBoardTeamInfo(
						Component.empty(),
						null, null,
						WrapperPlayServerTeams.NameTagVisibility.NEVER,
						WrapperPlayServerTeams.CollisionRule.NEVER,
						NamedTextColor.WHITE,
						WrapperPlayServerTeams.OptionData.NONE
				),
				Collections.singletonList("NPC-" + getName())
		);

		PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), spawn);
		PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), playerMeta);
		PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), team);
	}

	/**
	 * Removes NPC nametag for player
	 * @note {@see sendNametag(cp)} should be called after this method
	 * @param cp Player
	 * @param number Number of nametags to remove
	 */
	protected void removeNametag(final @NonNull CPlayer cp, int number)
	{
		final WrapperPlayServerDestroyEntities remove = new WrapperPlayServerDestroyEntities();
		remove.setEntityIds(availableNetworkIds.stream().mapToInt(Integer::intValue).toArray());

		PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), remove);
	}

	protected void sendNametag(final @NonNull CPlayer cp)
	{
		final List<Component> tags = getNametag(cp);
		int i = 0;
		for (final Component tag : tags)
		{
			// Reserve a new network id
			reserveNetworkIds(i + 1);

			// Spawn
			final Location loc = getLocation(cp);
			final WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                    availableNetworkIds.get(i),
					Optional.of(UUID.randomUUID()),
					EntityTypes.ARMOR_STAND,
					new Vector3d(loc.getX(), loc.getY()+(tags.size()-i-1)*0.3+1.80, loc.getZ()),
					0.f, 0.f, 0.f,
					0,
					Optional.empty()
			);
			// Metadata
			final WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata(
                    availableNetworkIds.get(i),
					Arrays.asList(
							new EntityMetadata.Status()
									.isInvisible(true)
									.into(),
							new EntityMetadata.NoGravity(true).into(),
							new EntityMetadata.CustomNameVisible(true).into(),
							new EntityMetadata.CustomName(tag).into(),
							new ArmorStandMetadata.Status()
									.isMarker(true)
									.into()
					)
			);

			PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), spawn);
			PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), meta);

			++i;
		}
	}

	/**
	 * Sends player info (profile)
	 * @note Setting {@ref resend} to true will send a destroy packet and then resend the player packet
	 * @param cp Player to send to
	 * @param resend If true, will send a {@code PlayerInfoRemove} packet first
	 */
	protected void sendInfo(final @NonNull CPlayer cp, boolean resend)
	{
		// Remove any previously sent NPC
		if (resend)
		{
			final WrapperPlayServerPlayerInfoRemove infoRemove = new WrapperPlayServerPlayerInfoRemove(getUniqueId());
			final WrapperPlayServerDestroyEntities remove = new WrapperPlayServerDestroyEntities(networkId);

			PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), infoRemove);
			PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), remove);
		}

		final Property texture = getTextures(cp);
		final WrapperPlayServerPlayerInfoUpdate info = new WrapperPlayServerPlayerInfoUpdate(
				WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
				new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
						new UserProfile(
								uniqueId,
								"NPC-" + getName(),
								List.of(
										new TextureProperty(
												texture.name(),
												texture.value(),
												texture.signature()
										)
								)
						),
						false,
						0,
						GameMode.SURVIVAL,
						null,
						null
				)
		);

		PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), info);

		// Also resend nametag
		if (resend)
			send(cp);
	}
}
