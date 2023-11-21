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
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.*;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.KitMenu;
import org.ef3d0c3e.sheepwars.kits.Kits;
import org.ef3d0c3e.sheepwars.IGui;
import org.ef3d0c3e.sheepwars.locale.LocaleMenu;
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
	@Getter
	protected static Vector lobbyOffset;
	@Getter
	protected static Vector lobbySpawn;
	@Getter
	protected static int limboHeight;
	@Getter
	private static World world = null;

	public static void init()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(SheepWars.getPlugin().getDataFolder().getAbsolutePath() + "/sw_lobby.yml"));
		Lobby.lobbyOffset = Util.parseVector(config.getString("offset"));
		Lobby.lobbySpawn = Util.parseVector(config.getString("spawn"));
		Lobby.limboHeight = config.getInt("limbo");

		// Create world
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				world = Bukkit.getWorld("lobby");
				if (world == null)
					world = Bukkit.createWorld(new WorldCreator("lobby")
						.generator(new SWChunkGenerator())
						.biomeProvider(new SWBiomeProvider())
						.seed(1)
					);

				// Post world
				world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
				world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
				world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
				world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
				world.setGameRule(GameRule.DO_INSOMNIA, false);
				world.setGameRule(GameRule.SPAWN_RADIUS, 0);
				world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
				world.setTime(6000);
				world.setWeatherDuration(0);
				world.setSpawnLocation((int)Lobby.getLobbySpawn().getX(), (int)Lobby.getLobbySpawn().getY(), (int)Lobby.getLobbySpawn().getZ());
			}
		}.runTaskLater(SheepWars.getPlugin(), 1);
	}

	/**
	 * Updates player inventory
	 * @param cp Player
	 */
	public static void updateInventory(final CPlayer cp)
	{
		cp.getHandle().getInventory().clear();
		cp.getHandle().getInventory().setItem(0, Team.getItem(cp));
		cp.getHandle().getInventory().setItem(1, KitMenu.getItem(cp));
		cp.getHandle().getInventory().setItem(4, MapMenu.getItem(cp));
		cp.getHandle().getInventory().setItem(6, LocaleMenu.getItem(cp));
		cp.getHandle().getInventory().setItem(7, Items.getSkinItem(cp));
		cp.getHandle().getInventory().setItem(8, Items.statItem);
		cp.getHandle().setGameMode(GameMode.ADVENTURE);
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
			if (!ev.isNewChunk() || ev.getChunk().getX() != 0 || ev.getChunk().getZ() != 0 || ev.getWorld() != getWorld())
				return;

			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Génération du lobby...");
			try
			{
				File lobby = new File(SheepWars.getPlugin().getDataFolder().getAbsolutePath() + "/sw_lobby.schem");
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

			ev.setTo(getWorld().getSpawnLocation());
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
		 * Give items & set gamemode
		 */
		@EventHandler(priority = EventPriority.HIGH)
		public void onJoin(final CPlayerJoinEvent ev)
		{
			final CPlayer cp = ev.getPlayer();

			Lobby.updateInventory(cp);

			if (ev.isNewPlayer())
				cp.getHandle().teleport(getWorld().getSpawnLocation());
		}

		/**
		 * Remove player's vote
		 */
		@EventHandler
		public void onQuit(final CPlayerQuitEvent ev)
		{
			final CPlayer cp = ev.getPlayer();
			Map.setVote(cp, null);
			Team.setTeam(cp, null);
		}
	}

	// TODO: Move all guis to their own classes
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
}
