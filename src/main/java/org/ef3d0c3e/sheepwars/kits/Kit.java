package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class Kit
{

	public abstract String getName();
	public abstract String getColoredName();
	public abstract ItemStack getDisplayItem();

	ArrayList<ItemStack> sheeps;
	public ArrayList<ItemStack> getSheeps()
	{
		return sheeps;
	}
	public double getAdditionalSheepChance()
	{
		return 0.0;
	}

	public Kit()
	{
		sheeps = new ArrayList<>();
		for (Pair<ItemStack, Class<? extends BaseSheep>> pair : Sheeps.list.values())
		{
			try
			{
				sheeps.add((ItemStack) pair.getB().getDeclaredMethod("getItem").invoke(null));
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}
}
