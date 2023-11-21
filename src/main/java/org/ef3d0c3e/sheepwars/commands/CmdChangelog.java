package org.ef3d0c3e.sheepwars.commands;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.ef3d0c3e.sheepwars.Util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdChangelog extends Command
{
	@Override
	public String getName()
	{
		return "changelog";
	}

	final static String pages[][] = {
		{ // Page 1
			"",
			" &9 &9 &n27 mai 2022&9: &6&oSheepWars 1.0",
			"",
		},
		{ // Page 2
			"",
			" &9 &9 &n28 juin 2022&9: &6&oSheepWars 1.1",
			"",
			"* Mise à jour pour la 1.19",
			"+ Buff du Mouton d'Échange::↑ Range: 6 → 9",
			"~ Ajustement du Mouton Soigneur::↓ Range: 7.5 → 6\n↑ Régénération: I → II\n↓ Durée de vie: ∞s → 8s",
			"* Correction du Mouton Tremblement de Terre::Il bump moins les joueurs",
		},
		{ // Page 3
			"",
			" &9 &9 &n4 avril 2023&9: &6&oSheepWars 1.2",
			"",
			"* Mise à jour pour la 1.19.4",
		},
		{ // Page 4
			"",
			" &9 &9 &n9 juillet 2023&9: &6&oSheepWars 1.3",
			"",
			"* Mise à jour pour la 1.20.1",
			"+ Ajout des Laines Bonus",
			"+ Ajout du Mouton Tsunami::● Il crée une grande vague",
			"+ Buff du kit Barbare::↑ Il obtient Dolphin's Grace",
			"~ Ajustement des dégâts de chute::↓ Régénération 65% → 55%",
		},
		{ // Page 5
			"",
			" &9 &9 &n10 juillet 2023&9: &6&oSheepWars 1.4",
			"",
			"~ Nouveau système de laines::● Click-droit sur un kit pour\n voir les laines",
			"+ Ajout de la map Dirigeables",
			"+ Ajout du Mouton Slime::● Il ralentit et tire des projectiles\nvers les ennemis",
			"- Nerf du Mouton Sombre::↓ Aveuglement permanent → 1.5s/2s",
		},
		{ // Page 6
			"",
			" &9 &9 &n14 juillet 2023&9: &6&oSheepWars 1.5",
			"",
			"~ Remake du Kit Enchanteur::● Son épée est remplacée\npar le Bâton de Lévitation",
			"+ Des laines bonus",
		},
		{ // Page 7
			"",
			" &9 &9 &n20 novembre 2023&9: &6&oSheepWars 1.6",
			"",
			"+ Langage",
		},
	};

	public CmdChangelog()
	{
		super("changelog", "Get changelog", "/changelog", Arrays.asList("ch"));
	}

	@Override
	public boolean execute(final CommandSender sender, final String label, final String[] args)
	{
		int page;
		if (args.length == 0)
			page = pages.length - 1;
		else
		{
			try
			{
				page = Integer.valueOf(args[0]) - 1;
			}
			catch (NumberFormatException e)
			{
				sender.sendMessage("§cVous devez spécifier un numéro de page!");
				return true;
			}
		}

		if (page < 0 || page >= pages.length)
		{
			sender.sendMessage(MessageFormat.format("§cLa page ''§e{0}§c'' n'existe pas.", page + 1));
			return true;
		}

		final ComponentBuilder navigator = new ComponentBuilder();
		if (page != 0)
		{
			final TextComponent arrowLeft = new TextComponent(TextComponent.fromLegacyText("§l §l §c ««« "));
			arrowLeft.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, MessageFormat.format("/ch {0}", page)));
			arrowLeft.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(MessageFormat.format("§7Page {0}", page))
			));
			navigator.append(arrowLeft);
		}
		else
		{
			TextComponent arrowLeft = new TextComponent(TextComponent.fromLegacyText("§l §l §7 ««« "));
			navigator.append(arrowLeft);
		}
		TextComponent numbers = new TextComponent(TextComponent.fromLegacyText(MessageFormat.format(
			"§6§l(§e{0}/{1}§6§l)", page +1 , pages.length
		)));
		navigator.append(numbers);
		if (page+1 != pages.length)
		{
			TextComponent arrowRight = new TextComponent(TextComponent.fromLegacyText("§c »»» "));
			arrowRight.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, MessageFormat.format("/ch {0}", page + 2)));
			arrowRight.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(MessageFormat.format("§7Page {0}", page + 2))
			));
			navigator.append(arrowRight);
		}
		else
		{
			TextComponent arrowRight = new TextComponent(TextComponent.fromLegacyText("§7 »»» "));
			navigator.append(arrowRight);
		}

		for (int i = 0; i < pages[page].length; ++i)
			sender.spigot().sendMessage(parseEntry(pages[page][i]));

		sender.spigot().sendMessage(navigator.create());

		return true;
	}

	private static Text parseDesc(String desc)
	{
		return new Text(desc
			.replace("↑", "§r§a§l↑§r§a")
			.replace("↓", "§r§c§l↓§r§c")
			.replace("●", "§r§b§l●§r§b")
		);
	}

	private static BaseComponent[] parseEntry(String entry)
	{
		ComponentBuilder builder = new ComponentBuilder();

		char prev = '\0';
		for (int i = 0; i < entry.length(); ++i)
		{
			if (entry.charAt(i) == ':' && prev == ':')
			{
				String color = null;
				switch (entry.charAt(0))
				{
					case '*':
						color = "<#2050F0>";
						break;
					case '-':
						color = "<#D06040>";
						break;
					case '+':
						color = "<#20F050>";
						break;
					case '~':
						color = "<#C0D020>";
						break;
				}

				if (color != null)
					builder.append(new TextComponent(TextComponent.fromLegacyText(Util.getColored(" " + color + entry.substring(0, i-1)))));
				else
					builder.append(new TextComponent(TextComponent.fromLegacyText(Util.getColored(entry.substring(0, i-1)))));

				TextComponent desc = new TextComponent(TextComponent.fromLegacyText(" §8[§d§oVoir§8]"));
				desc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, parseDesc(entry.substring(i+1))));
				builder.append(desc);

				break;
			}
			else if (entry.charAt(i) == ';' && prev == ';')
			{
				String color = null;
				switch (entry.charAt(0))
				{
					case '*':
						color = "<#2050F0>";
						break;
					case '-':
						color = "<#D06040>";
						break;
					case '+':
						color = "<#20F050>";
						break;
					case '~':
						color = "<#C0D020>";
						break;
				}

				if (color != null)
					builder.append(new TextComponent(TextComponent.fromLegacyText(Util.getColored(" " + color + entry.substring(0, i-1)))));
				else
					builder.append(new TextComponent(TextComponent.fromLegacyText(Util.getColored(entry.substring(0, i-1)))));

				TextComponent desc = new TextComponent(TextComponent.fromLegacyText(" §8[§b§o§nVoir§8]"));
				desc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6" + entry.substring(i+1))));
				desc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, entry.substring(i+1)));
				builder.append(desc);

				break;
			}
			else if (i == entry.length()-1)
			{
				String color = null;
				switch (entry.charAt(0))
				{
					case '*':
						color = "<#2050F0>";
						break;
					case '-':
						color = "<#D06040>";
						break;
					case '+':
						color = "<#20F050>";
						break;
					case '~':
						color = "<#C0D020>";
						break;
				}

				if (color != null)
					builder.append(new TextComponent(TextComponent.fromLegacyText(Util.getColored(" " + color + entry))));
				else
					builder.append(new TextComponent(TextComponent.fromLegacyText(Util.getColored(entry))));
				break;
			}

			prev = entry.charAt(i);
		}

		return builder.create();
	}


	@Override
	public List<String> tabComplete(final CommandSender sender, final String label, final String[] args)
	{
		ArrayList<String> l = new ArrayList<>();
		if (args.length == 1)
		{
			for (int i = 1; i <= pages.length; ++i)
				l.add(String.valueOf(i));
		}

		return l;
	}
}
