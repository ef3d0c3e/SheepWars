package org.ef3d0c3e.sheepwars.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PacketListenerFactory
{
    static private HashSet<Class<? extends PacketListener>> registrable = new HashSet<>();
    static private HashSet<PacketListener> registered = new HashSet<>();


    public static void create() throws Exception
    {
        final Reflections refl = new Reflections("org.ef3d0c3e.sheepwars");
        final Set<Class<? extends PacketListener>> classes = refl.getSubTypesOf(PacketListener.class);
        for (final Class<? extends PacketListener> clz : classes)
        {
            if (clz.getAnnotation(WantsListen.class) == null)
                continue;

            registrable.add(clz);
        }
    }

    /**
     * Registers all listeners for current phase
     * Unregisters uneeded listeners
     * @param phase Current phase
     */
    public static void update(final WantsListen.Target phase)
    {
        // Un register
        for (Iterator<PacketListener> it = registered.iterator(); it.hasNext();)
        {
            final PacketListener listener = it.next();
            final WantsListen.Target target = listener.getClass().getAnnotation(WantsListen.class).phase();

            if (target.isCompatible(phase)) continue;

            ProtocolLibrary.getProtocolManager().removePacketListener(listener);
            it.remove();
            SheepWars.debugMessage("Unregistered " + listener.getClass().getName());
        }

        // Register
        for (final Class<? extends PacketListener> clz : registrable)
        {
            final WantsListen.Target target = clz.getAnnotation(WantsListen.class).phase();

            if (!target.isCompatible(phase)) continue;

            boolean shouldRegister = true;
            // Check if not already registered
            for (final PacketListener l : registered)
            {
                if (!l.getClass().equals(clz)) continue;

                shouldRegister = false;
                break;
            }
            if (!shouldRegister) continue;

            try
            {
                final PacketListener l = clz.getDeclaredConstructor().newInstance();

                registered.add(l);
                ProtocolLibrary.getProtocolManager().addPacketListener(l);
                SheepWars.debugMessage("Registered " + l.getClass().getName());
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
    }
}

