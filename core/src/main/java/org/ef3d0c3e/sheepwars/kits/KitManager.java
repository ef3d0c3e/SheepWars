package org.ef3d0c3e.sheepwars.kits;

import lombok.Getter;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public class KitManager {
    @Getter
    private static ArrayList<Kit> kits = new ArrayList<>();

    /**
     * Initializes all kits
     */
    public static void init()
    {
        final Reflections refl = new Reflections("org.ef3d0c3e.sheepwars");

        final Set<Class<? extends Kit>> classes = refl.getSubTypesOf(Kit.class);
        for (final Class<? extends Kit> clz : classes)
        {
            try {
                final var inst = (Kit)clz.getDeclaredConstructor().newInstance();
                kits.add(inst);
                SheepWars.debugMessage("Loaded Kit " + inst.getName());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface ForEachMap
    {
        void operation(final Kit kit);
    }

    /**
     * Executes lambda for all kits
     * @param f Lambda expression to execute on all kits
     */
    public static void forEach(final ForEachMap f)
    {
        for (final Kit kit : kits)
            f.operation(kit);
    }

    /**
     * Gets a kit by it's id
     * @param id The kit id
     * @return The kit, or null
     */
    public static @Nullable Kit getById(int id)
    {
        return kits.get(id);
    }
}
