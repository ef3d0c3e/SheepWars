package org.ef3d0c3e.sheepwars.level;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.*;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.Kits;
import org.ef3d0c3e.sheepwars.IGui;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

public class Lobby
{
	protected static Vector lobbyOffset;
	protected static Vector lobbySpawn;
	protected static int limboHeight;

	/**
	 * Gets height of limbo layer
	 * @return Height of limbo layer
	 */
	public static int getLimboHeight()
	{
		return limboHeight;
	}

	public static void init()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(SheepWars.plugin.getDataFolder().getAbsolutePath() + "/sw_lobby.yml"));
		Lobby.lobbyOffset = Util.parseVector(config.getString("offset"));
		Lobby.lobbySpawn = Util.parseVector(config.getString("spawn"));
		Lobby.limboHeight = config.getInt("limbo");
	}

	/**
	 * Gets lobby offset
	 * @return Lobby offset
	 */
	public static Vector getLobbyOffset()
									 {
										return lobbyOffset;
														   }

	/**
	 * Gets lobby spawn
	 * @return Lobby spawn
	 */
	public static Vector getLobbySpawn()
									{
									   return lobbySpawn;
														 }

	public static class Events implements Listener
	{
		public Events()
		{
		}

		/**
		 * Lobby generation
		 */
		@EventHandler
		public void onChunkLoad(ChunkLoadEvent ev)
		{
			if (!ev.isNewChunk() || ev.getChunk().getX() != 0 || ev.getChunk().getZ() != 0 || !ev.getWorld().getName().equals("world"))
				return;

			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Génération du lobby...");
			try
			{
				File lobby = new File(SheepWars.plugin.getDataFolder().getAbsolutePath() + "/sw_lobby.schem");
				ClipboardFormat format = ClipboardFormats.findByFile(lobby);
				ClipboardReader reader = format.getReader(new FileInputStream(lobby));
				Clipboard clipboard = reader.read();

				com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(ev.getWorld());
				EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);
				Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
					.to(BlockVector3.at(lobbyOffset.getX(), lobbyOffset.getY()+64, lobbyOffset.getZ()))
					.ignoreAirBlocks(true)
					.build();

				Operations.complete(operation);
				editSession.close();

				// Biome
				final Location origin = new Location(ev.getWorld(), lobbyOffset.getX(), lobbyOffset.getY()+64, lobbyOffset.getZ());
				Biomes.setBiome(origin, clipboard, Biomes.lobbyBiome);
			}
			catch (IOException | WorldEditException e)
			{
				e.printStackTrace();
			}
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Lobby généré!");
		}

		@EventHandler
		public void onEntityInteract(final EntityInteractEvent ev)
		{
			if (ev.getEntity() instanceof Player && ((Player)ev.getEntity()).isOp())
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onFoodLevelChange(final FoodLevelChangeEvent ev)
																{
																   ev.setCancelled(true);
																						 }

		@EventHandler
		public void onEntityAirChange(final EntityAirChangeEvent ev)
																{
																   ev.setCancelled(true);
																						 }

		@EventHandler
		public void onEntityMount(final EntityMountEvent ev)
		{
			if (((CraftEntity)ev.getMount()).getHandle() instanceof BaseSheep)
				return;
		   	ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityCombust(final EntityCombustEvent ev)
															{
															   ev.setCancelled(true);
																					 }

		@EventHandler
		public void onEntityDamage(final EntityDamageEvent ev)
		{
			if (ev instanceof EntityDamageByEntityEvent)
				return;
			ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityDamageByEntity(final EntityDamageByEntityEvent ev)
		{
			if (ev.getDamager() instanceof Player && (ev.getDamager().isOp()))
				return;
			ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityDismount(final EntityDismountEvent ev)
		{
			if (((CraftEntity)ev.getDismounted()).getHandle() instanceof BaseSheep)
				return;
			ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityDropItem(final EntityDropItemEvent ev)
															  {
																 ev.setCancelled(true);
																					   }

		@EventHandler
		public void onPlayerDropItem(final PlayerDropItemEvent ev)
		{
			if (ev.getPlayer().isOp())
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityExhaust(final EntityExhaustionEvent ev)
															   {
																  ev.setCancelled(true);
																						}

		@EventHandler
		public void onEntityPickupItem(final EntityPickupItemEvent ev)
		{
			if (ev.getEntity() instanceof Player && ((Player)ev.getEntity()).isOp())
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityPlace(final EntityPlaceEvent ev)
		{
			if (ev.getEntity() instanceof Player && ((Player)ev.getEntity()).isOp())
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onBlockBreak(final BlockBreakEvent ev)
		{
			if (((Player)ev.getPlayer()).isOp())
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onCraftItem(final CraftItemEvent ev)
													{
													   ev.setCancelled(true);
																			 }

		@EventHandler
		public void onPlayerMoveEvent(final PlayerMoveEvent ev)
		{
			if (ev.getTo().getY() > limboHeight)
				return;

			ev.setTo(Game.getLobby().getSpawnLocation());
		}

		// TODO
		//@EventHandler
		//public void onBlockFromTo(final BlockFromToEvent ev)
		//{
		//}

		//@EventHandler
		//public void onPiston(final BlockPistonEvent ev)
		//{
		//	ev.setCancelled(true);
		//}

		/**
		 * Give items
		 */
		@EventHandler(priority = EventPriority.HIGH)
		public void onJoin(final CPlayerJoinEvent ev)
		{
			final CPlayer cp = ev.getPlayer();

			cp.getHandle().getInventory().clear();
			cp.getHandle().getInventory().setItem(0, Items.getTeamItem(cp));
			cp.getHandle().getInventory().setItem(1, Items.getKitItem(cp));
			cp.getHandle().getInventory().setItem(4, Items.voteItem);
			cp.getHandle().getInventory().setItem(7, Items.getSkinItem(cp));
			cp.getHandle().getInventory().setItem(8, Items.statItem);
			cp.getHandle().setGameMode(GameMode.ADVENTURE);

			if (ev.isNewPlayer())
				cp.getHandle().teleport(Game.getLobby().getSpawnLocation());
		}

		/**
		 * Remove vote
		 */
		@EventHandler
		public void onQuit(final CPlayerQuitEvent ev)
		{
			final CPlayer cp = ev.getPlayer();
			Map.setVote(cp, null);
			Team.setTeam(cp, null);
		}

		@EventHandler
		public void onTeamItemUse(final PlayerInteractEvent ev)
		{
			if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;
			if (ev.getItem() == null || !Items.is(Items.ID.TEAM, ev.getItem()))
				return;

			ev.setCancelled(true);
			final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());

			// Change team (get next team)
			for (int i = 0; i < Team.getTeamList().size(); ++i)
			{
				if (cp.getTeam() != Team.getTeamList().get(i))
					continue;

				Team.setTeam(cp, Team.getTeamList().get( (i+1) % Team.getTeamList().size() ));
				CPlayer.forEach((cp2) -> cp2.updateScoreboard()); // Update for everyone
				cp.updateTabname();
				cp.updateNametag();
				break;
			}

			// Change wool type
			Items.remove(cp, Items.ID.TEAM);
			cp.getHandle().getInventory().addItem(Items.getTeamItem(cp));
		}

		/**
		 * Open inventories
		 * @param ev Event
		 */
		@EventHandler
		public void onKitItemUse(final PlayerInteractEvent ev)
		{
			if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK || ev.getItem() == null)
				return;
			final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());
			if (Items.is(Items.ID.KIT, ev.getItem()))
			{
				ev.setCancelled(true);
				cp.getHandle().openInventory(new KitGui(cp).getInventory());
			}
			else if (Items.is(Items.ID.VOTE, ev.getItem()))
			{
				ev.setCancelled(true);
				cp.getHandle().openInventory(new VoteGui(cp).getInventory());
			}

		}
	}

	@AllArgsConstructor
	public static class VoteGui implements IGui
	{
		@Getter
		private CPlayer cp;
		@Override
		public void onGuiClose(final Player p) {}
		@Override
		public void onGuiDrag(final Player p, final InventoryDragEvent ev) {}

		@Override
		public void onGuiClick(final Player p, final ClickType click, final int slot, final ItemStack item)
		{
			if (item == null || item.getType() == Material.AIR)
				return;

			final CPlayer cp = CPlayer.getPlayer(p);

			// Set voted map
			for (final Map map : Map.getMaps().values())
			{
				if (!item.getItemMeta().getDisplayName().endsWith(map.getDisplayName()))
					continue;

				Map.setVote(cp, map);
				break;
			}

			// Update inventory
			p.openInventory(getInventory());

			cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 65536.f, 1.4f);
		}

		@Override
		public Inventory getInventory()
		{
			final Inventory inv = Bukkit.createInventory(this, (Map.getMaps().size() / 9 + 1) * 9, "§9Votez pour une carte");
			for (final Map map : Map.getMaps().values())
			{
				final ItemStack item = Items.createItem(map.getIcon(), "§a" + map.getDisplayName(),
					MessageFormat.format("§7 - Vote(s): §e{0}", map.getVote()));
				item.setAmount(Math.max(1, Math.min(64, map.getVote())));
				if (Map.votedFor(cp, map)) // Glitter
				{
					ItemMeta meta = item.getItemMeta();
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}

				inv.addItem(item);
			}

			return inv;
		}
	}

	@AllArgsConstructor
	public static class KitGui implements IGui
	{
		@Getter
		private CPlayer cp;
		@Override
		public void onGuiClose(final Player p) {}
		@Override
		public void onGuiDrag(final Player p, final InventoryDragEvent ev) {}

		@Override
		public void onGuiClick(final Player p, final ClickType click, final int slot, final ItemStack item)
		{
			if (item == null || item.getType() == Material.AIR)
				return;

			final Kit kit = Kits.list.get(slot);
			final CPlayer cp = CPlayer.getPlayer(p);

			// Set kit
			try
			{
				cp.setKit(kit.getClass().getDeclaredConstructor().newInstance());
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				e.printStackTrace();
			}

			cp.updateScoreboard();
			cp.updateTabname();

			// Update inventory
			p.openInventory(getInventory());

			// Change items
			Items.remove(cp, Items.ID.KIT);
			cp.getHandle().getInventory().addItem(Items.getKitItem(cp));

			cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 65536.f, 1.4f);
		}

		@Override
		public Inventory getInventory()
		{
			final Inventory inv = Bukkit.createInventory(this, (Kits.list.size() / 9 + 1) * 9, "§9Choisissez un kit");
			for (final Kit kit : Kits.list)
			{
				if (cp.getKit().getClass() == kit.getClass())
				{
					ItemStack item = kit.getDisplayItem().clone();
					ItemMeta meta = item.getItemMeta();
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					inv.addItem(item);
				}
				else
					inv.addItem(kit.getDisplayItem());
			}

			return inv;
		}
	}
}
