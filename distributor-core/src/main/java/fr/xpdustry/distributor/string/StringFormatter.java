package fr.xpdustry.distributor.string;

import arc.util.*;


@FunctionalInterface
public interface StringFormatter{
    StringFormatter DEFAULT = String::format;

    StringFormatter MINDUSTRY = Strings::format;

    String format(String string, Object... args);
}
