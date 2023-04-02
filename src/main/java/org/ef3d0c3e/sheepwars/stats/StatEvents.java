package org.ef3d0c3e.sheepwars.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.events.*;

public class StatEvents implements Listener
{
	@EventHandler
	public void onGameStart(final GameStartEvent ev)
	{
		CPlayer.forEach(cp ->
		{
			cp.incrementStat("sw#games#played");
			cp.incrementStat("kits#" + cp.getKit().getName() + "#played");
			cp.incrementStat("maps#" + Game.getGameMap().getName() + "#played");
		});
	}

	@EventHandler
	public void onGameEnd(final GameEndEvent ev)
	{
		CPlayer.forEach(cp ->
		{
			if (cp.getTeam() == ev.getWinner())
			{
				cp.incrementStat("sw#games#won");
				cp.incrementStat("kits#" + cp.getKit().getName() + "#won");
				cp.incrementStat("maps#" + Game.getGameMap().getName() + "#won");
			}
		});
	}

	@EventHandler
	public void onSheepFire(final SheepFireEvent ev)
	{
		ev.getShooter().incrementStat("sheeps#" + ev.getSheepName() + "#used");
	}

	@EventHandler
	public void onPlayerDeath(final CPlayerDeathEvent ev)
	{
		if (ev.getPlayerKiller() != null)
			ev.getPlayerKiller().incrementStat("sw#kills#player_killed");

		ev.getVictim().incrementStat("sw#kills#death");
	}

	@EventHandler
	public void onSheepDeath(final SheepDeathEvent ev)
	{
		if (ev.getKiller() == null)
			return;

		ev.getKiller().incrementStat("sw#kills#sheep_killed");
	}

	@EventHandler
	public void onPlayerDamage(final CPlayerDamageEvent ev)
	{
		if (ev.getAttacker() != null)
			ev.getAttacker().incrementStat("sw#damage#dealt", ev.getOriginalEvent().getDamage());
		ev.getVictim().incrementStat("sw#damage#taken", ev.getOriginalEvent().getDamage());
	}
}
