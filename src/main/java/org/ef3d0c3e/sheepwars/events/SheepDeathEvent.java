package org.ef3d0c3e.sheepwars.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.builder.qual.NotCalledMethods;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Called when game starts
 */
@AllArgsConstructor
public class SheepDeathEvent extends Event
{
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	public static HandlerList getHandlerList()
	{
		return HANDLERS_LIST;
	}
	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS_LIST;
	}

	@Getter @NonNull
	private BaseSheep sheep;

	@Getter @NonNull
	List<ItemStack> drops;

	@Getter @Nullable
	private CPlayer killer;
}
