package org.ef3d0c3e.sheepwars.events;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EventListenerFactory
{
    static private final HashSet<Class<? extends Listener>> registrableClasses = new HashSet<>();
    static private final HashSet<Listener> registeredClasses = new HashSet<>();

    public static void create() throws Exception
    {
        final Reflections refl = new Reflections("org.ef3d0c3e.sheepwars");

        // Classes
        final Set<Class<? extends Listener>> classes = refl.getSubTypesOf(Listener.class);
        for (final Class<? extends Listener> clz : classes)
        {
            if (clz.getAnnotation(WantsListen.class) == null)
                throw new Exception("Listener " + clz.getName() + " is missing WantsListen annotation");

            registrableClasses.add(clz);
        }
    }

    /**
     * Registers all listeners for the current phase
     * Unregisters unneeded listeners
     * @param phase Current phase
     */
    public static void update(final WantsListen.Target phase)
    {
        // Un register
        for (Iterator<Listener> it = registeredClasses.iterator(); it.hasNext();)
        {
            final Listener listener = it.next();
            final WantsListen.Target target = listener.getClass().getAnnotation(WantsListen.class).phase();

            if (target.isCompatible(phase)) continue;

            HandlerList.unregisterAll(listener);
            it.remove();
            SheepWars.debugMessage("Unregistered listener " + listener.getClass().getName());
        }

        // Register
        for (final Class<? extends Listener> clz : registrableClasses)
        {
            final WantsListen.Target target = clz.getAnnotation(WantsListen.class).phase();

            if (!target.isCompatible(phase)) continue;

            boolean shouldRegister = true;
            // Check if not already registered
            for (final Listener l : registeredClasses)
            {
                if (!l.getClass().equals(clz)) continue;

                shouldRegister = false;
                break;
            }
            if (!shouldRegister) continue;

            try
            {
                final Listener l = clz.getDeclaredConstructor().newInstance();

                registeredClasses.add(l);
                Bukkit.getPluginManager().registerEvents(l, SheepWars.getPlugin());
                SheepWars.debugMessage("Registered listener " + l.getClass().getName());
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
    }
}

