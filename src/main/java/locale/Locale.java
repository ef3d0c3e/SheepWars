package locale;

import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.text.MessageFormat;

/**
 * Class that defines all strings to be localized
 */
public class Locale
{
	/**
	 * @brief Constructor
	 */
	public Locale(final YamlConfiguration config)
	{
		for (Field field : Locale.class.getDeclaredFields())
		{
			//field.setAccessible(true);
			final String ymlName = field.getName().toLowerCase(java.util.Locale.ENGLISH).replace('_', ':');

			try
			{
				field.set(this, config.get(ymlName));
				Bukkit.getConsoleSender().sendMessage(MessageFormat.format(" - {0} : \"{1}\"", field.getName(), field.get(this)));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public String SYSTEM_KICK;

	public String SYSTEM_RELOAD;
}
