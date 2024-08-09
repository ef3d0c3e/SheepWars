package org.ef3d0c3e.sheepwars.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Location without world, useful for config
 */
@AllArgsConstructor
public class AnyLocation
{
    @Getter @Setter
    public double x, y, z;
    @Getter @Setter
    public float pitch, yaw;

    /**
     * Gets location from AnyLocation
     * @param world World
     * @return Location
     */
    public Location getLocation(final World world)
    {
        return new Location(world, x, y, z, pitch, yaw);
    }
}

