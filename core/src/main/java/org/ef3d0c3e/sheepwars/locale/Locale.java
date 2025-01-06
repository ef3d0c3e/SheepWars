package org.ef3d0c3e.sheepwars.locale;

import com.google.common.base.CaseFormat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;

public class Locale
{
    /**
     * Format the name of identifiers to YML format
     * @param name Identifier name
     * @return Identifier name in YML
     */
    private static String formatName(String name)
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name);
    }

    // Config
    public ItemStack CONFIG_BANNER;
    public String CONFIG_NAME;
    public String CONFIG_DISPLAYNAME;

    // System
    public String SYSTEM_QUIT;
    public String SYSTEM_JOIN;

    // Game
    public String GAME_MAPSTART;

    // GUIs
    public String GUI_NEXT;
    public String GUI_PREVIOUS;

    // Items
    public String ITEMS_RIGHTCLICK;
    public String ITEMS_ROCKET;
    public String ITEMS_TEAM;
    public List<String> ITEMS_TEAMLORE;
    public String ITEMS_VOTE;
    public List<String> ITEMS_VOTELORE;
    public String ITEMS_VOTEDESC;
    public String ITEMS_SKIN;
    public List<String> ITEMS_SKINLORE;
    public String ITEMS_KIT;
    public List<String> ITEMS_KITLORE;

    // Info hologram
    public String LOBBY_INFO_RADIUS;
    public String LOBBY_INFO_DURATION;

    // Scoreboard
    public String SCOREBOARD_KIT;
    public String SCOREBOARD_KITNONE;
    public String SCOREBOARD_TEAM;
    public String SCOREBOARD_VOTE;
    public String SCOREBOARD_VOTENONE;
    public String SCOREBOARD_FOOTER;

    // Skin
    public String SKIN_PICKER;
    public String SKIN_NPCNAME;
    public String SKIN_NPCCURRENT;
    public String SKIN_MENU;
    public List<String> SKIN_MENULORE;

    // Team
    public String TEAM_PICKER;
    public String TEAM_NPCNAME;
    public String TEAM_NPCCURRENT;
    public String TEAM_RED;
    public String TEAM_BLUE;

    // Kits
    public String KIT_PICKER;
    public String KIT_NPCNAME;
    public String KIT_NPCCURRENT;

    // Mage kit
    public String KIT_MAGE_NAME;
    public List<String> KIT_MAGE_DESC;

    // Vote
    public String VOTE_PICKER;
    public String VOTE_NPCNAME;
    public String VOTE_NPCCURRENT;

    // Game
    public String GAME_COUNTDOWN_WAITTITLE;
    public String GAME_COUNTDOWN_WAITSUBTITLE;
    public String GAME_COUNTDOWN_STARTTITLE;
    public String GAME_COUNTDOWN_STARTSUBTITLE;

    public String GAME_KILL_DIED;
    public String GAME_KILL_KILLED;

    // Loadouts
    public String LOADOUT_SWORD;
    public String LOADOUT_BOW;
    public String LOADOUT_ARMOR_HEAD;
    public String LOADOUT_ARMOR_BODY;
    public String LOADOUT_ARMOR_LEGGINGS;
    public String LOADOUT_ARMOR_BOOTS;

    /**
     * Constructor
     */
    public Locale()
    {
    }


    public YamlConfiguration serialize()
    {
        final YamlConfiguration cfg = new YamlConfiguration();
        final Field[] fields = Locale.class.getDeclaredFields();

        cfg.set("config.banner", CONFIG_BANNER);

        for (Field f : fields)
        {

            final String name = formatName(f.getName());
            Bukkit.getConsoleSender().sendMessage(name);
            try
            {
                if (f.getType().equals(String.class))
                    cfg.set(name, f.get(this));
                else if (f.getType().equals(List.class))
                    cfg.set(name, f.get(this));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return cfg;
    }

    public void deserialize(YamlConfiguration cfg)
    {
        CONFIG_BANNER = cfg.getItemStack("config.banner");

        final Field[] fields = Locale.class.getDeclaredFields();
        for (Field f : fields)
        {
            final String name = formatName(f.getName()).replace('-', '.');

            if (cfg.contains(name))
            {
                try
                {
                    if (f.getType().equals(String.class))
                        f.set(this, cfg.get(name));
                    else if (f.getType().equals(List.class))
                        f.set(this, cfg.getStringList(name));
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Bukkit.getConsoleSender().sendMessage(MessageFormat.format("Unknown locale string: {0}", name));
            }
        }

        // Banner name
        final ItemMeta meta = CONFIG_BANNER.getItemMeta();
        meta.setDisplayName("§9" + CONFIG_DISPLAYNAME);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        CONFIG_BANNER.setItemMeta(meta);

    }
}

