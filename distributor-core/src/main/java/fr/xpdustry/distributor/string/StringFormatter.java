package fr.xpdustry.distributor.string;

import arc.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;


@FunctionalInterface
@SuppressWarnings("NullAway")
public interface StringFormatter{
    StringFormatter DEFAULT = String::format;

    StringFormatter MINDUSTRY = Strings::format;

    String format(@NonNull String string, @Nullable Object... args);
}
