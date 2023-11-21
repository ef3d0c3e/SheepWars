package org.ef3d0c3e.sheepwars;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util
{
	/**
	 * Gets whether material is blast resistant
	 * @param mat Materiel to check
	 * @return True if material is blast-resistant, false otherwise
	 */
	public static boolean isBlastResistant(final Material mat)
	{
		return mat.getBlastResistance() >= Material.OBSIDIAN.getBlastResistance();
	}

	/**
	 * Translates 24-bit hex color codes to colors
	 * @param startTag Begin delimiter
	 * @param endTag End delimiter
	 * @param message Message to colorize
	 * @return Colorized message
	 */
	public static String translateHexColorCodes(final String startTag, final String endTag, final String message)
	{
		final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag); // 24bit hexadecimal
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find())
		{
			String group = matcher.group(1);
			matcher.appendReplacement(buffer,
				ChatColor.COLOR_CHAR + "x" + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR
					+ group.charAt(1) + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR
					+ group.charAt(3) + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR
					+ group.charAt(5));
		}
		return matcher.appendTail(buffer).toString();
	}

	/**
	 * Parses string to vector
	 * @param text Text in the form ".5 7.5 -10"
	 * @return Vector
	 */
	public static Vector parseVector(final String text)
	{
		String[] split = text.split(" ", 3);

		return new Vector(Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]));
	}

	/**
	 * Parses string to color
	 * @param text Text in the forme "127 0 11"
	 * @return Color
	 */
	public static int parseColor(final String text)
	{
		String[] split = text.split(" ", 3);

		return
			(Integer.valueOf(split[0]) << 16) |
			(Integer.valueOf(split[1]) << 8) |
			(Integer.valueOf(split[2]));
	}


	/**
	 * Gets colored string from in-game "inputable" string
	 * @param message Original string
	 * @return Colored string
	 */
	public static String getColored(final String message)
	{
		return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes("<#", ">", message));
	}


	/**
	 * Interface for `forEachPlayer` parameters
	 */
	public interface ForEachBlockInClipboard
	{
		public void operation(int x, int y, int z);
	}

	public static void forEachBlockInClipboard(final Clipboard clipboard, ForEachBlockInClipboard f)
	{
		for (int x = clipboard.getMinimumPoint().getX(); x <= clipboard.getMaximumPoint().getX(); ++x)
			for (int y = clipboard.getMinimumPoint().getY(); y <= clipboard.getMaximumPoint().getY(); ++y)
				for (int z = clipboard.getMinimumPoint().getZ(); z <= clipboard.getMaximumPoint().getZ(); ++z)
					f.operation(x, y, z);
	}

	/**
	 * Send error message
	 * @param sender Send to
	 * @param message Message to send
	 */
	public static void errorMessage(final CommandSender sender, final String message)
	{
		sender.sendMessage(MessageFormat.format("§cErreur> {0}", message));
	}

	/**
	 * Gets player attacker from event
	 * @param ev Event
	 * @return CPlayer if found, null otherwise
	 */
	public static CPlayer getPlayerAttacker(final EntityDamageByEntityEvent ev)
	{
		if (ev.getDamager() instanceof Player)
			return CPlayer.getPlayer((Player)ev.getDamager());
		else if (ev.getDamager() instanceof Projectile)
		{
			final ProjectileSource shooter = ((Projectile)ev.getDamager()).getShooter();
			if (shooter instanceof Player)
				return CPlayer.getPlayer((Player)shooter);
			else
				return null;
		}
		else if (ev.getDamager() instanceof Tameable)
		{
			final AnimalTamer owner = ((Tameable)ev.getDamager()).getOwner();
			if (owner instanceof Player)
				return CPlayer.getPlayer((Player)owner);
			else
				return null;
		}
		else if (((CraftEntity)ev.getDamager()).getHandle() instanceof BaseSheep)
		{
			return ((BaseSheep)((CraftEntity)ev.getDamager()).getHandle()).getOwner();
		}
		else
			return null;
	}

	/**
	 * Gets random location within rectangle cuboid
	 * @param lowest Cuboid's lowest edge
	 * @param highest Cuboid highest edge
	 * @return Random location inside rectangle cuboid
	 */
	public static Vector getRandomWithinCuboid(final Vector lowest, final Vector highest)
	{
		return new Vector(
			Math.random()*(highest.getX() - lowest.getX()),
			Math.random()*(highest.getY() - lowest.getY()),
			Math.random()*(highest.getZ() - lowest.getZ())
		).add(lowest);
	}


	/**
	 * Gets a random location within a ball
	 * @param center Ball's center
	 * @param radius Ball's radius
	 * @return Random location inside ball
	 */
	public static Location getRandomWithinBall(final Location center, final double radius)
	{
		final double r = Math.random() * radius;
		final double t = Math.random() * Math.PI * 2.0;
		final double p = Math.random() * Math.PI - Math.PI / 2.0;

		final double st = Math.sin(t);
		final double ct = Math.cos(t);
		final double sp = Math.sin(p);
		final double cp = Math.cos(p);

		return center.add(r*st*ct, r*st*sp, r*cp);
	}

	public interface RunInCircle
	{
		void operation(final Location loc, final double t, final int i);
	}

	/**
	 * Execute operation in circle
	 * @param center Circle's center
	 * @param normal Circle's normal
	 * @param radius Circle's radius
	 * @param points Number of points to run it at
	 * @param f Callback
	 */
	public static void runInCircle(final Location center, final Vector normal, final double radius, final int points, final RunInCircle f)
	{
		Vector u = new Vector(1, 0, 0);
		if (normal.dot(u) != 0)
		 	u = new Vector(0, 1, 0);

		final Vector a = normal.clone().crossProduct(u);
		final Vector b = normal.clone().crossProduct(a);

		for (int i = 0; i < points; ++i)
		{
			final double t = Math.PI * 2 * Double.valueOf(i) / Double.valueOf(points);

			final Location loc = center.clone().add(
				radius * Math.cos(t) * a.getX() + radius * Math.sin(t) * b.getX(),
				radius * Math.cos(t) * a.getY() + radius * Math.sin(t) * b.getY(),
				radius * Math.cos(t) * a.getZ() + radius * Math.sin(t) * b.getZ());

			f.operation(loc, t, i);
		}
	}

	public interface RunInLine
	{
		void operation(final Location loc, final int i);
	}

	/**
	 * Execute operation in line
	 * @param begin Line's begin position
	 * @param end Line's end position
	 * @param points Number of points to run it at
	 * @param f Callback
	 * @note Will ignore end's world
	 */
	public static void runInLine(final Location begin, final Location end, final int points, final RunInLine f)
	{
		final Vector step = new Vector(
			(end.getX() - begin.getX()) / Double.valueOf(points),
			(end.getY() - begin.getY()) / Double.valueOf(points),
			(end.getZ() - begin.getZ()) / Double.valueOf(points)
		);

		for (int i = 0; i < points; ++i)
		{
			final Location loc = begin.clone().add(step.clone().multiply(i));

			f.operation(loc, i);
		}
	}

	public interface RunInSphereBlock
	{
		void operation(final Block b);
	}

	public static void runInSphereBlock(final Location center, final double radius, final RunInSphereBlock f)
	{
		final World world = center.getWorld();
		final double radiusSq = radius * radius;

		final int xMin = (int)((double)center.getBlockX() + 0.5 - radius);
		final int xMax = (int)((double)center.getBlockX() + 0.5 + radius);
		final int yMin = (int)((double)center.getBlockY() + 0.5 - radius);
		final int yMax = (int)((double)center.getBlockY() + 0.5 + radius);
		final int zMin = (int)((double)center.getBlockZ() + 0.5 - radius);
		final int zMax = (int)((double)center.getBlockZ() + 0.5 + radius);

		for (int x = xMin - center.getBlockX(); x <= xMax - center.getBlockX(); ++x)
		{
			for (int y = yMin - center.getBlockY(); y <= yMax - center.getBlockY(); ++y)
			{
				if (x * x + y * y > radiusSq)
					continue;

				for (int z = zMin - center.getBlockZ(); z <= zMax - center.getBlockZ(); ++z)
				{
					if (x * x + y * y + z * z > radiusSq)
						continue;

					f.operation(world.getBlockAt(
						center.getBlockX() + x,
						center.getBlockY() + y,
						center.getBlockZ() + z));
				}
			}
		}
	}
}
