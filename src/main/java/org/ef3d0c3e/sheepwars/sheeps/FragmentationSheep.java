package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class FragmentationSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.GRAY_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#373737>Fragmentation"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.GRAY;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Fragmentation"));
		name.setStyle(Style.EMPTY.withColor(0x373737));
		return name;
	}

	static public ItemStack getItem()
	{
		return ITEM;
	}

	public ItemStack getDrop()
	{
		return getItem();
	}

	protected void spawnParticles(final int time)
	{
		final World world = (World)level().getWorld();
		world.spawnParticle(Particle.EXPLOSION_NORMAL, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int fuse = 0;

	public FragmentationSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (onGround())
			++grounded;

		if (grounded >= 5)
		{
			++fuse;
			if ((fuse / 8) % 2 == 0)
				setColor(DyeColor.WHITE);
			else
				setColor(getColor());

			if (fuse == 40)
			{
				this.remove(RemovalReason.DISCARDED);
				this.level().explode(this, getX(), getY(), getZ(), 5, false, Level.ExplosionInteraction.BLOCK);

				final Location loc = new Location((World)level().getWorld(), getX(), getY(), getZ());
				for (int i = 0; i < 7; ++i)
				{
					final Vector dir = new Vector(
						Math.random() * 2.0 - 1.0,
						Math.random() * 0.5 + 0.5,
						Math.random() * 2.0 - 1.0
					);

					final FragmentationBabySheep baby = new FragmentationBabySheep(loc, owner);
					baby.spawn();
					baby.launch(dir, 1, 1);
				}
			}
		}
	}

	static public class Events implements Listener
	{
		/**
		 * Prevents frag baby to take explosion damage
		 */
		@EventHandler
		public void onFragBabyDamage(final EntityDamageEvent ev)
		{
			if (!(((CraftEntity)ev.getEntity()).getHandle() instanceof FragmentationBabySheep)
				|| ev.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
				return;
			ev.setCancelled(true);
		}
	}
}
