package org.ef3d0c3e.sheepwars.versions;

import org.bukkit.Bukkit;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WrapperFactory
{
    private static void resolve(final String version, final Field wrapperField) throws Exception
    {
        final AutoWrapper wrapper = wrapperField.getAnnotation(AutoWrapper.class);
        wrapperField.setAccessible(true);

        // Resolve correct wrapper
        final Class<?> wrapperClz = Class.forName("org.ef3d0c3e.sheepwars.v" + version + "." + wrapper.name());

        // Check if wrapper implementation implements abstract wrapper class
        boolean valid = false;
        for (final Class<?> inter : wrapperClz.getInterfaces())
        {
            if (!inter.equals(wrapperField.getType())) continue;

            valid = true;
            break;
        }
        if (!valid) throw new RuntimeException("Wrapper implementation " + wrapperClz.getName() + " does not implement " + wrapperField.getType().getName());

        // Set wrapper field
        wrapperField.set(null, wrapperClz.getDeclaredConstructor().newInstance());

        SheepWars.debugMessage("Wrapper for " + wrapperField.getDeclaringClass().getName() + " set to " + wrapperClz.getName());
    }

    public static void resolveWrappers() throws Exception
    {
        // Build version string (e.g: "1.20.2" -> "1_20_R2")
        final String bukkitVersion = Bukkit.getVersion();
        Bukkit.getConsoleSender().sendMessage("version=" + bukkitVersion);
        Matcher matcher = Pattern.compile("\\(MC: (?<version>\\d)\\.(?<major>\\d+)(?:\\.(?<minor>\\d+))?\\)").matcher(bukkitVersion);
        if (!matcher.find()) throw new RuntimeException("Could not determine minecraft version from Bukkit version: `" + bukkitVersion + "`");
        String version;
        if (matcher.group("minor") == null)
            version = matcher.group("version") + "_" + matcher.group("major")  + "_R0";
        else
            version = matcher.group("version") + "_" + matcher.group("major")  + "_R" + matcher.group("minor");

        SheepWars.debugMessage("Resolving wrappers for NMS " + version + "...");

        // Loop annotated fields
        final Reflections refl = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("org.ef3d0c3e.sheepwars")
                        .filterInputsBy(new FilterBuilder().includePackage("org.ef3d0c3e.sheepwars"))
                        .setScanners(Scanners.FieldsAnnotated));

        for (final Field wrapperField : refl.getFieldsAnnotatedWith(AutoWrapper.class))
            resolve(version, wrapperField);
    }
}

