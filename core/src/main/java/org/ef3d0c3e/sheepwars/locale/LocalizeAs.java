package org.ef3d0c3e.sheepwars.locale;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LocalizeAs {
    boolean absolute() default false;

    String value();
}
