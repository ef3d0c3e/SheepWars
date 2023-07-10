package org.ef3d0c3e.sheepwars;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for resources that refill
 */
public class RefilledResource
{
	public interface RefilledRate
	{
		public int operation(CPlayer cp);
	}

	public interface RefilledMax
	{
		public int operation(CPlayer cp);
	}

	public interface RefilledPredicate
	{
		public boolean operation(CPlayer cp);
	}

	@Getter @Setter
	private int delay; ///< Refill delay in tick
	@Getter @Setter
	private int preferredSlot; ///< Preferred slot
	@Getter @Setter
	private RefilledRate rate; ///< Refill rate
	@Getter @Setter
	private ItemStack item; ///< Item to refill
	@Getter @Setter
	private RefilledMax max; ///< Max amount
	@Getter @Setter
	private RefilledPredicate predicate; ///< Player predicate (already checks for alive & online)
	@Getter @Setter
	private boolean fillFirst; ///< Whether to fill all at tick 0

	public RefilledResource(final int delay, final ItemStack item, final RefilledMax max, final RefilledPredicate predicate)
	{
		this.delay = delay;
		this.preferredSlot = -1;
		this.rate = cp -> 1;
		this.item = item;
		this.max = max;
		this.predicate = predicate;
		fillFirst = true;
	}

	public RefilledResource(final int delay, final ItemStack item, final RefilledMax max, final RefilledPredicate predicate, final int preferredSlot)
	{
		this.delay = delay;
		this.preferredSlot = preferredSlot;
		this.rate = cp -> 1;
		this.item = item;
		this.max = max;
		this.predicate = predicate;
		fillFirst = true;
	}

	/**
	 * Run every tick to update resource
	 * @param tick Elapsed ticks
	 */
	private void tick(final int tick)
	{
		if (tick % delay == 0)
		{
			CPlayer.forEach(cp ->
			{
				if (!cp.isAlive() || !cp.isOnline() || !predicate.operation(cp))
					return;
				final Inventory inv = cp.getHandle().getInventory();

				HashMap<Integer, ? extends ItemStack> items = inv.all(item.getType());
				if (items.isEmpty()) // No items
				{
					if (preferredSlot != -1) // Attempt to put in preferred slot
					{
						if (inv.getItem(preferredSlot) == null)
						{
							final int prate = (fillFirst && tick == 0) ? max.operation(cp) : rate.operation(cp);
							int filled = 0;
							int toFill = Math.min(item.getMaxStackSize(), prate - filled);
							inv.setItem(preferredSlot, item);
							inv.getItem(preferredSlot).setAmount(toFill);
							filled += toFill;

							while (filled != prate)
							{
								toFill = Math.min(item.getMaxStackSize(), prate - filled);
								final ItemStack item_ = item.clone();
								item_.setAmount(toFill);
								inv.addItem(item_);
								filled += toFill;
							}
						}
						else // Put somewhere else
						{
							final int prate = (fillFirst && tick == 0) ? max.operation(cp) : rate.operation(cp);
							int filled = 0;
							while (filled != prate)
							{
								final int toFill = Math.min(item.getMaxStackSize(), prate - filled);
								final ItemStack item_ = item.clone();
								item_.setAmount(toFill);
								inv.addItem(item_);
								filled += toFill;
							}
						}
					}
					else
					{
						final int prate = (fillFirst && tick == 0) ? max.operation(cp) : rate.operation(cp);
						int filled = 0;
						while (filled != prate)
						{
							final int toFill = Math.min(item.getMaxStackSize(), prate - filled);
							final ItemStack item_ = item.clone();
							item_.setAmount(toFill);
							inv.addItem(item_);
							filled += toFill;
						}
					}
				}
				else
				{
					AtomicInteger count = new AtomicInteger();
					items.forEach((slot, item_) -> count.addAndGet( item_.getAmount() ));

					final int prate = (fillFirst && tick == 0) ? max.operation(cp) : rate.operation(cp);
					final int pmax = max.operation(cp);
					int filled = 0;
					if (count.get() < pmax)
					{
						for (final ItemStack item_ : items.values())
						{
							if (filled == prate)
								break;
							if (!item_.isSimilar(item))
								continue;

							final int toFill = Math.min(item_.getMaxStackSize(), prate - filled) - item_.getAmount(); // How much can actually be added
							item_.setAmount(item_.getAmount() + toFill);
							filled += toFill;
						}

						// Leftover
						while (filled != prate)
						{
							final int toFill = Math.min(item.getMaxStackSize(), prate - filled);
							final ItemStack item_ = item.clone();
							item_.setAmount(toFill);
							inv.addItem(item_);
							filled += toFill;
						}
					}
				}
			});
		}
	}

	static private ArrayList<RefilledResource> resources = new ArrayList<>();

	/**
	 * Adds resource to list of automatically ticked resources
	 * @param resource Resource to tick
	 */
	static public void addResource(final RefilledResource resource)
	{
		resources.add(resource);
	}

	static public class Events implements Listener
	{
		/**
		 * Register runnable
		 */
		public Events()
		{
			new BukkitRunnable()
			{
				int tick = 0;
				@Override
				public void run()
				{
					for (final RefilledResource resource : resources)
						resource.tick(tick);
					++tick;
				}
			}.runTaskTimer(SheepWars.getPlugin(), 0, 1);
		}
	}
}
