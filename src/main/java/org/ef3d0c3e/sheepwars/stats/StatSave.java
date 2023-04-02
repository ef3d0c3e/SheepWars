package org.ef3d0c3e.sheepwars.stats;

import org.bukkit.Bukkit;
import org.bukkit.util.FileUtil;
import org.codehaus.plexus.util.FileUtils;
import org.ef3d0c3e.sheepwars.CPlayer;

import java.io.*;

public class StatSave
{
	static File dir;

	/**
	 * Creates required folders
	 */
	public static void init()
	{
		dir = new File("plugins/SheepWars/stats/");
		if (!dir.exists() || !dir.isDirectory())
		{
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Creation du dossier des stats...");

			dir.mkdirs();
		}
	}

	/**
	 * Saves stats associated with a player
	 * @param cp Player to save stats for
	 * @note Called when player leaves
	 */
	public static void save(final CPlayer cp)
	{
		// Trick is to only save currently used variables (useful if we later decide to rename/remove keys)
		try
		{
			FileWriter fw = new FileWriter(dir.getPath() + "/" + cp.getHandle().getUniqueId());
			fw.write(StatMenu.SHEEPWARS.serialize(cp));
			fw.write(StatMenu.KITS.serialize(cp));
			fw.write(StatMenu.MAPS.serialize(cp));
			fw.write(StatMenu.SHEEPS.serialize(cp));
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Load stats associated with a player
	 * @param cp Player to save stats for
	 * @note Called when player joins
	 */
	public static void load(final CPlayer cp)
	{
		StatMenu.SHEEPWARS.init(cp);
		StatMenu.KITS.init(cp);
		StatMenu.MAPS.init(cp);
		StatMenu.SHEEPS.init(cp);

		// Load from disk
		try
		{
			FileReader fr = new FileReader(dir.getPath() + "/" + cp.getHandle().getUniqueId());
			String line;
			final BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null)
			{
				final int pos = line.indexOf(':');
				final String key = line.substring(0, pos);
				final String value = line.substring(pos+1);

				StatValue sv = cp.getStat(key);
				if (sv instanceof StatLong)
					((StatLong)sv).deserialize(value);
				else if (sv instanceof StatDouble)
					((StatDouble)sv).deserialize(value);
			}
			fr.close();
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}


	}
}
