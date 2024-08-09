package org.ef3d0c3e.sheepwars.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WantsListen
{
    enum Target
    {
        None(0b000),
        Lobby(0b001),
        Game(0b010),
        End(0b100),
        Always(0b111);

        private int flag;

        /**
         * Gets whether two game phases are compatible
         * @param other Other game phase
         * @return true if both phase are compatible
         */
        public boolean isCompatible(final Target other)
        {
            return (other.flag & this.flag) != 0;
        }

        public String toBitSet() { return Integer.toBinaryString(flag); }

        Target(int flag)
        {
            this.flag = flag;
        }
    }


    Target phase();
}

