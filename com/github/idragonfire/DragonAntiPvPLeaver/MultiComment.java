package com.github.idragonfire.DragonAntiPvPLeaver;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultiComment {
    String[] value();
}
